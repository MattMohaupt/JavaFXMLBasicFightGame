package com.example.level;

import java.util.List;

import com.example.entity.Enemy;
import com.example.entity.Player;

public class Level1 extends Level {

    public Level1(Player player, List<Enemy> enemies, double groundY) {
        super(player, enemies, groundY);
        configure();
    }

    @Override
    protected void configure() {
        worldLength = 3000;
        // No enemies in Level 1
    }

    @Override
    public boolean requireAllEnemiesDefeated() { return false; }
}