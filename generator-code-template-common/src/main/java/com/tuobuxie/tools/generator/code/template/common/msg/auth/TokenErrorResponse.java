package com.tuobuxie.tools.generator.code.template.common.msg.auth;

import com.tuobuxie.tools.generator.code.template.common.constant.RestCodeConstants;
import com.tuobuxie.tools.generator.code.template.common.msg.BaseResponse;

/**
 * Created by ace on 2017/8/23.
 */
public class TokenErrorResponse extends BaseResponse {
    public TokenErrorResponse(String message) {
        super(RestCodeConstants.TOKEN_ERROR_CODE, message);
    }
}
