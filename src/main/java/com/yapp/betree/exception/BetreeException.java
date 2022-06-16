package com.yapp.betree.exception;

public class BetreeException extends RuntimeException {

    private final ErrorCode code;

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public ErrorCode getCode() {
        return this.code;
    }

    public BetreeException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public BetreeException(ErrorCode code, String message) {
        super(code.getMessage() + ": " + message);
        this.code = code;
    }
}
