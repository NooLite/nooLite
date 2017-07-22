package com.noolite;

/**
 * Created by urix on 7/22/2017.
 */

public class AppException extends RuntimeException {
    private ResultType resultType;

    public AppException(ResultType resultType) {
        this.resultType = resultType;
    }

    public AppException(String detailMessage, Throwable throwable, ResultType resultType) {
        super(detailMessage, throwable);
        this.resultType = resultType;
    }

    public ResultType getResultType() {
        return resultType;
    }
}
