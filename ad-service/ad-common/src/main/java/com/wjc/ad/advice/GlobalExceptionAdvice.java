package com.wjc.ad.advice;

import com.wjc.ad.exception.AdException;
import com.wjc.ad.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常拦截
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = AdException.class)
    public CommonResponse<String> handlerAdException(HttpServletRequest request, AdException ex){
        CommonResponse<String> response = new CommonResponse<>(-1, "system error");
        response.setData(ex.getMessage());
        return response;
    }
}
