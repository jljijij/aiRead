package com.shanzha.web.rest.errors;

import com.shanzha.domain.CodeMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebGlobalAdvice {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<Object> handleBusinessException(BusinessException ex){
        CodeMsg codeMsg = ex.getCodeMsg();
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST) // 可根据业务定义具体状态码
            .body(codeMsg);
    }
}
