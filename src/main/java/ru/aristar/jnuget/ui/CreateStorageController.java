package ru.aristar.jnuget.ui;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author sviridov
 */
@ManagedBean(name = "createStorage")
@RequestScoped
public class CreateStorageController {

    private String className;
    private boolean indexed = false;

    public String addStorage() {
        System.out.println("className=" + className + "; indexed=" + indexed);
        return null;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }
}
