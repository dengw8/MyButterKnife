package com.example.compiler;

import com.example.annotation.BindView;
import com.example.annotation.OnClick;
import com.example.compiler.model.BindViewField;
import com.example.compiler.model.OnClickMethod;
import com.example.compiler.model.ProxyClassInfo;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    /**
     * 文件相关的辅助类
     **/
    private Filer mFiler;
    /**
     * 日志打印类
     * */
    private Messager mMessager;
    /**
     * 元素工具类
     * */
    private Elements mElements;
    /**
     * 保存所有的要生成的注解文件信息
     * */
    private Map<String, ProxyClassInfo> mProxyMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
        mProxyMap = new HashMap<>();
        // 在这里打印gradle文件传进来的参数
        Map<String, String> map = processingEnv.getOptions();
        for (String key : map.keySet()) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "key" + "：" + map.get(key));
        }
    }

    /**
     * 此方法用来设置支持的注解类型，没有设置的无效（获取不到）
     * */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        // 把支持的类型添加进去
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(OnClick.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();

        // 获取全部的带有BindView 注解的Element
        Set<? extends Element> bindViewAnnotations = roundEnv.getElementsAnnotatedWith(BindView.class);
        for(Element element : bindViewAnnotations ) {
            // 强转成属性元素
            VariableElement variableElement = (VariableElement) element;
            // 我们知道属性元素的外层一定是类元素
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            // 获取类元素的类名
            String className = typeElement.getQualifiedName().toString();
            ProxyClassInfo proxyClassInfo = mProxyMap.get(className);
            if(proxyClassInfo == null) {
                proxyClassInfo = new ProxyClassInfo(typeElement, mElements);
                mProxyMap.put(className, proxyClassInfo);
            }
            proxyClassInfo.addField(new BindViewField(variableElement));
        }

        // 获取全部 OnClick 注解
        Set<? extends Element> onClickAnnotations = roundEnv.getElementsAnnotatedWith(OnClick.class);
        for(Element element : onClickAnnotations) {
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            String className = typeElement.getQualifiedName().toString();
            ProxyClassInfo proxyClassInfo = mProxyMap.get(className);
            if(proxyClassInfo == null) {
                proxyClassInfo = new ProxyClassInfo(typeElement, mElements);
                mProxyMap.put(className, proxyClassInfo);
            }
            proxyClassInfo.addMethod(new OnClickMethod(executableElement));
        }

        // 循环生成源代理类
        for(String key : mProxyMap.keySet()) {
            ProxyClassInfo proxyClassInfo = mProxyMap.get(key);
            try {
                proxyClassInfo.generateFile().writeTo(mFiler);
            } catch (IOException e) {
                error("Generate file failed, reason: %s", e.getMessage());
            }
        }
        return true;
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }
}
