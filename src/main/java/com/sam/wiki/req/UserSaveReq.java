package com.sam.wiki.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserSaveReq {
    private Long id;

    @NotNull(message = "【用戶名】不能為空")
    private String loginName;

    @NotNull(message = "【暱稱】不能為空")
    private String name;

    @NotNull(message = "【密碼】不能為空")
    // @Length(min = 6, max = 20, message = "【密碼】6~20位")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密碼】至少包含 數字和英文，長度6-32")
    private String password;

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
        sb.append(", id=").append(id);
        sb.append(", loginName=").append(loginName);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}