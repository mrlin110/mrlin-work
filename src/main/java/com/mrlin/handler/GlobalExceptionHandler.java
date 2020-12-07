package com.mrlin.handler;

import com.mrlin.commons.domain.ResultInfo;
import com.mrlin.commons.domain.ResultResponse;
import com.mrlin.commons.exception.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice // 将输出的内容写入 ResponseBody 中
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(ParameterException.class)
    public ResultInfo<Map<String, String>> handlerParameterException(ParameterException ex) {
        ResultInfo<Map<String, String>> resultInfo =
                ResultResponse.buildError(ex.getErrorCode(), ex.getMessage());
        return resultInfo;
    }

    @ExceptionHandler(Exception.class)
    public ResultInfo<Map<String, String>> handlerException(Exception ex) {
        log.info("未知异常：{}", ex);
        ResultInfo<Map<String, String>> resultInfo =
                ResultResponse.buildError();
        return resultInfo;
    }

}
