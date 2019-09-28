package com.chen.communit.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"该问题不存在，要不换个试试~"),
    TARGET_PARENT_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登陆，请登陆后重试"),
    SYSTEM_ERROR(2004,"服务冒烟了，要不你稍后再试试~"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你操作的评论不存在，要不换个试试~"),
    REQUEST_ERROR(2007,"你发送的请求没找到，要不换个试试！"),
    CONTENT_IS_EMPTY(2008,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2009,"兄弟你这是读别人的信息呢?"),
    NOTIFICATION_NOT_FOUND(2010,"消息莫非是不翼而飞了？"),
    FILE_UPLOAD_FAIL(2011,"图片上传失败~");

    private Integer code;
    private String message;

    CustomizeErrorCode( Integer code,String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
