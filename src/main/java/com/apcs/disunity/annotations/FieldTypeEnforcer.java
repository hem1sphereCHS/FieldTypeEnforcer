package com.apcs.disunity.annotations;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class FieldTypeEnforcer extends AbstractProcessor {
  private Elements elementUtils;
  private Types typeUtils;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    messager = processingEnv.getMessager();
    typeUtils = processingEnv.getTypeUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    for(Element annotation: roundEnv.getElementsAnnotatedWith(EnforceFieldType.class)) {
      ElementType[] targetValue = annotation.getAnnotation(Target.class).value();
      if(targetValue.length != 1 && targetValue[0] == ElementType.FIELD) {
        messager.printError(String.format(
          "annotations with %s has to be able to annotate Fields only.",
          EnforceFieldType.class),
          annotation
        );
      }

      TypeMirror enforcingType = (TypeMirror) annotation.getAnnotationMirrors()
        .stream()
        .filter(m -> m.getAnnotationType().toString().equals(EnforceFieldType.class.getName()))
        .findFirst()
        .get()
        .getElementValues()
        .values()
        .iterator()
        .next()
        .getValue();

      for(Element element: roundEnv.getElementsAnnotatedWith((TypeElement) annotation)) {
        if(!typeUtils.isAssignable(element.asType(), enforcingType)) {
          messager.printError(String.format("%s cannot be casted to %s.", element, enforcingType),element);
        } else {
          messager.printNote(String.format("%s can be casted to %s.", element, enforcingType),element);
        }
      }
    }

    return true;
  }

  private TypeElement getType(Class<?> cls) {
    return elementUtils.getTypeElement(cls.getCanonicalName());
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new HashSet<>();
    set.add(EnforceFieldType.class.getName());
    return set;
  }
}
