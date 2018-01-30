package com.alientome.editors.animations.dto;

public class ClassDTO {

    public final String packageName;
    public final String className;
    public final String classSubDirectory;

    public ClassDTO(String packageName, String className, String classSubDirectory) {
        this.packageName = packageName;
        this.className = className;
        this.classSubDirectory = classSubDirectory;
    }

    @Override
    public String toString() {
        return "Class[" + packageName + ":" + className + "/" + classSubDirectory + "]";
    }
}
