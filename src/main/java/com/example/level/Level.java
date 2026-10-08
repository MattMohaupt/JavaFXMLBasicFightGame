package com.example.level;

import java.util.List;

import com.example.entity.Enemy;
import com.example.entity.Player;

import javafx.scene.canvas.GraphicsContext;

/**
 * Defines a level. Each level provides its own world length,
 * enemy spawn rules, and optionally overrides drawing.
 */
public abstract class Level {

    protected final Player player;
    protected final List<Enemy> enemies;
    protected double worldLength;
    protected double groundY;

    public Level(Player player, List<Enemy> enemies, double groundY) {
        this.player = player;
        this.enemies = enemies;
        this.groundY = groundY;
    }

    /** Subclasses set worldLength and populate enemies here. */
    protected abstract void configure();

    /** Called every frame to update level-specific logic (optional). */
    public void update() { }

    /** Level-specific background drawing (optional). */
    public void drawBackground(GraphicsContext gc, double cameraX, int screenWidth, int screenHeight) { }

    /** Whether the player must defeat all enemies before the goal counts. */
    public boolean requireAllEnemiesDefeated() { return false; }

    public double getWorldLength() { return worldLength; }

    public boolean allEnemiesDefeated() {
        return enemies.stream().noneMatch(Enemy::isAlive);
    }

    protected Enemy makeEnemy(double x, double patrolRange, double speed) {
        return new Enemy(x, 40, 70, x - patrolRange, x + patrolRange, speed);
    }
}