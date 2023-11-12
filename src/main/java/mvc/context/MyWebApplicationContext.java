package mvc.context;

import mvc.XMLParser.XMLParser;
import mvc.annotation.Controller;

import java.io.File;
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
    private List<String> classFullPathList = new ArrayList<String>();
//    classFullPath 存放的是所有的类的全路径
//    classFullPath + controller.MonsterController
//    classFullPath + controller.OrderController
//    classFullPath + controller.T22.t2

    //    定义属性 存放反射后生成的对象 格式 对象名：对象实例
    public ConcurrentHashMap<String,Object> ioc = new ConcurrentHashMap<String,Object>();
//    ioc = {orderController=controller.OrderController@5d74b886, monsterController=controller.MonsterController@270e3998}

//    动态获取配置文件的路径
    private String configClass;

    public MyWebApplicationContext() {
    }
    public MyWebApplicationContext(String configClass) {
        this.configClass = configClass;
    }

    public void init(){
//        开始读取配置文件
//        String xml = XMLParser.getXML("MyspringMvc.xml");
        String xml = XMLParser.getXML(configClass.split(":")[1].trim());
        String[] split = xml.split(",");
        if(split.length>0) {
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


    }
    //通过xml文件获取包的路径
    //创建方法 对包下面的类进行扫描 拿到所有的类 然后我们需要他们的类和工程路径
    public void scan(String basePackage){
        //获取包下的所有类的全路径
//        1.拿到类加载路径
        URL resource = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
//        System.out.println(resource);
//        file:/C:/Users/23584/Desktop/IDEAUtf-8/springMvc/MyMvc/target/MyMvc/WEB-INF/classes/controller/
//        2.拿到文件夹下的所有文件
        String path = resource.getFile();
//        System.out.println("path + "+ path);
        File dir = new File(path);
//        3.遍历文件夹下的所有文件 如果是文件夹就递归 如果是文件就加入到list中
        for(File file:dir.listFiles()){
            if(file.isDirectory()){
                scan(basePackage+"."+file.getName());
            }else{
//                目前还不知道是否有注解 同时不知道后缀是不是.class(假设全是class)
//                现在是把所有文件路径都扫描进来
//                我们不需要.class后缀 我们希望的是包名+类名
                String classFullPath = basePackage+"."+file.getName().replaceAll(".class","");

                classFullPathList.add(classFullPath);
                System.out.println("classFullPath + " +classFullPath);
            }
        }
    }

//    将符合条件的类反射放入容器
    public void executeInstance(){
        if(classFullPathList.size()>0){
            for(String classFullPath:classFullPathList){
                try {
                    Class<?> clazz = Class.forName(classFullPath);
//                    有注解 开始反射
                    if(clazz.isAnnotationPresent(Controller.class)){
                        Object o = clazz.newInstance();
//                        将类名首字母小写作为key
                        String s = clazz.getSimpleName().substring(0, 1).toLowerCase()
                                + clazz.getSimpleName().substring(1);
                        ioc.put(s,o);
                    }//要其他注解这里自己加
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            System.out.println("没有类");
            return;
        }
    }

}
