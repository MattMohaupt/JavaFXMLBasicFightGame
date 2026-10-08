package com.example.level;

import java.util.List;
import java.util.Random;

import com.example.entity.Enemy;
import com.example.entity.Player;

public class Level2 extends Level {

    private final Random random = new Random();

    public Level2(Player player, List<Enemy> enemies, double groundY) {
        super(player, enemies, groundY);
        configure();
    }

    @Override
    protected void configure() {
        worldLength = 5000;
        int count = 6;
        double spacing = (worldLength - 1200) / (count + 1);
        for (int i = 0; i < count; i++) {
            double baseX = 900 + i * spacing + random.nextInt(150);
            double patrolRange = 60 + random.nextInt(80);
            double speed = 24 + random.nextDouble() * 24;   // 24–48 px/s
            enemies.add(makeEnemy(baseX, patrolRange, speed));
        }
    }

    @Override
    public boolean requireAllEnemiesDefeated() { return true; }
}