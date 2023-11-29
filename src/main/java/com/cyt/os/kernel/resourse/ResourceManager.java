package com.cyt.os.kernel.resourse;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author cyt
 * @date 2023/11/29 21:51
 */
public class ResourceManager {
    private final IntegerProperty resourceA;
    private final IntegerProperty resourceB;
    private final IntegerProperty resourceC;

    public ResourceManager() {
        this.resourceA = new SimpleIntegerProperty(10);
        this.resourceB = new SimpleIntegerProperty(10);
        this.resourceC = new SimpleIntegerProperty(10);
    }

    public int getResourceA() {
        return resourceA.get();
    }


    public int getResourceB() {
        return resourceB.get();
    }


    public int getResourceC() {
        return resourceC.get();
    }
}
