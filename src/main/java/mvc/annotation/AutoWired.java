package mvc.annotation;

import java.lang.annotation.*;

/**
 * Karl Rules!
 * 2023/11/13
 * now File Encoding is UTF-8
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWired {
    String value() default "";
}
