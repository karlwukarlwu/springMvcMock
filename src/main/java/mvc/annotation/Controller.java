package mvc.annotation;

import java.lang.annotation.*;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
//可以标记的位置
@Target(ElementType.TYPE)
//生命周期
@Retention(RetentionPolicy.RUNTIME)
//是否生成到javadoc中
@Documented
public @interface Controller {
    String value() default "";
}
