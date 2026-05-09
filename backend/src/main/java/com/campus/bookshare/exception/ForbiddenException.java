package com.campus.bookshare.exception;

import com.campus.bookshare.common.ResultCode;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ResultCode getResultCode() {
        return ResultCode.FORBIDDEN;
    }
}
