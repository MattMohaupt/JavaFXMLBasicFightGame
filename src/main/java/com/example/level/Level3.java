package com.example.level;

import java.util.List;
import java.util.Random;

import com.example.entity.Enemy;
import com.example.entity.Player;

public class Level3 extends Level {

    private final Random random = new Random();

    public Level3(Player player, List<Enemy> enemies, double groundY) {
        super(player, enemies, groundY);
        configure();
    }

    @Override
protected void configure() {
    worldLength = 6000;
    int count = 9;
    double spacing = (worldLength - 1200) / (count + 1);
    for (int i = 0; i < count; i++) {
        double baseX = 900 + i * spacing + random.nextInt(120);
        double patrolRange = 80 + random.nextInt(100);
        double speed = 36 + random.nextDouble() * 30;   // 36–66 px/s
        enemies.add(makeEnemy(baseX, patrolRange, speed));
    }
}

    @Override
    public boolean requireAllEnemiesDefeated() { return true; }
}