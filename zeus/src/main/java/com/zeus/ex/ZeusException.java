package com.zeus.ex;

/**
 * Created by tianyang on 18/5/24.
 */
public class ZeusException extends Exception {

    public ZeusException() {
    }

    public ZeusException(String detailMessage) {
        super(detailMessage);
    }

    public ZeusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ZeusException(Throwable throwable) {
        super(throwable);
    }
}
