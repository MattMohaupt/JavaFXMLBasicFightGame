package com.example.entity;

public class Enemy {

    private double x;
    private final double width;
    private final double height;
    private final double patrolMin;
    private final double patrolMax;
    private final double speed;    // now in px/s
    private double direction = 1;
    private boolean alive = true;
    private double hitFlash = 0;   // seconds remaining

    public Enemy(double x, double width, double height,
                 double patrolMin, double patrolMax, double speed) {
        this.x = x;
        this.width = width;
        this.height = height;
        this.patrolMin = patrolMin;
        this.patrolMax = patrolMax;
        this.speed = speed;
    }

    /** @param dt delta time in seconds */
    public void update(double groundY, double dt) {
        if (!alive) return;
        x += direction * speed * dt;
        if (x < patrolMin) { x = patrolMin; direction = 1; }
        if (x > patrolMax) { x = patrolMax; direction = -1; }
        if (hitFlash > 0) {
            hitFlash -= dt;
            if (hitFlash < 0) hitFlash = 0;
        }
    }

    public void kill() { alive = false; }

    public double getTop(double groundY) { return groundY - height; }
    public double getX()        { return x; }
    public double getWidth()    { return width; }
    public double getHeight()   { return height; }
    public boolean isAlive()    { return alive; }
    public double getHitFlash() { return hitFlash; }
}