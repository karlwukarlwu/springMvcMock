package controller;

import entity.Monster;
import mvc.annotation.AutoWired;
import mvc.annotation.Controller;
import mvc.annotation.RequestMapping;
import mvc.annotation.RequestParam;
import service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
@Controller
public class MonsterController {
    @AutoWired
    private MonsterService monsterService;

    @RequestMapping(value = "/monster/list")
    public void listMonster(HttpServletRequest request, HttpServletResponse response) {
//        设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
//
        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
        //调用monsterService
        List<Monster> monsters = monsterService.listMonster();
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Monster monster : monsters) {
            content.append("<tr><td>" + monster.getId()
                    + "</td><td>" + monster.getName() + "</td><td>"
                    + monster.getSkill() + "</td><td>"
                    + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");

        //获取writer返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //增加方法，通过name返回对应的monster集合

    @RequestMapping(value = "/monster/find")
//    public void findMonsterByName(HttpServletRequest request,
//                                  HttpServletResponse response,
//                                  @RequestParam(value = "name") String name) {
    public void findMonsterByName(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String name) {
        //设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
        System.out.println("--接收到的name---" + name);
        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
        //调用monsterService
        List<Monster> monsters = monsterService.findMonsterByName(name);
        content.append("<table border='1px' width='400px' style='border-collapse:collapse'>");
        for (Monster monster : monsters) {
            content.append("<tr><td>" + monster.getId()
                    + "</td><td>" + monster.getName() + "</td><td>"
                    + monster.getSkill() + "</td><td>"
                    + monster.getAge() + "</td></tr>");
        }
        content.append("</table>");

        //获取writer返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
