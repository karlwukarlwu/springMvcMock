package mvc.annotation;

import java.lang.annotation.*;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
//    只有有了这个才可以使用 value() 方法
    String value() default "";
}
