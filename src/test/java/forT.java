/**
 * Karl Rules!
 * 2023/11/15
 * now File Encoding is UTF-8
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Monster;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 韩顺平
 * @version 1.0
 */
public class forT {
    public static void main(String[] args) {

        List<Monster> monsters =
                new ArrayList<>();
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "老猫妖怪", "抓老鼠", 200));

        //把monsters 转成json
//        要在pom.xml中添加依赖
//        ObjectMapper这个来自于jackson 去pom里添加
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String monstersJson = objectMapper.writeValueAsString(monsters);
            System.out.println("monstersJson=" + monstersJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
