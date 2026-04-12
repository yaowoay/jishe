from flask import Blueprint, jsonify, render_template, request, session

from models import db
from models.Application import Application
from models.Applicant import Applicant
from models.Company import Company
from models.Job import Job
from models.Report import Report
from models.Resume import Resume
from models.User import User
from models.ResumeScores import ResumeScores
from models.AIInterview import AIInterview
from models.InterviewScores import InterviewScores

applicant_bp = Blueprint('applicantion', __name__)


@applicant_bp.route('/get_application', methods=['GET'])
# @applicant_bp.route('/company/send_invitation/<int:application_id>', methods=['POST'])
def get_application():
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        applicant = Applicant.query.filter_by(user_id=user_id).first()
        if not applicant:
            return jsonify({'success': False, 'message': '用户未找到对应的申请人'}), 404

        applicant_id = applicant.applicant_id

        recent_interviews = (
            db.session.query(
                Application.application_id,
                Job.title,
                Company.company_name,
                Application.apply_time,
                Application.status,
                AIInterview.overall_score  # ✅ 换成 AIInterview 的总分
            )
            .join(Job, Application.job_id == Job.job_id)
            .join(Company, Job.company_id == Company.company_id)
            .outerjoin(AIInterview, AIInterview.application_id == Application.application_id)  # ✅ 关键 JOIN
            .filter(Application.applicant_id == applicant_id)
            .order_by(Application.apply_time.desc())
            .limit(5)
            .all()
        )

        interviews = []
        for iv in recent_interviews:
            interviews.append({
                'application_id': iv.application_id,
                'job_title': iv.title,
                'company_name': iv.company_name,
                'apply_time': iv.apply_time.isoformat(),
                'status': iv.status,
                'ai_evaluation_score': iv.overall_score  # ✅ 直接取 AIInterview 的分数
            })

        return jsonify({'success': True, 'recent_interviews': interviews}), 200
    except Exception as e:
        print(f"Error retrieving applications: {e}")
        return jsonify({'success': False, 'message': '获取面试情况失败'}), 500


# 添加新的路由以获取用户的全部面试情况
@applicant_bp.route('/get_all_interviews', methods=['GET'])
def get_all_interviews():
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    # 通过user_id查询applicant_id
    applicant = Applicant.query.filter_by(user_id=user_id).first()
    if not applicant:
        return jsonify({'success': False, 'message': '用户未找到对应的申请人'}), 404

    applicant_id = applicant.applicant_id

    # 获取用户的全部面试情况并优化查询
    all_interviews = (
        Application.query.filter_by(applicant_id=applicant_id)
        .join(Job, Application.job_id == Job.job_id)  # 使用join减少查询次数
        .join(Company, Job.company_id == Company.company_id)  # 添加join以获取公司信息
        .order_by(Application.apply_time.desc())
        .with_entities(
            Application.application_id,
            Job.title,
            Job.expiration_date,  # 添加expiration_date
            Company.company_name,  # 添加company_name
            Application.apply_time,
            Application.status,
            Application.ai_evaluation_score
        )
        .all()
    )

    interviews = [
        {
            'application_id': interview.application_id,
            'job_title': interview.title,
            'expiration_date': interview.expiration_date.isoformat() if interview.expiration_date else None,  # 格式化日期
            'company_name': interview.company_name,
            'apply_time': interview.apply_time.isoformat(),
            'status': interview.status,
            'ai_evaluation_score': interview.ai_evaluation_score
        }
        for interview in all_interviews
    ]

    return jsonify({'success': True, 'all_interviews': interviews}), 200


@applicant_bp.route('/job_detail/<int:job_id>', methods=['GET'])
def job_detail(job_id):
    user_id = session.get('user_id')
    session['job_id'] = job_id
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        job = Job.query.filter_by(job_id=job_id).first()
        if not job:
            return jsonify({'success': False, 'message': '职位未找到'}), 404

        applications = Application.query.filter_by(job_id=job_id).all()
        applicants_info = []

        for application in applications:
            applicant = Applicant.query.filter_by(applicant_id=application.applicant_id).first()
            resume = Resume.query.filter_by(resume_id=application.resume_id).first()
            job = Job.query.filter_by(job_id=application.job_id).first()
            report = Report.query.filter_by(job_id=application.job_id, resume_id=application.resume_id).first()

            # 检查是否存在报告，如果不存在则返回错误
            if report is None:
                return jsonify({'success': False, 'message': '用户必须先进行简历分析才能投递岗位'}), 400

            print(report.report_id)

            resume_scores = ResumeScores.query.filter_by(report_id=report.report_id).first()
            # 通过 user_id 获取 User 对象
            user = User.query.filter_by(user_id=applicant.user_id).first()

            applicant_info = {
                'applicant_id': applicant.applicant_id,
                'full_name': applicant.full_name,
                'resume': resume.resume_id,
                'status': application.status,
                'application_id': application.application_id,
            }

            if resume_scores:
                applicant_info.update({
                    'total_score': resume_scores.total_score,
                    'tech_match': resume_scores.tech_match,
                    'experience_match': resume_scores.experience_match,
                    'education_match': resume_scores.education_match,
                    'potential_match': resume_scores.potential_match,
                })
                # applicant_info['total_score'] = resume_scores.total_score
                # applicant_info['tech_match'] = resume_scores.tech_match
                # applicant_info['experience_match'] = resume_scores.experience_match
                # applicant_info['education_match'] = resume_scores.education_match
                # applicant_info['potential_match'] = resume_scores.potential_match

            applicants_info.append(applicant_info)

        return render_template('company/manage_interview.html', job=job, applicants=applicants_info), 200

    except Exception as e:
        print(f"Error retrieving job details: {e}")
        return jsonify({'success': False, 'message': '获取职位详情失败'}), 500


@applicant_bp.route('/mark_viewed/<int:application_id>', methods=['POST'])
def mark_viewed(application_id):
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        # 获取申请记录
        application = Application.query.filter_by(application_id=application_id).first()
        if not application:
            return jsonify({'success': False, 'message': '申请记录未找到'}), 404

        # # 检查申请记录是否属于当前用户
        # applicant = Applicant.query.filter_by(applicant_id=application.applicant_id).first()
        # if not applicant or applicant.user_id != user_id:
        #     return jsonify({'success': False, 'message': '无权限访问此申请记录'}), 403

        # 更新申请状态为"已查看"
        application.status = '已查看'
        db.session.commit()

        return jsonify({'success': True, 'message': '状态已更新为已查看'}), 200
    except Exception as e:
        print(f"Error marking application as viewed: {e}")
        return jsonify({'success': False, 'message': '标记申请为已查看失败'}), 500


@applicant_bp.route('/send_invitation/<int:application_id>', methods=['POST'])
def send_invitation(application_id):
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:
        # 获取申请记录
        application = Application.query.filter_by(application_id=application_id).first()
        if not application:
            return jsonify({'success': False, 'message': '申请记录未找到'}), 404

        # # 检查申请记录是否属于当前用户
        # applicant = Applicant.query.filter_by(applicant_id=application.applicant_id).first()
        # if not applicant or applicant.user_id != user_id:
        #     return jsonify({'success': False, 'message': '无权限访问此申请记录'}), 403

        # 这里可以添加发送面试邀请的逻辑，例如发送邮件或更新状态
        application.status = '面试中'
        db.session.commit()

        return jsonify({'success': True, 'message': '面试邀请已发送'}), 200
    except Exception as e:
        print(f"Error sending invitation: {e}")
        return jsonify({'success': False, 'message': '发送面试邀请失败'}), 500


@applicant_bp.route('/reject/<int:application_id>', methods=['POST'])
def reject(application_id):
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401
    try:
        # 获取申请记录
        application = Application.query.filter_by(application_id=application_id).first()
        if not application:
            return jsonify({'success': False, 'message': '申请记录未找到'}), 404
        application.status = '拒绝'
        db.session.commit()
        return jsonify({'success': True, 'message': '已拒绝'}), 200
    except Exception as e:
        print(f"Error rejecting invitation: {e}")
        return jsonify({'success': False, 'message': '拒绝失败'}), 500


@applicant_bp.route('/accept/<int:application_id>', methods=['GET'])
def accept(application_id):
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401
    try:
        # 获取申请记录
        application = Application.query.filter_by(application_id=application_id).first()
        if not application:
            return jsonify({'success': False, 'message': '申请记录未找到'}), 404
        application.status = '录用'
        db.session.commit()
        return jsonify({'success': True, 'message': '已录用'}), 200
    except Exception as e:
        print(f"Error rejecting invitation: {e}")
        return jsonify({'success': False, 'message': '录用失败'}), 500


@applicant_bp.route('/applicant/interview-room', methods=['GET'])
def interview_room():
    user_id = session.get('user_id')
    if not user_id:
        return jsonify({'success': False, 'message': '用户未登录'}), 401

    try:

        # 假设 application_id 从请求参数中获取
        application_id = request.args.get('application_id')
        if not application_id:
            return jsonify({'success': False, 'message': '未找到面试记录'}), 404

        # 将 application_id 存储到会话中
        session['application_id'] = application_id
        print(session)
        return jsonify({'success': True, 'message': '成功'}), 200

    except Exception as e:
        print(f"Error retrieving interview room data: {e}")
        return jsonify({'success': False, 'message': '获取面试房间数据失败'}), 500


@applicant_bp.route('/candidates', methods=['GET'])
def get_candidates():
    try:
        job_id = request.args.get('job_id')
        show_all = request.args.get('show_all', 'false').lower() == 'true'

        if not job_id:
            return jsonify({'success': False, 'message': '缺少职位ID参数'}), 400

        # 1. 查询符合条件的申请记录
        applications = db.session.query(Application).filter(
            Application.job_id == job_id,
            Application.status == '待定'
        ).all()

        candidates_data = []

        for application in applications:
            try:
                # 2. 查询面试信息
                interview = db.session.query(AIInterview).filter(
                    AIInterview.application_id == application.application_id
                ).order_by(AIInterview.interview_id.desc()).first()

                if not interview or interview.overall_score is None:
                    continue

                if not show_all and float(interview.overall_score) < 60:
                    continue

                # 3. 查询面试评分
                interview_scores = db.session.query(InterviewScores).filter(
                    InterviewScores.interview_id == interview.interview_id
                ).first()

                if not interview_scores:
                    continue

                # 4. 查询申请人信息
                applicant = db.session.query(Applicant).filter(
                    Applicant.applicant_id == application.applicant_id
                ).first()

                if not applicant:
                    continue

                # 5. 查询职位信息
                job = db.session.query(Job).filter(
                    Job.job_id == application.job_id
                ).first()

                if not job:
                    continue

                # 构建候选人数据
                candidate_data = {
                    'applicant_id': applicant.applicant_id,
                    'application_id': application.application_id,
                    'full_name': applicant.full_name,
                    'job_title': job.title,
                    'total_score': float(interview.overall_score),
                    'technical_ability': float(interview_scores.technical_ability),
                    'learning_ability': float(interview_scores.learning_ability),
                    'team_collaboration': float(interview_scores.team_collaboration),
                    'problem_solving': float(interview_scores.problem_solving),
                    'communication_expression': float(interview_scores.communication_expression),
                    'apply_time': application.apply_time.strftime('%Y-%m-%d %H:%M'),
                    'education': applicant.education_level,
                    'experience_years': applicant.work_years,
                    'interview_time': interview.start_time.strftime('%Y-%m-%d %H:%M') if interview.start_time else None
                }

                candidates_data.append(candidate_data)

            except Exception as e:
                print(f"Error processing application {application.application_id}: {e}")
                continue

        # 按总分降序排序
        candidates_data.sort(key=lambda x: x['total_score'], reverse=True)

        return render_template('company/candidates.html',
                               candidates=candidates_data,
                               job_id=job_id,
                               show_all=show_all)

    except Exception as e:
        print(f"Error getting candidates: {e}")
        return jsonify({'success': False, 'message': '获取候选人列表失败'}), 500

