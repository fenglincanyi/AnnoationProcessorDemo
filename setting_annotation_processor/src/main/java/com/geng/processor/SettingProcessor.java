package com.geng.processor;


import com.geng.annotation.Bean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by gengjiarong
 * on 2017/12/3.
 * <p>
 * init():
 * gives you paintbrushes to start painting. Filer(to generate file), Messager(debugging), Utility classes.
 * You can get these classes with processing environment.
 * <p>
 * process():
 * brain of your processor. Starts rounding and gives you annotated classes, methods, fields, annotation etc.
 * It gives you all annotated elements here. And you start doing all calculation and generate your new class file here.
 * <p>
 * getSupportedAnnotationTypes():
 * We return only our custom annotation set in this method. We can say that return value of this
 * method will be given to us as process method’s first parameter.
 * <p>
 * getSupportedSourceVersion():
 * We always return latest java version.
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.geng.annotation.Bean") // 支持的注解的全路径
public class SettingProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
//    private Field[] fields;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 拿到打了注解的类元素
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Bean.class);

        if (!elements.isEmpty()) {
            Element e = elements.iterator().next();


            if (e.getKind() != ElementKind.CLASS) {
                error(e, "annotation kind not CLASS");
                return true;
            }

            // 拿到 class
//            Class clazz = e.getClass();
//            fields = clazz.getDeclaredFields();

            Bean anno = e.getAnnotation(Bean.class);
            generateJavaBean(anno);
        }

        return true;
    }

    private void generateJavaBean(Bean anno) {
        TypeSpec clazz = buildClazz(anno);
        JavaFile javaFile = JavaFile.builder("com.geng.annoationprocessordemo", clazz).build();
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(javaFile.toString());
    }

    private TypeSpec buildClazz(Bean anno) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(anno.clzName());

        builder.addModifiers(Modifier.PUBLIC);
        builder.addSuperinterface(Serializable.class);
        builder.addField(buildFiledSerializableID());

        if (anno.attName().length != anno.typeName().length) {
            messager.printMessage(ERROR, "annotation attName size not equal typeName size");
            return null;
        }

        for (int i = 0; i < anno.attName().length; i++) {
            String tmpName = anno.attName()[i].substring(0, 1).toUpperCase() + anno.attName()[i].substring(1);

            Class attClass = null;
            switch (anno.typeName()[i]) {
                case INT:
                    attClass = int.class;
                    break;
                case FLOAT:
                    attClass = float.class;
                    break;
                case DOUBLE:
                    attClass = double.class;
                    break;
                case STRING:
                    attClass = String.class;
                    break;
            }

            // 字段
            builder.addField(attClass, anno.attName()[i], Modifier.PUBLIC);
            // get 方法
            builder.addMethod(buildGetMethod("get" + tmpName, anno.attName()[i], attClass));
            // set 方法
            builder.addMethod(buildSetMethod("set" + tmpName, anno.attName()[i], attClass));
        }

        // toString 方法
        builder.addMethod(buildToStringMethod(anno));

        return builder.build();
    }


    private FieldSpec buildFiledSerializableID() {
        return FieldSpec.builder(long.class, "serialVersionUID",
                Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).initializer("1L").build();
    }


    private MethodSpec buildSetMethod(String methodName, String fieldName, Class type) {
        return MethodSpec.methodBuilder(methodName)
                .returns(TypeName.VOID)
                .addParameter(type, fieldName)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    private MethodSpec buildGetMethod(String methodName, String fieldName, Class type) {
        return MethodSpec.methodBuilder(methodName)
                .returns(type)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return $L", fieldName)
                .build();
    }

    private MethodSpec buildToStringMethod(Bean bean) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("toString");
        builder.addCode("return $S", "People = { ");
        for (int i = 0; i < bean.attName().length; i++) {
            builder.addCode(" + $S + $L", bean.attName()[i]+":", "this."+bean.attName()[i]);
            if (i < bean.attName().length - 1) {
                builder.addCode(" + $S", ", ");
            }
        }
        builder.addCode(" + $S;\n", "}");

        builder.returns(String.class);
        builder.addModifiers(Modifier.PUBLIC);

        return builder.build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Bean.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(ERROR, message, element);
    }
}
