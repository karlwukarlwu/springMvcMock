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
public class MonsterController {

    @RequestMapping(value = "/monster/list")
    public void listMonster(HttpServletRequest request, HttpServletResponse response){
//        设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
//
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.println("MonsterController.listMonster");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
