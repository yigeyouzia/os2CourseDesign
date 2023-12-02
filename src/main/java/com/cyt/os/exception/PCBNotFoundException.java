package com.cyt.os.exception;

/**
 * @author cyt
 * @date 2023/12/1 15:01
 */
public class PCBNotFoundException extends RuntimeException {
    public PCBNotFoundException() {
        super("未找到相应的PCB");
    }
}
