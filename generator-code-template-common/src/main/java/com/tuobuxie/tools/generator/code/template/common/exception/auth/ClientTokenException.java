package com.tuobuxie.tools.generator.code.template.common.exception.auth;


import com.tuobuxie.tools.generator.code.template.common.constant.CommonConstants;
import com.tuobuxie.tools.generator.code.template.common.exception.BaseException;

/**
 * Created by ace on 2017/9/10.
 */
public class ClientTokenException extends BaseException {
    public ClientTokenException(String message) {
        super(message, CommonConstants.EX_CLIENT_INVALID_CODE);
    }
}
