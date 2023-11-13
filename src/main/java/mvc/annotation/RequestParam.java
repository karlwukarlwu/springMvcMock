package mvc.annotation;

import java.lang.annotation.*;

/**
 * Karl Rules!
 * 2023/11/13
 * now File Encoding is UTF-8
 * 用来标记参数的注解
 * 表示这个参数是从请求中获取的
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
