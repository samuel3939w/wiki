package com.sam.wiki.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserLoginReq {

    @NotBlank(message = "【帳號】不能為空")
    private String loginName;

    @NotBlank(message = "【密碼】不能為空")
    // @Length(min = 6, max = 20, message = "【密碼】6~20位")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密碼】規則不正確")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", loginName=").append(loginName);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}