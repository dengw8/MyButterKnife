package com.example.api_module;

import android.app.Activity;

import com.example.api_module.provider.ActivityProvider;

import java.util.HashMap;
import java.util.Map;

public class MyButterKnife {

    // 统一编译时生成类的类名后缀为 $$ViewInject
    private static final String SUFFIX = "$$ViewInject";

    // 保存已经加载的代理类，避免多次反射
    private static Map<String, Inject<?>> proxyActivityMap = new HashMap<>();

    // 提供给Activity使用
    public static void injectView(Activity activity) {
        injectView(activity, activity);
    }

    public static void injectView(Object object, Object root) {
        Inject proxyActivity = findProxyActivity(object);
        proxyActivity.inject(object, root, new ActivityProvider());
    }

    private static Inject findProxyActivity(Object obj) {
        String className = obj.getClass().getName();
        Inject proxyActivity = proxyActivityMap.get(className);
        if(proxyActivity == null) {
            try {
                // 拼接产生的代理类的类名，然后通过反射产生代理类
                Class<?> injectorClazz = Class.forName(className + SUFFIX);
                proxyActivity = (Inject) injectorClazz.newInstance();
                proxyActivityMap.put(className, proxyActivity);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return proxyActivity;
    }
}
