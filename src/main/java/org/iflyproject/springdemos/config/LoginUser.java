package org.iflyproject.springdemos.config;


import javax.security.auth.Subject;
import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

/**
 * 用户信息
 *
 * @author ruoyi
 */
public class LoginUser implements Serializable, Principal {
    private static final long serialVersionUID = 1L;


    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
