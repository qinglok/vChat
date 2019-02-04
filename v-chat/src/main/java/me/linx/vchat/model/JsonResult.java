package me.linx.vchat.model;


import me.linx.vchat.constants.CodeMap;

import javax.validation.constraints.NotNull;

public class JsonResult {
    private Integer code;
    private String msg;
    private Object data;

    public JsonResult() {
    }

    public JsonResult(CodeMap code) {
        this.code = code.value;
        this.msg = code.msg;
    }

    public JsonResult(Integer code) {
        this.code = code;
    }

    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResult(String msg) {
        this.msg = msg;
    }

    public JsonResult(Object data) {
        this.data = data;
    }

    public JsonResult(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }

    public JsonResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult(CodeMap codeMap, Object data) {
        this(codeMap.value, codeMap.msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static JsonResult success() {
        return new JsonResult(CodeMap.Yes.value);
    }

    public static JsonResult success(@NotNull Object data) {
        return new JsonResult(CodeMap.Yes.value, data);
    }

    public static JsonResult failure(@NotNull CodeMap codeMap) {
        return new JsonResult(codeMap);
    }

    public static JsonResult failure(@NotNull int code, @NotNull String msg) {
        return new JsonResult(code, msg);
    }

    public static JsonResult failure(@NotNull CodeMap codeMap, Object data) {
        return new JsonResult(codeMap, data);
    }
}
