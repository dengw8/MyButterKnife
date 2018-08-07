package com.example.api_module;

import com.example.api_module.provider.Provider;

public interface Inject<T> {
    void inject(T target, Object source, Provider provider);
}
