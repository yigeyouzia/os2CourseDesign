package com.cyt.os.exception;

/**
 * 选中不是page
 *
 * @author cyt
 * @date 2023/12/11 20:58
 */
public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(String msg) {
        super(msg);
    }
}
