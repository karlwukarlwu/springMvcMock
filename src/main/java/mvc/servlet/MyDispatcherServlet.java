package mvc.servlet;

import mvc.annotation.Controller;
import mvc.annotation.RequestMapping;
import mvc.annotation.RequestParam;
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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 * 这个就是前端控制器
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
        executeDispatch(req, resp);
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
//        获取用户请求的uri
//          MyHandler{
//              url='/order/add',
//              controller='controller.OrderController@5d74b886',
//              method=public void controller.OrderController.addOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
//          }
        MyHandler handler = getHandler(request);
        if (handler == null) {
            try {
                response.getWriter().println("404");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        } else {
            try {
//                开始执行反射方法 传入request和response作为方法的参数（）
//                这里等于是 method.invoke(o,request,response)
//                //   MyHandler{url='/order/add', controller='controller.OrderController@5d74b886', method=public void controller.OrderController.addOrder(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)},
//                下面这样写法，其实是针对目标方法是 m(HttpServletRequest request , HttpServletResponse response)
//                不符合rest 风格 现在开始优化
//                handler.getMethod().invoke(handler.getController(),request,response);

//                现在开始加上了requestParam注解
//                将: HttpServletRequest 和 HttpServletResponse封装到参数数组
                //1. 得到目标方法的所有形参参数信息[对应的数组]
                Class<?>[] parameterTypes =
                        handler.getMethod().getParameterTypes();
                //2. 根据上面的数组长度创建一个新的数组
                Object[] params = new Object[parameterTypes.length];
                //3. 循环遍历数组, 将request和response放入到数组中
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if ("HttpServletRequest".equals(parameterType.getSimpleName())) {
                        params[i] = request;
                    } else if ("HttpServletResponse".equals(parameterType.getSimpleName())) {
                        params[i] = response;
                    }
                }
//                将参数放入到params数组中（刚刚放的是HttpServletRequest和HttpServletResponse)
//                拿到http的请求参数
                Map<String, String[]> parameterMap = request.getParameterMap();
//                遍历参数
//                为什么 entry是string string[]
//                因为可能多个value一个key
//                http://localhost:8080/monster/find?name=牛魔王&hobby=打篮球&hobby=喝酒
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//                    获取参数名
                    String paramName = entry.getKey();
//                    获取参数值(假设只有单值)
                    String paramValues = entry.getValue()[0];
//                    开始不断地去找 每当获得一个entry
//                    handler和 对应的method是固定的，是上面获取的 现在这里就说把所有键值对进行遍历匹配
//                    匹配成功 返回参数在handler.getMethod中的位置
//                    这个参数没有被注释 则返回-1
                    int index = getIndexRequestParameterIndex(handler.getMethod(), paramName);
                    if (index != -1) {//返回对应位置 并放入params数组中
                        params[index] = paramValues;
                    } else {
//                      如果没有@requestParam注解 那么开始采取另一个处理
                        //思路
                        //1. 得到目标方法的所有形参的名称-专门编写方法获取形参名
                        //2. 对得到目标方法的所有形参名进行遍历,如果匹配就把当前请求的参数值，填充到params
//                        这里是循环套循环 等于是拿到一个参数和另一个数组进行循环匹配
                        List<String> parameterNames =
                                getParameterNames(handler.getMethod());
                        for (int i = 0; i < parameterNames.size(); i++) {
                            String parameterName = parameterNames.get(i);
                            if (parameterName.equals(paramName)) {
                                params[i] = paramValues;
                                break;
                            }
                        }


                    }
                }
//                 这里专门编写一个方法，得到请求的参数对应的是第几个形参
//          invoke的第三个参数为可变参数 可以传入数组或者多个值
                handler.getMethod().invoke(handler.getController(), params);


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
//阶段6这里是拿到有注解的参数
    public int getIndexRequestParameterIndex(Method method, String name) {

        //1.得到method的所有形参参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //取出当前的形参参数
            Parameter parameter = parameters[i];
            //判断parameter是不是有@RequestParam注解
            boolean annotationPresent = parameter.isAnnotationPresent(RequestParam.class);
            if (annotationPresent) {//说明有@RequestParam
                //取出当前这个参数的 @RequestParam(value = "xxx")
                RequestParam requestParamAnnotation =
                        parameter.getAnnotation(RequestParam.class);
                String value = requestParamAnnotation.value();
                //这里就是匹配的比较
                if (name.equals(value)) {
                    return i;//找到请求的参数，对应的目标方法的形参的位置
                }
            }
        }
        //如果没有匹配成功，就返回-1
        return -1;
    }

    //阶段6 编写方法, 得到目标方法的所有形参的名称,并放入到集合中返回
//    他这里有瑕疵 如果是混合这有注释和没注释的参数 会出现问题
//    他是要么全部是注释 要么全部是没注释
    public List<String> getParameterNames(Method method) {

        List<String> parametersList = new ArrayList<>();
        //获取到所以的参数名->这里有一个小细节
        //在默认情况下 parameter.getName() 得到的名字不是形参真正名字
        //而是 [arg0, arg1, arg2...], 这里我们要引入一个插件，使用java8特性，这样才能解决
//
        Parameter[] parameters = method.getParameters();
        //遍历parameters 取出名称，放入parametersList
//        想获得名称pom.xml中需要加入插件
//          <compilerArgs>
//                        <arg>-parameters</arg>
//                    </compilerArgs>
//                    <encoding>utf-8</encoding>
        for (Parameter parameter : parameters) {
            parametersList.add(parameter.getName());
        }
        System.out.println("目标方法的形参列表=" + parametersList);
        return parametersList;
    }
}
