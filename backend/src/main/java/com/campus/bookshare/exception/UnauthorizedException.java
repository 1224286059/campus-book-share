package com.campus.bookshare.exception;

import com.campus.bookshare.common.ResultCode;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public ResultCode getResultCode() {
        return ResultCode.UNAUTHORIZED;
    }
}
