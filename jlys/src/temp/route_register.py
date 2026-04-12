from flask import Blueprint, request, redirect, url_for, flash

from models.Applicant import Applicant
from models.Company import Company
from models.User import *  # 确保从models导入db实例和模型类

# 创建子蓝图
register_bp = Blueprint('register', __name__)

@register_bp.route('/register', methods=[ 'POST'])
def register():
    email = request.form['email']
    password = request.form['password']
    confirm_password = request.form['confirm_password']
    role = request.form['role']

    if password != confirm_password:
        flash('密码不一致，请重新输入。')
        return redirect(url_for('register'))

    if User.query.filter_by(email=email).first():
        flash('该邮箱已被注册，请使用其他邮箱。')
        return redirect(url_for('register'))

    if role == 'company':
        company_name = request.form['company_name']
        industry = request.form['industry']
        address = request.form['address']
        scale = request.form['scale']
        website = request.form['website']
        contact_phone = request.form['contact_phone']
        description = request.form['description']

        new_user = User(email=email, password=password, role=role)
        db.session.add(new_user)
        db.session.flush()
        new_company = Company(
            company_name=company_name,
            industry=industry,
            address=address,
            scale=scale,
            website=website,
            contact_phone=contact_phone,
            description=description,
            user_id=new_user.user_id
        )
        db.session.add(new_company)

    elif role == 'applicant':
        full_name = request.form['full_name']
        gender = request.form['gender']
        birthdate = request.form['birthdate']  # 获取字符串格式的日期
        education_level = request.form['education_level']
        work_years = request.form['work_years']
        expected_position = request.form['expected_position']
        expected_salary = request.form['expected_salary']

        # 将字符串日期转换为 datetime.date 对象

        print(f"Birthdate: {birthdate}, Type: {type(birthdate)}")

        new_user = User(email=email, password=password, role=role)
        db.session.add(new_user)
        db.session.flush()
        new_applicant = Applicant(
            full_name=full_name,
            gender=gender,
            birthdate=birthdate,  # 传递 datetime.date 对象
            education_level=education_level,
            work_years=work_years,
            expected_position=expected_position,
            expected_salary=expected_salary,
            user_id=new_user.user_id
        )
        db.session.add(new_applicant)

    else:
        flash('无效的角色，请重新选择。')
        return redirect(url_for('register'))

    try:
        db.session.commit()
        flash('注册成功！')
        return redirect(url_for('login'))
    except Exception as e:
        db.session.rollback()
        flash(f'注册失败：{str(e)}')
        return redirect(url_for('register'))


