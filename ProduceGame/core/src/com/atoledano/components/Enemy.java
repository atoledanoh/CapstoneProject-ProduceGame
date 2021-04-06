package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;

public class Enemy extends Component {
    public enum State {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        DAMAGED,
        DYING;

        public static State getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }
    }

    public State state;
    public Type needs;
    public int hp;
    protected float speed;

    public float lifetime; // total time when alive

    private String dieSound;
    public String type;

    public int receivedDamage;

    public Enemy(int hp) {
        this(hp, 2);
    }

    public Enemy(int hp, float speed) {
        this(hp, speed, "EnemyDie.ogg");
    }

    public Enemy(int hp, float speed, String dieSound) {
        this(hp, speed, dieSound, "basic");
    }

    public Enemy(int hp, float speed, String dieSound, String type) {
        needs = Type.getRandomType();
        state = State.getRandomWalkingState();
        this.hp = hp;
        this.speed = speed;
        this.dieSound = dieSound;
        this.type = type;

        lifetime = 0;
        receivedDamage = 0;

        // increase enemy count
        GameManager.enemiesLeft++;
    }

    public void damage(int damage) {
        hp -= damage;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getDieSound() {
        return dieSound;
    }

    public void setDieSound(String dieSound) {
        this.dieSound = dieSound;
    }

}
