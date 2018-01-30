package com.alientome.editors.animations.dto;

public class PackageDTO {

    public final String packageName;
    public final String packageDirectory;

    public PackageDTO(String packageName, String packageDirectory) {
        this.packageName = packageName;
        this.packageDirectory = packageDirectory;
    }

    @Override
    public String toString() {
        return "Package[" + packageName + "/" + packageDirectory + "]";
    }
}
