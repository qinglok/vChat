package me.linx.vchat.constants;


public enum CodeMap {

    Yes(0, ""),

    ErrorSys(1, "系统错误"),
    ErrorParameter(2, "参数错误"),

    ErrorEmailWasUsed(3, "邮箱已被注册"),

    ErrorEmailUnUsed(4, "该邮箱尚未注册"),
    ErrorPassword(5, "邮箱或密码错误"),

    ErrorTokenFailed(6, "请登录后操作"),

    ErrorFileEmpty(7, "文件为空"),
    ErrorFileUploadFailure(8, "上传失败"),

    ErrorLoggedOther(9, "已经在其他设备登录"),
    ErrorVerifySecret(10, "验证密保失败");

    public int value;
    public String msg;

    CodeMap(int value, String msg) {
        this.value = value;
        this.msg  = msg;
    }

}
