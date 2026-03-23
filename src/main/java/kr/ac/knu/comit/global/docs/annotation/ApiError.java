package kr.ac.knu.comit.global.docs.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kr.ac.knu.comit.global.exception.BusinessErrorCode;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiError {
    BusinessErrorCode code();

    String when() default "";
}
