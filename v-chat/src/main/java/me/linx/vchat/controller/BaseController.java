package me.linx.vchat.controller;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.ResultEntity;

import javax.validation.constraints.NotNull;

public abstract class BaseController {

    protected ResultEntity success() {
        ResultEntity result = new ResultEntity();
        result.setCode(CodeMap.YES);
        return result;
    }

    protected ResultEntity success(@NotNull Object data) {
        ResultEntity result = new ResultEntity();
        result.setCode(CodeMap.YES);
        result.setData(data);
        return result;
    }

    protected ResultEntity failure(@NotNull int code) {
        ResultEntity result = new ResultEntity();
        result.setCode(code);
        return result;
    }

    protected ResultEntity failure(@NotNull int code, @NotNull Object data) {
        ResultEntity result = new ResultEntity();
        result.setCode(code);
        result.setData(data);
        return result;
    }

}
