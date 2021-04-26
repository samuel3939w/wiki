package com.sam.wiki.resp;

import javax.validation.constraints.NotBlank;

public class UserLoginResp {
    private Long id;

    @NotBlank(message = "【帳號】不能為空")
    private String loginName;

    @NotBlank(message = "【名稱】不能為空")
    private String name;

    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserLoginResp{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}