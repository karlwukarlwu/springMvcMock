import mvc.context.MyWebApplicationContext;
import org.junit.Test;

/**
 * Karl Rules!
 * 2023/11/13
 * now File Encoding is UTF-8
 */
public class forT {
    public static void main(String[] args) {
        MyWebApplicationContext ioc = new MyWebApplicationContext("config: MyspringMvc.xml");
        ioc.init();
        System.out.println("ioc=" + ioc);
    }
}
