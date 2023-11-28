package com.cyt.os.exception;


/**
 * 内存异常
 * @author cyt
 * @date 2023/11/28
 */

public class MemoryException extends RuntimeException {
    public MemoryException(String msg) {
        super(msg);
    }
}
