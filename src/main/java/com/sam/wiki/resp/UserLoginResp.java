package com.sam.wiki.resp;

import javax.validation.constraints.NotBlank;

public class UserLoginResp {
    private Long id;

    @NotBlank(message = "【帳號】不能為空")
    private String loginName;

    @NotBlank(message = "【名稱】不能為空")
    private String name;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append("]");
        return sb.toString();
    }
}