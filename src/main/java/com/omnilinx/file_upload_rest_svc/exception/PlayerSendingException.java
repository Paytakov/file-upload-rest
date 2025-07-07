package com.omnilinx.file_upload_rest_svc.exception;

public class PlayerSendingException extends RuntimeException {

    public PlayerSendingException(String message, Throwable cause) {
        super(message, cause);
    }
    public PlayerSendingException(String message) {
        super(message);
    }

}
