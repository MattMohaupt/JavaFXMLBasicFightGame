package com.example.entity;

public class Player {

    public static final double WIDTH = 40;
    public static final double HEIGHT = 80;

    // All units are now PER SECOND.
    public static final double MOVE_SPEED = 240;      // px/s
    public static final double GRAVITY = 1500;          // px/s²
    public static final double JUMP_FORCE = -600;     // px/s (initial upward velocity)

    // Attack timings in SECONDS.
    public static final double PUNCH_DURATION = 0.20;
    public static final double KICK_DURATION = 0.30;
    public static final double ATTACK_COOLDOWN = 0.10;

    // Ranges stay in pixels (they don't depend on time).
    public static final double PUNCH_RANGE = 45;
    public static final double KICK_RANGE = 60;

    private double x;
    private double y;
    private double velocityY;
    private boolean onGround = true;

    private AttackType attack = AttackType.NONE;
    private double attackTimer = 0;      // seconds remaining
    private double attackCooldown = 0;   // seconds remaining

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param dt delta time in seconds since the last frame
     */
    public void update(boolean leftPressed, boolean rightPressed,
                       double groundY, double dt) {

        if (leftPressed)  x -= MOVE_SPEED * dt;
        if (rightPressed) x += MOVE_SPEED * dt;
        if (x < 0) x = 0;

        // Apply gravity
        velocityY += GRAVITY * dt;

        // Apply velocity to position
        y += velocityY * dt;

        // Ground collision
        if (y + HEIGHT >= groundY) {
            y = groundY - HEIGHT;
            velocityY = 0;
            onGround = true;
        }

        // Attack timing
        if (attackTimer > 0) {
            attackTimer -= dt;
            if (attackTimer <= 0) {
                attackTimer = 0;
                attack = AttackType.NONE;
                attackCooldown = ATTACK_COOLDOWN;
            }
        } else if (attackCooldown > 0) {
            attackCooldown -= dt;
            if (attackCooldown < 0) attackCooldown = 0;
        }
    }

    public void jump() {
        if (onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
        }
    }

    public void tryAttack(AttackType type) {
        if (attack != AttackType.NONE) return;
        if (attackCooldown > 0) return;
        attack = type;
        attackTimer = (type == AttackType.PUNCH) ? PUNCH_DURATION : KICK_DURATION;
    }

    public boolean attackHits(double ex, double ey, double ew, double eh, double groundY) {
        if (attack == AttackType.NONE || attackTimer <= 0) return false;

        double reach = (attack == AttackType.PUNCH) ? PUNCH_RANGE : KICK_RANGE;

        double padX = 20;
        double padY = 15;

        double attackLeft  = x + WIDTH - padX;
        double attackRight = x + WIDTH + reach + padX;

        double attackTop    = y - padY;
        double attackBottom = y + HEIGHT + padY;

        if (attack == AttackType.KICK) {
            attackTop    += 15;
            attackBottom += 15;
        }

        return attackRight > ex
                && attackLeft < ex + ew
                && attackBottom > ey
                && attackTop < ey + eh;
    }

    public boolean overlaps(double ex, double ey, double ew, double eh) {
        return x + WIDTH > ex
                && x < ex + ew
                && y + HEIGHT > ey;
    }

    public double getX()             { return x; }
    public void setX(double x)       { this.x = x; }
    public double getY()             { return y; }
    public void setY(double y)       { this.y = y; }
    public double getVelocityY()     { return velocityY; }
    public void setVelocityY(double v){ this.velocityY = v; }
    public boolean isOnGround()      { return onGround; }
    public void setOnGround(boolean b){ this.onGround = b; }
    public AttackType getAttack()    { return attack; }
    public double getAttackTimer()   { return attackTimer; }
}