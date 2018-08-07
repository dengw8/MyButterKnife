package com.example.api_module.provider;

import android.content.Context;
import android.view.View;

public interface Provider {
    Context getContext(Object object);

    View findView(Object object, int id);
}
