package com.example.compiler;

import com.squareup.javapoet.ClassName;

public class TypeUtil {
    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName ANDROID_ON_CLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName INJET = ClassName.get("com.example.api_module", "Inject");
    public static final ClassName PROVIDER = ClassName.get("com.example.api_module.provider","Provider");
}
