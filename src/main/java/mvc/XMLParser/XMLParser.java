package mvc.XMLParser;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
public class XMLParser {
    public static String getXML(String xmlFile){
        try {
            SAXReader saxReader = new SAXReader();
            InputStream resourceAsStream = XMLParser.class.getClassLoader().
                    getResourceAsStream(xmlFile);
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            Element element = rootElement.element("component-scan");
            Attribute attribute = element.attribute("base-package");
            String basePackage = attribute.getValue();
//            controller,service
            System.out.println(basePackage);
            return basePackage;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
