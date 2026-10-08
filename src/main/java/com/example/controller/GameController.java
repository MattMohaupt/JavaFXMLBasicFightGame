package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.Main;
import com.example.entity.AttackType;
import com.example.entity.Enemy;
import com.example.entity.Player;
import com.example.level.Level;
import com.example.level.Level1;
import com.example.level.Level2;
import com.example.level.Level3;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameController {

    @FXML private StackPane gameRoot;
    @FXML private Canvas canvas;

    private GraphicsContext gc;
    private Player player;
    private Level level;
    private final List<Enemy> enemies = new ArrayList<>();
    private AnimationTimer gameLoop;

    private double cameraX = 0;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameRunning = false;
    private int currentLevelNumber = 1;

    private static final double GROUND_Y = 520;

    // FPS counter
    private long lastFpsUpdate = 0;
    private int framesSinceLastFpsUpdate = 0;
    private int currentFps = 0;
    private static final long FPS_UPDATE_INTERVAL = 500_000_000L; // 0.5 s in ns

    // Safety clamp so a long pause doesn't teleport the player
    private static final double MAX_DT = 0.05; // seconds

    @FXML
    private void initialize() {
        gc = canvas.getGraphicsContext2D();
    }

    public void startLevel(int levelNumber) {
        this.currentLevelNumber = levelNumber;
        this.enemies.clear();
        this.player = new Player(100, GROUND_Y - Player.HEIGHT);

        // Build the correct Level subclass
        switch (levelNumber) {
            case 1 -> this.level = new Level1(player, enemies, GROUND_Y);
            case 2 -> this.level = new Level2(player, enemies, GROUND_Y);
            default -> this.level = new Level3(player, enemies, GROUND_Y);
        }

        cameraX = 0;
        leftPressed = false;
        rightPressed = false;
        gameRunning = true;

        // Reset FPS counters
        lastFpsUpdate = 0;
        framesSinceLastFpsUpdate = 0;
        currentFps = 0;

        // Input handling
        gameRoot.setFocusTraversable(true);
        gameRoot.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                leftPressed = true;
            } else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                rightPressed = true;
            } else if (e.getCode() == KeyCode.SPACE
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.W) {
                if (gameRunning) player.jump();
            } else if (e.getCode() == KeyCode.P) {
                player.tryAttack(AttackType.PUNCH);
            } else if (e.getCode() == KeyCode.K) {
                player.tryAttack(AttackType.KICK);
            }
        });

        gameRoot.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                leftPressed = false;
            } else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                rightPressed = false;
            }
        });

        // Start loop
        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return; // skip first frame; no dt yet
                }
                double dt = (now - lastTime) / 1_000_000_000.0; // seconds
                lastTime = now;
                if (dt > MAX_DT) dt = MAX_DT;

                update(dt);
                draw();
                tickFps(now);
            }
        };
        gameLoop.start();

        Platform.runLater(() -> gameRoot.requestFocus());
    }

    private void update(double dt) {
        if (!gameRunning) return;

        player.update(leftPressed, rightPressed, GROUND_Y, dt);
        level.update();
        cameraX = Math.max(0, player.getX() - 250);

        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            e.update(GROUND_Y, dt);

            // Attack hits enemy
            if (player.attackHits(e.getX(), e.getTop(GROUND_Y),
                    e.getWidth(), e.getHeight(), GROUND_Y)) {
                e.kill();
            }

            // Body collision
            if (e.isAlive() && player.overlaps(e.getX(), e.getTop(GROUND_Y),
                    e.getWidth(), e.getHeight())) {
                loseGame();
                return;
            }
        }

        // Reached the goal?
        if (player.getX() >= level.getWorldLength() - 100) {
            if (level.requireAllEnemiesDefeated() && !level.allEnemiesDefeated()) {
                return;
            }
            winGame();
        }
    }

    private void winGame() {
        gameRunning = false;
        if (gameLoop != null) gameLoop.stop();
        Main.showWinScreen(currentLevelNumber, currentLevelNumber + 1);
    }

    private void loseGame() {
        gameRunning = false;
        if (gameLoop != null) gameLoop.stop();
        Main.showLoseScreen(currentLevelNumber);
    }

    private void draw() {
        // Sky
        gc.setFill(Color.web("#87CEEB"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Parallax hills
        gc.setFill(Color.web("#6BAF7A"));
        double hillOffset = -(cameraX * 0.3) % 400;
        for (int i = -1; i < 4; i++) {
            gc.fillOval(hillOffset + i * 400, GROUND_Y - 150, 300, 300);
        }

        // Sun
        gc.setFill(Color.YELLOW);
        gc.fillOval(650, 50, 80, 80);

        // Ground
        gc.setFill(Color.web("#8B4513"));
        gc.fillRect(0, GROUND_Y, canvas.getWidth(), canvas.getHeight() - GROUND_Y);
        gc.setFill(Color.web("#228B22"));
        gc.fillRect(0, GROUND_Y - 10, canvas.getWidth(), 10);

        // Enemies
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            double sx = e.getX() - cameraX;
            if (sx + e.getWidth() < 0 || sx > canvas.getWidth()) continue;
            drawEnemy(e, sx);
        }

        // Goal flag
        double goalX = level.getWorldLength() - cameraX;
        if (goalX > -100 && goalX < canvas.getWidth() + 100) {
            gc.setFill(Color.web("#4A4A4A"));
            gc.fillRect(goalX, GROUND_Y - 180, 6, 180);
            gc.setFill(Color.web("#FFD700"));
            gc.fillPolygon(
                    new double[]{goalX + 6, goalX + 6, goalX + 70},
                    new double[]{GROUND_Y - 180, GROUND_Y - 130, GROUND_Y - 155},
                    3);
        }

        drawPlayer();

        // HUD
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18));
        double remaining = Math.max(0, (level.getWorldLength() - 100) - player.getX());
        gc.fillText("Level " + currentLevelNumber + "  |  Distance: "
                + (int) remaining + "m", 20, 30);

        long alive = enemies.stream().filter(Enemy::isAlive).count();
        gc.setFont(Font.font("Arial", 14));
        if (!enemies.isEmpty()) {
            gc.fillText("Enemies: " + alive, 20, 55);
            gc.fillText("P = Punch   K = Kick   SPACE = Jump", 20, 78);
        } else {
            gc.fillText("← → move   SPACE = Jump", 20, 55);
        }

        // FPS counter in bottom-left
        gc.fillText("FPS: " + currentFps, 20, canvas.getHeight() - 20);
    }

    private void drawEnemy(Enemy e, double sx) {
        double top = e.getTop(GROUND_Y);
        Color bodyColor = e.getHitFlash() > 0 ? Color.WHITE : Color.web("#8B0000");
        Color headColor = e.getHitFlash() > 0 ? Color.WHITE : Color.web("#B22222");

        double headR = 16;
        double hcx = sx + e.getWidth() / 2;
        double hcy = top + headR;

        gc.setFill(headColor);
        gc.fillOval(hcx - headR, hcy - headR, headR * 2, headR * 2);

        gc.setFill(Color.YELLOW);
        gc.fillOval(hcx - 9, hcy - 4, 6, 6);
        gc.fillOval(hcx + 3, hcy - 4, 6, 6);
        gc.setFill(Color.BLACK);
        gc.fillOval(hcx - 8, hcy - 3, 3, 3);
        gc.fillOval(hcx + 4, hcy - 3, 3, 3);

        double bodyTop = top + headR * 2 - 4;
        double bodyHeight = 30;
        gc.setFill(bodyColor);
        gc.fillRect(sx + 4, bodyTop, e.getWidth() - 8, bodyHeight);

        gc.fillRect(sx - 6, bodyTop + 4, 10, 8);
        gc.fillRect(sx + e.getWidth() - 4, bodyTop + 4, 10, 8);

        gc.setFill(Color.web("#2F2F2F"));
        double legsTop = bodyTop + bodyHeight;
        double legsHeight = GROUND_Y - legsTop;
        gc.fillRect(sx + 6, legsTop, 8, legsHeight);
        gc.fillRect(sx + e.getWidth() - 14, legsTop, 8, legsHeight);
    }

    private void drawPlayer() {
        double sx = player.getX() - cameraX;
        double sy = player.getY();
        AttackType attack = player.getAttack();

        double headR = 20;
        double hcx = sx + Player.WIDTH / 2;
        double hcy = sy + headR;

        gc.setFill(Color.web("#FFDAB9"));
        gc.fillOval(hcx - headR, hcy - headR, headR * 2, headR * 2);

        gc.setFill(Color.BLACK);
        gc.fillOval(hcx - 10, hcy - 7, 6, 6);
        gc.fillOval(hcx + 4, hcy - 7, 6, 6);

        if (attack != AttackType.NONE) {
            gc.setFill(Color.web("#8B0000"));
            gc.fillRect(hcx - 8, hcy + 4, 16, 5);
        } else {
            gc.setStroke(Color.web("#8B0000"));
            gc.setLineWidth(2);
            gc.strokeArc(hcx - 8, hcy + 2, 16, 10, 200, 140,
                    javafx.scene.shape.ArcType.OPEN);
        }

        double bodyTop = sy + headR * 2 - 2;
        double bodyHeight = 50;
        gc.setFill(Color.web("#4169E1"));
        gc.fillRect(sx + 5, bodyTop, Player.WIDTH - 10, bodyHeight);

        gc.setFill(Color.web("#FFDAB9"));
        if (attack == AttackType.PUNCH) {
            gc.fillRect(sx + Player.WIDTH - 5, bodyTop + 5, 35, 10);
            gc.fillOval(sx + Player.WIDTH + 28, bodyTop + 3, 14, 14);
            gc.fillRect(sx - 5, bodyTop + 5, 10, 35);
        } else if (attack == AttackType.KICK) {
            gc.fillRect(sx - 8, bodyTop + 5, 10, 35);
            gc.fillRect(sx + Player.WIDTH - 2, bodyTop + 5, 10, 35);
        } else {
            gc.fillRect(sx - 5, bodyTop + 5, 10, 35);
            gc.fillRect(sx + Player.WIDTH - 5, bodyTop + 5, 10, 35);
        }

        gc.setFill(Color.web("#2F4F4F"));
        double legsTop = bodyTop + bodyHeight;
        double legsHeight = sy + Player.HEIGHT - legsTop;

        if (attack == AttackType.KICK) {
            gc.fillRect(sx + 6, legsTop, 10, legsHeight);
            gc.fillRect(sx + Player.WIDTH - 10, legsTop - 15, 10, 25);
            gc.fillRect(sx + Player.WIDTH - 10, legsTop - 20, 35, 12);
            gc.setFill(Color.BLACK);
            gc.fillRect(sx + Player.WIDTH + 22, legsTop - 22, 14, 10);
        } else {
            gc.fillRect(sx + 8, legsTop, 10, legsHeight);
            gc.fillRect(sx + Player.WIDTH - 18, legsTop, 10, legsHeight);
            gc.setFill(Color.BLACK);
            gc.fillRect(sx + 6, sy + Player.HEIGHT - 5, 14, 8);
            gc.fillRect(sx + Player.WIDTH - 20, sy + Player.HEIGHT - 5, 14, 8);
        }
    }

    private void tickFps(long now) {
        framesSinceLastFpsUpdate++;

        if (lastFpsUpdate == 0) {
            lastFpsUpdate = now;
            return;
        }

        if (now - lastFpsUpdate >= FPS_UPDATE_INTERVAL) {
            currentFps = (int) Math.round(
                    framesSinceLastFpsUpdate * 1_000_000_000.0 / (now - lastFpsUpdate));
            framesSinceLastFpsUpdate = 0;
            lastFpsUpdate = now;
        }
    }
}