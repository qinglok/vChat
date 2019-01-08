package me.linx.vchat.controller;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.ResultEntity;

import javax.validation.constraints.NotNull;

public abstract class BaseController {

    protected ResultEntity success() {
        ResultEntity result = new ResultEntity();
        result.setCode(CodeMap.Yes.value);
        return result;
    }

    protected ResultEntity success(@NotNull Object data) {
        ResultEntity result = new ResultEntity();
        result.setCode(CodeMap.Yes.value);
        result.setData(data);
        return result;
    }

    protected ResultEntity failure(@NotNull CodeMap codeMap) {
        ResultEntity result = new ResultEntity();
        result.setCode(codeMap.value);
        result.setMsg(codeMap.msg);
        return result;
    }

    protected ResultEntity failure(@NotNull int code, @NotNull String msg) {
        ResultEntity result = new ResultEntity();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

}
