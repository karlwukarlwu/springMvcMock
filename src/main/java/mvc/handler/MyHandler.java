package mvc.handler;

import java.lang.reflect.Method;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
//用来记录url和控制器方法映射关系的类
//    结构是url - 对象 - 方法
public class MyHandler {
    private String url;
    private Object controller;
//    这个是对应的方法 因为一个控制器可能有多个方法
    private Method method;

    public MyHandler() {
    }

    public MyHandler(String url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "MyHandler{" +
                "url='" + url + '\'' +
                ", controller='" + controller + '\'' +
                ", method=" + method +
                '}';
    }
}
