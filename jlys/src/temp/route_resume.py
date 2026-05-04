from flask import Blueprint, request, jsonify, session
from dashscope import Generation

from models.Applicant import Applicant
from models.Job import Job
from models.Resume import Resume
from models.Application import Application  # 导入Application模型
from models.ResumeScores import ResumeScores
from models import db
from models.Report import Report
import re
import os
from werkzeug.utils import secure_filename
import json
from openai import OpenAI
from collections import OrderedDict
import re
from datetime import datetime

resume_bp = Blueprint('resume', __name__)

# 确保临时目录存在
TARGET_DIR = r'D:\jingsai\ai-interview-master\uploads'
if not os.path.exists(TARGET_DIR):
    os.makedirs(TARGET_DIR)

@resume_bp.route('/upload_resume', methods=['POST'])
def upload_resume():
    """
    上传简历：本地→星火→解析→总结→入库
    （已对齐官方文档 /file/upload + /file/info + /file/chunks）
    """
    print("[DEBUG] 进入 upload_resume 函数")

    # ---------------- 1. 基础校验 ----------------
    if 'file' not in request.files:
        return jsonify(success=False, message='没有文件部分')

    file = request.files['file']
    if file.filename == '':
        return jsonify(success=False, message='没有选择文件')

    user_filename = request.form.get('storage_name')
    if not user_filename:
        return jsonify(success=False, message='没有提供文件名')

    # ---------------- 2. 本地保存 ----------------
    safe_filename = user_filename + os.path.splitext(file.filename)[1].lower()
    file_path = os.path.join(TARGET_DIR, safe_filename)
    file.save(file_path)

    # ---------------- 3. 上传到星火知识库 ----------------
    import time, hashlib, hmac, base64, requests

    APP_ID  = "ae56356f"
    API_SEC = "YzUxNjY0MWE5MDViNDk2ZTIzZTlmNTky"
    ts = str(int(time.time()))

    sign_origin = hashlib.md5((APP_ID + ts).encode()).hexdigest()
    signature = base64.b64encode(
        hmac.new(API_SEC.encode(), sign_origin.encode(), hashlib.sha1).digest()
    ).decode()

    headers = {"appId": APP_ID, "timestamp": ts, "signature": signature}

    with open(file_path, "rb") as f:
        resp = requests.post(
            "https://chatdoc.xfyun.cn/openapi/v1/file/upload",
            headers=headers,
            data={"fileType": "wiki", "parseType": "AUTO"},
            files={"file": (safe_filename, f, "application/pdf")}
        )
    print("[DEBUG] 星火上传返回：", resp.status_code, resp.text)

    if resp.status_code != 200 or resp.json().get("code") != 0:
        return jsonify(success=False, message=f"上传失败：{resp.text}")

    file_id = resp.json()["data"]["fileId"]

    # ---------------- 4. 等待向量化完成 ----------------
    for _ in range(60):          # 最多 2 分钟
        info_resp = requests.get(
            "https://chatdoc.xfyun.cn/openapi/v1/file/status",
            headers=headers,
            params={"fileIds": file_id}
        )
        data_arr = info_resp.json().get("data", [])
        status = data_arr[0]["fileStatus"] if data_arr else ""

        if status == "vectored":
            break
        if status == "failed":
            return jsonify(success=False, message="文件向量化失败")
        time.sleep(2)
    else:
        return jsonify(success=False, message="文件向量化超时")

    # ---------------- 5. 拉取全文文本 ----------------
    chunks_resp = requests.get(
        "https://chatdoc.xfyun.cn/openapi/v1/file/chunks",
        headers=headers,
        params={"fileId": file_id}
    )
    if chunks_resp.status_code != 200:
        return jsonify(success=False, message="拉取文本失败")

    chunks = chunks_resp.json().get("data", [])
    file_content = "\n".join(chunk.get("content", "") for chunk in chunks).strip()

    print("[DEBUG] file_content 前 200 字：", repr(file_content[:200]))

    if not file_content:
        return jsonify(success=False, message="解析后仍无文本，请确认文件可读或联系管理员")

    # ---------------- 6. 大模型总结 ----------------
    client = OpenAI(
        api_key="sk-0fb1011a6f404ea28251cc1480efc539",
        base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    )
    completion = client.chat.completions.create(
        model="qwen-plus",
        messages=[
            {"role": "system", "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手。"},
            {"role": "user", "content": file_content},
            {"role": "user", "content": "请以 JSON 格式总结该简历信息，只返回 JSON。"}
        ],
        temperature=0.3,
        response_format={"type": "json_object"}
    )
    data = json.loads(completion.choices[0].message.content)

    # ---------------- 7. 入库 ----------------
    user_id = session.get('user_id')
    if not user_id:
        return jsonify(success=False, message='用户未登录')

    applicant = Applicant.query.filter_by(user_id=user_id).first()
    if not applicant:
        return jsonify(success=False, message='用户不存在')

    resume = Resume(
        applicant_id=applicant.applicant_id,
        file_url=file_path,
        filename=safe_filename,
        parsed_data=json.dumps(data, ensure_ascii=False)
    )
    db.session.add(resume)
    db.session.commit()

    return jsonify(
        success=True,
        message='文件上传成功',
        file_url=file_path,
        resume_data=data
    )



@resume_bp.route('/get_resumes', methods=['GET'])
def get_resumes():
    user_id = session.get('user_id')
    if not user_id:
        print("User not logged in")  # 调试信息
        return jsonify({'success': False, 'message': '用户未登录'})

    try:
        applicant=Applicant.query.filter_by(user_id=user_id).first()
        applicant_id=applicant.applicant_id
        resumes = Resume.query.filter_by(applicant_id=applicant_id).all()
        resume_list = []
        for resume in resumes:
            resume_list.append({
                'id': resume.resume_id,
                'filename': resume.filename,
                'upload_date': resume.upload_date.isoformat(),
                'file_url': resume.file_url
            })
        return jsonify({'success': True, 'resumes': resume_list})
    except Exception as e:
        print(f"Error retrieving resumes: {e}")  # 调试信息
        return jsonify({'success': False, 'message': f'获取简历失败: {str(e)}'})


@resume_bp.route('/get_resumesbyUser', methods=['GET'])
def get_resumesbyUser():
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        applicant = Applicant.query.filter_by(user_id=user_id).first()
        applicant_id = applicant.applicant_id
        resumes = Resume.query.filter_by(applicant_id=applicant_id).all()
        resume_list = []
        for resume in resumes:
            report=Report.query.filter_by(resume_id=resume.resume_id).first()
            resume_list.append({
                'resume_id': resume.resume_id,
                'filename': resume.filename,
                'upload_date': resume.upload_date.isoformat(),
                'file_url': resume.file_url,
                'report': bool(report)  # 将report字段改为布尔类型
            })
        return jsonify({'success': True, 'resumes': resume_list})
    except Exception as e:
        print(f"Error retrieving resumes: {e}")
        return jsonify({'success': False, 'message': f'获取简历失败: {str(e)}'}), 500


@resume_bp.route('/analyze_resume', methods=['POST'])
def analyze_resume():
    global report_content
    resume_id = request.form.get('resume_id')

    job_id = request.form.get('job_id')

    model = request.form.get('model')

    print(resume_id, job_id, model)
    if not resume_id or not job_id or not model:
        return jsonify({'success': False, 'message': '缺少必要的参数'}), 400

    try:
        resume = Resume.query.filter_by(resume_id=resume_id).first()
        if not resume:
            return jsonify({'success': False, 'message': '简历不存在'}), 404

        job = Job.query.filter_by(job_id=job_id).first()
        if not job:
            return jsonify({'success': False, 'message': '岗位不存在'}), 404

        job_details = {
            'title': job.title,
            'job_type': job.job_type,
            'description': job.description,
            'requirements': job.requirements,
            'min_salary': job.min_salary,
            'max_salary': job.max_salary
        }
        job_info_json = job_details
        resume_json = resume.parsed_data
        print()
        print(job_info_json)
        print()
        print(resume_json)

        # 构建提示词
        prompt = f"""
        作为资深人才发展专家，请基于以下材料进行专业评估：

        候选人简历：
        {resume_json}

        目标岗位说明书：
        {job_info_json}

        请严格按照以下格式输出JSON格式的结构化分析报告，确保所有分数都在0-10之间：

        {{
            "assessment_metadata": {{
                "assessment_date": "当前日期",
                "assessment_model": "人岗匹配度评估模型v2.0",
                "industry_benchmark": "计算机信息技术服务业"
            }},
            "overall_evaluation": {{
                "total_score": {{
                    "score": "0-10分",
                    "grade": "S/A/B/C/D其中之一，对应关系：9-10分=S级(极其优秀)，8-8.9分=A级(优秀)，7-7.9分=B级(良好)，6-6.9分=C级(合格)，0-5.9分=D级(不合格)",
                    "conclusion": "总体评价结论"
                }},
                "dimension_scores": {{
                    "technical_match": "0-10",
                    "experience_match": "0-10",
                    "education_match": "0-10",
                    "potential_match": "0-10"
                }}
            }},
            "competency_analysis": {{
                "technical_skills": {{
                    "score": "0-10分",
                    "strengths": ["优势1", "优势2"],
                    "weaknesses": ["不足1", "不足2"],
                    "evidence": ["具体表现1", "具体表现2"],
                    "gap_analysis": "与岗位要求的差距分析"
                }},
                "project_experience": {{
                    "score": "0-10分",
                    "key_projects": [
                        {{
                            "project_name": "项目名称",
                            "relevance": "0-10分 - 与目标岗位的相关度",
                            "highlight": "项目亮点"
                        }}
                    ],
                    "experience_quality": "经验质量评价"
                }},
                "soft_skills": {{
                    "score": "0-10分",
                    "leadership": "领导力评分 0-10",
                    "communication": "沟通能力评分 0-10",
                    "teamwork": "团队协作评分 0-10",
                    "evidence": ["表现证据1", "表现证据2"]
                }}
            }},
            "salary_analysis": {{
                "market_position": {{
                    "current_range": "当前薪资范围",
                    "market_range": "市场薪资范围",
                    "position_percentage": "位于市场水平的百分位"
                }},
                "expectation_analysis": {{
                    "expectation_reasonability": "0-10分 - 期望薪资合理度",
                    "gap_with_budget": "与岗位预算的差距",
                    "suggestion": "薪资建议"
                }},
                "compensation_structure": {{
                    "base_salary": "基本工资建议",
                    "bonus": "奖金建议",
                    "other_benefits": ["其他福利1", "其他福利2"]
                }}
            }},
            "development_suggestions": {{
                "short_term": [
                    {{
                        "aspect": "改进方向",
                        "priority": "高/中/低",
                        "specific_actions": ["具体行动1", "具体行动2"],
                        "expected_outcome": "预期效果"
                    }}
                ],
                "long_term": {{
                    "career_path": ["职业发展路径1", "职业发展路径2"],
                    "key_milestones": ["里程碑1", "里程碑2"],
                    "required_skills": ["需要提升的技能1", "需要提升的技能2"]
                }},
                "training_recommendations": [
                    {{
                        "course": "推荐课程",
                        "priority": "优先级",
                        "benefit": "预期收益"
                    }}
                ]
            }}
        }}

        注意事项：
        1. 总评分必须在0-10分之间，并给出对应的等级评定
        2. 各维度分数必须客观公正，并提供具体的评分依据
        3. 能力分析要突出优势和不足
        4. 薪资分析要结合市场行情和岗位预算
        5. 发展建议要具体可行，并按优先级排序
        """
        # 调用通义千问模型
        client = OpenAI(
            api_key="sk-0fb1011a6f404ea28251cc1480efc539",
            base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
        )

        completion = client.chat.completions.create(
            model="qwen-plus",  # 使用通义千问模型
            messages=[
                {'role': 'system', 'content': 'You are a helpful assistant.'},
                {'role': 'user', 'content': prompt}  # 使用之前构建的提示词
            ]
        )

        content = completion.choices[0].message.content
        print("AI生成的内容:", content)  # 打印生成的内容以便调试

        # 提取有效的JSON部分
        try:
            json_start = content.index('{')  # 找到JSON开始的位置
            json_content = content[json_start:-3]  # 提取JSON内容
            # 调用 extract_and_store_scores 函数
            # resume.report = json_content  # 存储有效的JSON内容

            existing_report = Report.query.filter_by(job_id=job_id, resume_id=resume_id).first()
            if existing_report:
                # 更新现有记录
                existing_report.report = json_content
                # existing_report.updated_at = datetime.now()  # 如果有时间戳字段
            else:
                # 创建新记录
                existing_report = Report(
                    job_id=job_id,
                    resume_id=resume_id,
                    report=json_content,
                    # created_at=datetime.now()  # 如果有时间戳字段
                )
                db.session.add(existing_report)

            # 提交到数据库
            db.session.commit()

            json_pattern = r'({[\s\S]*})'
            match = re.search(json_pattern, json_content)

            if match:
                # 提取匹配到的JSON字符串
                json_str = match.group(1)

                # 清理JSON字符串
                json_str = json_str.strip()
                json_str = re.sub(r'[\n\t\r]+', '', json_str)
                json_str = re.sub(r'\s+(?=[^"]*"(?:[^"]*"[^"]*")*[^"]*$)', ' ', json_str)

                # 解析JSON
                json_obj = json.loads(json_str)

                # 提取所需字段
                total_score = json_obj['overall_evaluation']['total_score']['score']
                technical_match = json_obj['overall_evaluation']['dimension_scores']['technical_match']
                experience_match = json_obj['overall_evaluation']['dimension_scores']['experience_match']
                education_match = json_obj['overall_evaluation']['dimension_scores']['education_match']
                potential_match = json_obj['overall_evaluation']['dimension_scores']['potential_match']

                report=Report.query.filter_by(job_id=job_id, resume_id=resume_id).first()

                new_scores = ResumeScores(
                    report_id=report.report_id,
                    total_score=total_score,
                    tech_match=technical_match,
                    experience_match=experience_match,
                    education_match=education_match,
                    potential_match=potential_match
                )
                db.session.add(new_scores)
                db.session.commit()

        except ValueError:
            print("未找到有效的JSON内容")
            return jsonify({'success': False, 'message': '生成内容无效'}), 400


        db.session.commit()

        # 将简历 ID 存储到会话中
        session['resume_id'] = resume_id
        session['job_id']=job_id
        return jsonify({'success': True, 'message': '分析成功'}), 200
    except Exception as e:
        print(f"Error analyzing resume: {e}")
        return jsonify({'success': False, 'message': f'分析简历失败: {str(e)}'}), 500

@resume_bp.route('/resume_report', methods=['GET'])
def get_resume_report():
    try:
        resume_id = session.get('resume_id')
        job_id = session.get('job_id')
        if not job_id:
            return jsonify({'success': False, 'message': '未找到职位ID'}), 404
        if not resume_id:
            return jsonify({'success': False, 'message': '未找到简历ID'}), 404

        resume = Resume.query.filter_by(resume_id=resume_id).first()
        if not resume:
            return jsonify({'success': False, 'message': '简历不存在'}), 404

        report = Report.query.filter_by(resume_id=resume_id, job_id=job_id).first()
        if not report:
            return jsonify({'success': False, 'message': '报告不存在'}), 404
            
        report_content = report.report
        if not report_content:
            return jsonify({'success': False, 'message': '报告内容为空'}), 404

        try:
            import json
            import re

            # 使用正则表达式提取有效的JSON内容
            json_pattern = r'({[\s\S]*})'
            match = re.search(json_pattern, report_content)

            if match:
                # 提取匹配到的JSON字符串
                json_str = match.group(1)

                # 清理JSON字符串
                json_str = json_str.strip()
                json_str = re.sub(r'[\n\t\r]+', '', json_str)
                json_str = re.sub(r'\s+(?=[^"]*"(?:[^"]*"[^"]*")*[^"]*$)', ' ', json_str)

                # 解析JSON
                json_obj = json.loads(json_str)

                # 更新评估日期为当前时间
                current_time = datetime.now().strftime('%Y-%m-%d')
                json_obj['assessment_metadata']['assessment_date'] = current_time

                # 转换回JSON字符串
                clean_report = json.dumps(json_obj, ensure_ascii=False)

                return jsonify({'success': True, 'report': clean_report})
            else:
                print("未找到有效的JSON内容")
                return jsonify({'success': False, 'message': '报告格式错误：未找到有效的JSON内容'}), 500

        except json.JSONDecodeError as e:
            print(f"JSON解析错误: {e}")
            return jsonify({'success': False, 'message': f'报告格式错误：{str(e)}'}), 500
        except Exception as e:
            print(f"处理报告时出错: {e}")
            return jsonify({'success': False, 'message': f'处理报告失败：{str(e)}'}), 500

    except Exception as e:
        print(f"Error retrieving resume report: {e}")
        return jsonify({'success': False, 'message': f'获取简历报告失败: {str(e)}'}), 500


@resume_bp.route('/resume_submit', methods=['POST'])
def resume_submit():
    user_id = session.get('user_id')
    if not user_id:
        print("User not logged in")  # 调试信息
        return jsonify({'success': False, 'message': '用户未登录'})

    job_id = request.json.get('job_id')
    resume_id = request.json.get('resume_id')

    if not job_id or not resume_id:
        return jsonify({'success': False, 'message': '缺少必要的参数'}), 400

    # 检查 applicant_id 是否存在于 applicants 表中
    applicant = Applicant.query.filter_by(user_id=user_id).first()
    if not applicant:
        print(f"Applicant with user ID {user_id} does not exist")  # 调试信息
        return jsonify({'success': False, 'message': '用户不存在'}), 404

    applicant_id = applicant.applicant_id  # 获取 applicant_id

    try:
        # 检查是否已经存在相同的 job_id 和 applicant_id 组合
        existing_application = Application.query.filter_by(job_id=job_id, applicant_id=applicant_id).first()
        if existing_application:
            # 如果存在，返回提示信息
            return jsonify({
                'success': False,
                'message': '您已经为该职位提交过简历，请勿重复提交'
            }), 409  # 使用 409 Conflict 状态码表示冲突

        # 创建新的申请记录
        new_application = Application(
            job_id=job_id,
            applicant_id=applicant_id,
            resume_id=resume_id,
            status='已投递'
        )
        db.session.add(new_application)
        db.session.commit()

        return jsonify({'success': True, 'message': '简历投递成功'}), 200
    except Exception as e:
        print(f"Error submitting resume: {e}")
        return jsonify({'success': False, 'message': f'简历投递失败: {str(e)}'}), 500

@resume_bp.route('/get_parsed_data', methods=['GET'])
def get_parsed_data():
    resume_id = request.args.get('resume_id')
    if not resume_id:
        return jsonify({'success': False, 'message': '缺少必要的参数'}), 400

    try:
        resume = Resume.query.filter_by(resume_id=resume_id).first()
        if not resume:
            return jsonify({'success': False, 'message': '简历不存在'}), 404

        parsed_data = resume.parsed_data
        if not parsed_data:
            return jsonify({'success': False, 'message': '解析数据不存在'}), 404

        return jsonify({'success': True, 'parsed_data': parsed_data})
    except Exception as e:
        print(f"Error retrieving parsed data: {e}")
        return jsonify({'success': False, 'message': f'获取解析数据失败: {str(e)}'}), 500

@resume_bp.route('/analyze_resumeReport', methods=['POST'])
def analyze_resumeReport():
    try:
        data = request.get_json()
        resume_id = data.get('resume_id')
        job_id = data.get('job_id')

        if not resume_id:
            return jsonify({'success': False, 'message': '缺少简历ID'}), 400
            
        # 如果没有提供job_id，可以使用默认值或清除之前的值
        if not job_id:
            # 查询该简历是否有关联的职位报告
            reports = Report.query.filter_by(resume_id=resume_id).first()
            if reports:
                job_id = reports.job_id
            else:
                return jsonify({'success': False, 'message': '未找到与该简历关联的职位ID'}), 400

        # 将 resume_id 和 job_id 存储到会话中
        session['resume_id'] = resume_id
        session['job_id'] = job_id
        print(session)

        return jsonify({'success': True, 'message': '简历ID已存储'}), 200
    except Exception as e:
        print(f"Error storing resume ID: {e}")
        return jsonify({'success': False, 'message': f'存储简历ID失败: {str(e)}'}), 500


@resume_bp.route('/get_submitted_jobs', methods=['GET'])
def get_submitted_jobs():
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        # 获取用户已投递的职位ID列表
        submitted_jobs = Application.query.filter_by(applicant_id=user_id).all()
        job_ids = [job.job_id for job in submitted_jobs]
        return jsonify({'success': True, 'job_ids': job_ids})
    except Exception as e:
        print(f"Error retrieving submitted jobs: {e}")
        return jsonify({'success': False, 'message': f'获取已投递职位失败: {str(e)}'}), 500









