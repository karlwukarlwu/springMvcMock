package test;

import mvc.XMLParser.XMLParser;
import mvc.context.MyWebApplicationContext;
import org.junit.jupiter.api.Test;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
public class forTEST {

    public static void main(String[] args) {
        MyWebApplicationContext ioc = new MyWebApplicationContext("config: MyspringMvc.xml");
        ioc.init();
        System.out.println("ioc=" + ioc);
    }
}
