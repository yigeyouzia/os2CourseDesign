package com.cyt.os.controller;


import com.cyt.os.common.Context;

/**
 * @author JuLiy
 * @date 2022/12/8 21:43
 */
public class RootController {
    public RootController() {
        String simpleName = this.getClass().getSimpleName();
        String name = simpleName.substring(0, simpleName.lastIndexOf("C"));
        Context.controllerMap.put(name, this);
    }
}
