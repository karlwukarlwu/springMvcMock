package controller;

import mvc.annotation.Controller;
import mvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
@Controller
public class OrderController {
    @RequestMapping(value = "/order/list")
    public void listOrder(HttpServletRequest request,
                          HttpServletResponse response)  {
        //设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
        //获取writer返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>订单列表信息</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/order/add")
    public void addOrder(HttpServletRequest request,
                         HttpServletResponse response)  {
        //设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
        //获取writer返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>添加订单...</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
