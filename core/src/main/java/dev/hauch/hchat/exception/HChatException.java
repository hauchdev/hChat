package dev.hauch.hchat.exception;

// class HChatException
public class HChatException extends RuntimeException {

    // make HChatException
    public HChatException(String message) {
        super(message);
    }

    // make HChatException
    public HChatException(String message, Throwable cause) {
        super(message, cause);
    }
}
