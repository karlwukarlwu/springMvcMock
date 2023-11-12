package mvc.servlet;

import mvc.annotation.Controller;
import mvc.annotation.RequestMapping;
import mvc.context.MyWebApplicationContext;
import mvc.handler.MyHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
public class MyDispatcherServlet extends HttpServlet {
    //    定义属性 handerList 保存url和控制器方法的映射
    private List<MyHandler> handlerList = new ArrayList<>();
//    handlerList = 格式 url + 对象 + 方法
//      [MyHandler{url='/order/list', controller='controller.OrderController@5d74b886', method=public void controller.OrderController.listOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)},
//       MyHandler{url='/order/add', controller='controller.OrderController@5d74b886', method=public void controller.OrderController.addOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)},
//       MyHandler{url='/monster/list', controller='controller.MonsterController@270e3998', method=public void controller.MonsterController.listMonster(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)}]
//
    MyWebApplicationContext myWebApplicationContext = null;


    @Override
//    初始化 同时也是启动Spring容器
//          当我们init-param 在web.xml中配置的时候 我们可以用ServletConfig来获取
//          从而实现动态配置
    public void init(ServletConfig servletConfig) throws ServletException {
        String contextConfigLocation = servletConfig.getInitParameter("contextConfigLocation");
        System.out.println("contextConfigLocation = " + contextConfigLocation);//拿到value

//        把作用域扩大
//        MyWebApplicationContext myWebApplicationContext = new MyWebApplicationContext();
        myWebApplicationContext = new MyWebApplicationContext(contextConfigLocation);
//        这里是初始化容器 这里开始读取spring的配置文件
//            至于web.xml 那个是给tomcat看的
        myWebApplicationContext.init();
//        当我们对容器进行初始化后 我们就可以从容器中获取对象了
//        这里开始整理url和控制器方法的映射
        initHandlerMapping();
        System.out.println("handlerList = " + handlerList);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MyDispatcherServlet.doGet");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("MyDispatcherServlet.doPost");
//        在init中 将url和控制器方法的映射关系放入到handlerList中
//        当我们进行不同的请求的时候 开始取出handlerList中的对象
       executeDispatch(req,resp);
    }

    // 编写方法 完成url和控制器方法的映射
    private void initHandlerMapping() {
        if (myWebApplicationContext.ioc.isEmpty()) {
//            如果容器为空 直接返回
            return;
        }
//        遍历ioc容器 开始映射
        for (String key : myWebApplicationContext.ioc.keySet()) {
//            获取容器中的对象
            Object o = myWebApplicationContext.ioc.get(key);
            Class<?> clazz = o.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
//                Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
//                String controllerName = annotation.value();
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
//                        取出url
                        RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
                        String url = requestMapping.value();
                        //                        封装成handler对象
//   MyHandler{url='/order/add', controller='controller.OrderController@5d74b886', method=public void controller.OrderController.addOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)},
                        MyHandler myHandler = new MyHandler(url, o, method);
                        handlerList.add(myHandler);
                    }
                }
            }

        }
    }

    //    通过request对象 返回handler对象 如果没有返回404
//    当我们进行请求的时候 会先来这里找有没有合适的url 如果由就返回handler对象，继续由executeDispatch进行反射
//    如果没有 返回null 由executeDispatch返回404
    private MyHandler getHandler(HttpServletRequest request) {
        if (handlerList.isEmpty()) {
            return null;
        }
//        获取用户请求的uri  比如localhost:8080/MyMvc/user/login uri就是/Mymvc/user/login
//        这里需要对工程名进行处理 因为我们获取的是这个MyHandler{url='/monster/list',没有工程路径

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();//这里取消掉工程路径
        requestURI = requestURI.replace(contextPath, "");
        for (MyHandler myHandler : handlerList) {
            if (myHandler.getUrl().equals(requestURI)) {
                return myHandler;
            }
        }
        return null;
    }

    // 先去找有没有合适的url 找到了开始进行反射
    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        MyHandler handler = getHandler(request);
        if (handler == null) {
            try {
                response.getWriter().println("404");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }else {
            try {
//                开始执行反射方法 传入request和response作为方法的参数（）
//                这里等于是 method.invoke(o,request,response)
//                //   MyHandler{url='/order/add', controller='controller.OrderController@5d74b886', method=public void controller.OrderController.addOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)},
                handler.getMethod().invoke(handler.getController(),request,response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
