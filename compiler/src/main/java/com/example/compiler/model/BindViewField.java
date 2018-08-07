package com.example.compiler.model;

import com.example.annotation.BindView;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindViewField {

    private VariableElement mVariableElement;
    private int mresId;

    public BindViewField(VariableElement element) throws IllegalArgumentException{
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    BindView.class.getSimpleName()));
        }
        mVariableElement = element;

        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mresId = bindView.value();
    }
    /**
     * 获取变量名称
     * @return
     */
    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    /**
     * 获取变量id
     * @return
     */
    public int getResId() {
        return mresId;
    }

    /**
     * 获取变量类型
     * @return
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }
}