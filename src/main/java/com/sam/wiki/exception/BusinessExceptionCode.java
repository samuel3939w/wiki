package com.sam.wiki.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("帳號名已存在"),
    LOGIN_USER_ERROR("用戶名不存在或密碼錯誤"),
    VOTE_REPEAT("您已點讚過"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

