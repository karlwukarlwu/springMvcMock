package mvc.context;

import mvc.XMLParser.XMLParser;
import mvc.annotation.AutoWired;
import mvc.annotation.Controller;
import mvc.annotation.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
//这里是spring的东西 对应的是spring的applicationContext
//    自定义的spring容器
//      CusSpringApplicationContext ioc = new CusSpringApplicationContext(CusSpringConfig.class);
//        System.out.println("ioc=" + ioc);
public class MyWebApplicationContext {
    //    定义属性 存放配置类的路径
    private List<String> classFullPathList = new ArrayList<>();
//    classFullPath 存放的是所有的类的全路径
//    classFullPath + controller.MonsterController
//    classFullPath + controller.OrderController
//    classFullPath + controller.T22.t2

    //    定义属性 存放反射后生成的对象 格式 对象名：对象实例
    public ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();
//    ioc = {orderController=controller.OrderController@5d74b886, monsterController=controller.MonsterController@270e3998}

    //    动态获取配置文件的路径
    private String configClass;

    public MyWebApplicationContext() {
    }

    public MyWebApplicationContext(String configClass) {
        this.configClass = configClass;
    }

    public void init() {
//        开始读取配置文件
//        String xml = XMLParser.getXML("MyspringMvc.xml");
        String xml = XMLParser.getXML(configClass.split(":")[1].trim());
        String[] split = xml.split(",");
        if (split.length > 0) {
            for (String s : split) {
                System.out.println("s = " + s);
//                扫描配置文件中的配置扫描文件夹
                scan(s);
            }
        }
//        当我们 扫描到所有的类的全路径后，会在scan文件中将所有的类的全路径放入到classFullPathList中
//        将list中扫描到的所有类进行反射
        executeInstance();
        System.out.println("ioc = " + ioc);
//        开始自动装配
        executeAutowired();
        System.out.println("ioc = " + ioc);


    }

    //通过xml文件获取包的路径
    //创建方法 对包下面的类进行扫描 拿到所有的类 然后我们需要他们的类和工程路径
    public void scan(String basePackage) {
        //获取包下的所有类的全路径
//        1.拿到类加载路径
        System.out.println(this.getClass().getClassLoader().getResource(""));
        System.out.println(basePackage.replaceAll("\\.", "/"));
        URL resource = this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.", "/"));
        System.out.println("resource = "+resource);
//        file:/C:/Users/23584/Desktop/IDEAUtf-8/springMvc/MyMvc/target/MyMvc/WEB-INF/classes/controller/
//        2.拿到文件夹下的所有文件
        String path = resource.getFile();
//        System.out.println("path + "+ path);
        File dir = new File(path);
//        3.遍历文件夹下的所有文件 如果是文件夹就递归 如果是文件就加入到list中
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scan(basePackage + "." + file.getName());
            } else {
//                目前还不知道是否有注解 同时不知道后缀是不是.class(假设全是class)
//                现在是把所有文件路径都扫描进来
//                我们不需要.class后缀 我们希望的是包名+类名
                String classFullPath = basePackage + "." + file.getName().replaceAll(".class", "");

                classFullPathList.add(classFullPath);
                System.out.println("classFullPath + " + classFullPath);
            }
        }
    }

    //    将符合条件的类反射放入容器
    public void executeInstance() {
        if (classFullPathList.size() > 0) {
            for (String classFullPath : classFullPathList) {
                try {
                    Class<?> clazz = Class.forName(classFullPath);
//                    有注解 开始反射
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        Object o = clazz.newInstance();
//                        将类名首字母小写作为key
                        String s = clazz.getSimpleName().substring(0, 1).toLowerCase()
                                + clazz.getSimpleName().substring(1);
                        ioc.put(s, o);
                    } else if (clazz.isAnnotationPresent(Service.class)) {  //现在这里要加上service注解
                        Service serviceAnnotation =
                                clazz.getAnnotation(Service.class);
                        String beanName = serviceAnnotation.value();
                        if ("".equals(beanName)) {//说明没有指定value, 我们就使用默认的机制注入Service
                            //可以通过接口名/类名[首字母小写]来注入ioc容器
                            //1.得到所有接口的名称=>反射
                            Class<?>[] interfaces = clazz.getInterfaces();
//这里聪明 他多个key 实际上是指向一个value

                            Object instance = clazz.newInstance();
                            for (Class<?> anInterface : interfaces) {
                                //接口名->首字母小写
                                String beanName2 = anInterface.getSimpleName().substring(0, 1).toLowerCase() +
                                        anInterface.getSimpleName().substring(1);
//                            他这里有问题  他这里是把接口名当成了beanName 实际上无论多少个接口 只有一个实现类在ioc中
                                ioc.put(beanName2, instance);
                            }
                        } else {//如果有指定名称,就使用该名称注入即可
                            ioc.put(beanName, clazz.newInstance());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("没有类");
            return;
        }
    }

    //    编写方法 完成属性的自动装配 阶段六 @AutoWired
    public void executeAutowired() {
        //看看你这个容器中有没有类
        if (ioc.isEmpty()) {
            return;
        }
//        遍历容器中的类，获取到所有是属性 看看是否要自动装配
        for (String key : ioc.keySet()) {
//            拿到bean
            Object instance = ioc.get(key);
//            拿到bean的所有属性
            Class<?> clazz = instance.getClass();
            try {
                for (Field declaredField : clazz.getDeclaredFields()) {
//                    判断是否有注解
                    if (declaredField.isAnnotationPresent(AutoWired.class)) {
//                        拿到注解
                        AutoWired annotation = declaredField.getAnnotation(AutoWired.class);
//                        拿到注解的值，如果有
                        String value = annotation.value();
                        if ("".equals(value)) {//如果没有设置value
//                            没有指定值
                            //即得到字段类型的名称的首字母小写，作为名字来进行装配
                            value = declaredField.getType().getSimpleName().substring(0, 1).toLowerCase()
                                    + declaredField.getType().getSimpleName().substring(1);
                        }
//                            指定的值不存在ioc中 报错
                        if (!ioc.containsKey(value)) {
                            throw new RuntimeException("没有这个bean");
                        }
//                        防止私有
                        declaredField.setAccessible(true);
//                        开始装配
//                        instance是要装配的类 value是要装配的属性
//                        这里类似于之前写的mock_spring02的那个自动装配
                        declaredField.set(instance, ioc.get(value));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
