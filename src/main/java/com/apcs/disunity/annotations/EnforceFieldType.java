package com.apcs.disunity.annotations;

import java.lang.annotation.*;

/// Requires the annotated field's type to be a specified type.
/// Types does not have to be strictly equal, but rather be able to be cased without error.
/// This means that if an interface was specified, every classes that implement it is valid.
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface EnforceFieldType {
  /// type of the annotated field
  Class<?> value();
}
