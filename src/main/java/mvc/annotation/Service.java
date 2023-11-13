package mvc.annotation;

import java.lang.annotation.*;

/**
 * Karl Rules!
 * 2023/11/12
 * now File Encoding is UTF-8
 * 用来标注业务层的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
