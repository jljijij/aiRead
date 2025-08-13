package com.shanzha.web.rest.errors;

import com.shanzha.domain.CodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException {

    private CodeMsg codemsg;

    public CodeMsg getCodeMsg() {
        return codemsg;
    }
}
