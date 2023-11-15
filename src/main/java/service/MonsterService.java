package service;

import entity.Monster;

import java.util.List;

/**
 * Karl Rules!
 * 2023/11/11
 * now File Encoding is UTF-8
 */
public interface MonsterService {
    public List<Monster> listMonster();
    public List<Monster> findMonsterByName(String name);

    public boolean login(String name);


}
