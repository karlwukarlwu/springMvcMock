package test;

import mvc.XMLParser.XMLParser;
import org.junit.jupiter.api.Test;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
public class forTEST {
    @Test
    public void test(){
        String xml = XMLParser.getXML("MyspringMvc.xml");
        System.out.println(xml);

    }
}
