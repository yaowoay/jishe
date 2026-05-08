# employment_warning_server.py
import warnings
warnings.filterwarnings("ignore")

import joblib
import pandas as pd
import numpy as np
from flask import Flask, request, jsonify

import os

# 获取当前脚本所在目录（resources/python/）
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# 加载模型
print("加载就业预警模型...")
model = joblib.load(os.path.join(BASE_DIR, "就业预警模型.pkl"))
preprocessor = joblib.load(os.path.join(BASE_DIR, "数据预处理器.pkl"))
print("模型加载完成")
app = Flask(__name__)

# 加载模型和预处理器
print("加载就业预警模型...")
model = joblib.load("就业预警模型.pkl")
preprocessor = joblib.load("数据预处理器.pkl")
print("模型加载完成")


@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "ok",
        "service": "employment-warning",
        "model_loaded": True
    })


def safe_convert(obj):
    """将numpy类型安全转换为Python原生类型"""
    if isinstance(obj, (np.bool_,)):
        return bool(obj)
    if isinstance(obj, (np.integer,)):
        return int(obj)
    if isinstance(obj, (np.floating,)):
        return float(obj)
    if isinstance(obj, np.ndarray):
        return obj.tolist()
    return obj


@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.json
        students = data.get('students', [])

        if not students:
            return jsonify({"error": "缺少学生数据"}), 400

        # 转换为DataFrame
        df = pd.DataFrame(students)
        print("接收到的数据列:", df.columns.tolist())

        # 使用预处理器转换特征
        if preprocessor:
            try:
                features = preprocessor.transform(df)
                print("预处理器转换成功")
            except Exception as e:
                print(f"预处理器转换失败: {e}")
                print("尝试手动特征工程...")
                features = manual_transform(df)
        else:
            features = manual_transform(df)

        # 预测
        probabilities = model.predict_proba(features)[:, 1]

        # 生成结果
        results = []
        for i, prob in enumerate(probabilities):
            prob_val = float(prob)
            if prob_val < 0.3:
                risk_level = "高风险"
                action = "立即干预"
            elif prob_val < 0.6:
                risk_level = "中风险"
                action = "重点关注"
            else:
                risk_level = "低风险"
                action = "常规跟踪"

            results.append({
                "index": i,
                "successProbability": round(prob_val, 4),
                "riskLevel": risk_level,
                "riskScore": round((1 - prob_val) * 100, 1),
                "needWarning": prob_val < 0.5,
                "recommendedAction": action
            })

        return jsonify({
            "success": True,
            "predictions": results,
            "total": len(results),
            "timestamp": pd.Timestamp.now().strftime("%Y-%m-%d %H:%M:%S")
        })

    except Exception as e:
        import traceback
        traceback.print_exc()
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500


def manual_transform(df):
    """手动特征工程"""
    X = df.copy()

    edu_map = {"专科": 0, "本科": 1, "硕士": 2, "博士": 3}

    X["学历_enc"] = X.get("学历", "本科").map(edu_map).fillna(1)
    X["实习_enc"] = (X.get("是否有实习经历", "否") == "是").astype(int)
    X["性别_enc"] = (X.get("性别", "男") == "男").astype(int)

    for col in ["专业", "工作城市", "就业行业"]:
        X[col + "_freq"] = 0.1

    salary = X.get("期望薪资", 8000)
    satisfaction = X.get("满意度评分", 7.0)

    X["薪资log"] = np.log1p(salary.astype(float))
    X["薪资平方"] = salary.astype(float) ** 2
    X["学历_实习"] = X["学历_enc"] * X["实习_enc"]
    X["薪资学历比"] = salary.astype(float) / (X["学历_enc"] + 1)
    X["满意实习"] = satisfaction.astype(float) * X["实习_enc"]
    X["薪资偏离"] = abs(salary.astype(float) - 8000)

    X["学历_专业热度"] = X["学历_enc"] * 0.1
    X["学历_城市热度"] = X["学历_enc"] * 0.1
    X["学历_行业热度"] = X["学历_enc"] * 0.1
    X["专业_行业"] = 0.01
    X["专业_城市"] = 0.01

    features = [
        "学历_enc", "实习_enc", "性别_enc",
        "期望薪资", "满意度评分", "薪资log", "薪资平方",
        "学历_实习", "薪资学历比", "满意实习", "薪资偏离",
        "专业_freq", "工作城市_freq", "就业行业_freq",
        "学历_专业热度", "学历_城市热度", "学历_行业热度",
        "专业_行业", "专业_城市"
    ]

    return X[features]


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=False)