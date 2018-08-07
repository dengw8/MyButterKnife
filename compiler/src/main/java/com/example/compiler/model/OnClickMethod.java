package com.example.compiler.model;

import com.example.annotation.OnClick;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

public class OnClickMethod {
    private ExecutableElement mExecutableElement;
    private int[] resIds;
    private Name mMethodName;

    public OnClickMethod(ExecutableElement element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException(
                    String.format("Only methods can be annotated with @%s",
                            OnClick.class.getSimpleName()));
        }
        mExecutableElement = element;
        resIds = mExecutableElement.getAnnotation(OnClick.class).value();
        mMethodName = mExecutableElement.getSimpleName();
    }

    /**
     * 获取方法名称
     * @return
     */
    public Name getMethodName() {
        return mMethodName;
    }

    /**
     * 获取id数组
     * @return
     */
    public int[] getResIds() {
        return resIds;
    }
}
