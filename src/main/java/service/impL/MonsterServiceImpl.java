package service.impL;

import entity.Monster;
import mvc.annotation.Service;
import service.MonsterService;

import java.util.ArrayList;
import java.util.List;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
@Service //通过这个注解 注入容器
public class MonsterServiceImpl implements MonsterService {
    //这里老师就模拟数据->DB
    List<Monster> monsters =
            new ArrayList<>();
    @Override
    public List<Monster> listMonster() {
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "老猫妖怪", "抓老鼠", 200));
        return monsters;
    }
    @Override
    public List<Monster> findMonsterByName(String name) {
        monsters.add(new Monster(100, "牛魔王", "芭蕉扇", 400));
        monsters.add(new Monster(200, "老猫妖怪", "抓老鼠", 200));
        monsters.add(new Monster(300, "大象精", "运木头", 100));
        monsters.add(new Monster(400, "黄袍怪", "吐烟雾", 300));
        monsters.add(new Monster(500, "白骨精", "美人计", 800));

        List<Monster> findMonsters =
                new ArrayList<>();
        //遍历monsters,返回满足条件
        for (Monster monster : monsters) {
            if (monster.getName().contains(name)) {
                findMonsters.add(monster);
            }
        }
        return findMonsters;
    }

}
