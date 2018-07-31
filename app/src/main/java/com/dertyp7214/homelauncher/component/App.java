package com.dertyp7214.homelauncher.component;

import android.graphics.drawable.Drawable;

public class App {

    private final Drawable icon;
    private final String name, packageName;

    public App(Drawable icon, String name, String packageName) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }
}
