package com.example.compiler.model;

import com.example.compiler.TypeUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ProxyClassInfo {
    /**
     * 注解修饰的元素
     **/
    private TypeElement mTypeElement;
    /**
     * 相关的辅助类
     **/
    private Elements mElements;
    /**
     * 保存了所有的 BindView 注解的信息
     **/
    private List<BindViewField> mFields = new ArrayList<>();
    /**
     * 保存了所有的 OnClick 注解的信息
     **/
    private List<OnClickMethod> mMethods = new ArrayList<>();
    /*
     * 统一编译时生成类的类名后缀为 $$ViewInject
     */

    private static final String SUFFIX = "$$ViewInject";

    public ProxyClassInfo(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElements = elements;
    }


    public void addField(BindViewField field) {
        mFields.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }
    public JavaFile generateFile() {
        // 要在代理类中生成的方法
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.PROVIDER,"provider");

        for(BindViewField field : mFields) {
            // find views
            injectMethod.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                    field.getFieldName(),
                    ClassName.get(field.getFieldType()), field.getResId());
        }

        for(OnClickMethod method : mMethods) {
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
                            .addStatement("host.$N()", method.getMethodName())
                            .build())
                    .build();
            injectMethod.addStatement("View.OnClickListener listener = $L ", listener);
            for (int id : method.getResIds()) {
                // set listeners
                injectMethod.addStatement("provider.findView(source, $L).setOnClickListener(listener)", id);
            }
        }

        // 生成的代理类
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJET, TypeName.get(mTypeElement.asType())))
                .addMethod(injectMethod.build())
                .build();

        String packgeName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();

        return JavaFile.builder(packgeName, injectClass).build();
    }
}
