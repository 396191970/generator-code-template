package com.tuobuxie.tools.generator.code.template.common.msg.auth;

import com.tuobuxie.tools.generator.code.template.common.constant.RestCodeConstants;
import com.tuobuxie.tools.generator.code.template.common.msg.BaseResponse;

/**
 * Created by ace on 2017/8/25.
 */
public class TokenForbiddenResponse  extends BaseResponse {
    public TokenForbiddenResponse(String message) {
        super(RestCodeConstants.TOKEN_FORBIDDEN_CODE, message);
    }
}
