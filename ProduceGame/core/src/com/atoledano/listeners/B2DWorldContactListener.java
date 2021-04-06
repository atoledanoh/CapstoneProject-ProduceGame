package com.atoledano.listeners;

import com.artemis.Entity;
import com.atoledano.components.Enemy;
import com.atoledano.components.Player;
import com.atoledano.components.PowerUp;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.physics.box2d.*;

public class B2DWorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // explosion
        if (fixtureA.getFilterData().categoryBits == GameManager.EXPLOSION_BIT || fixtureB.getFilterData().categoryBits == GameManager.EXPLOSION_BIT) {
            // explode enemy
            if (fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
                Entity e = (Entity) fixtureA.getBody().getUserData();
                Enemy enemy = e.getComponent(Enemy.class);
                enemy.receivedDamage++;
            } else if (fixtureB.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
                Entity e = (Entity) fixtureB.getBody().getUserData();
                Enemy enemy = e.getComponent(Enemy.class);
                enemy.receivedDamage++;
            }

            //enemy
            else if (fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT || fixtureB.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
                //power up
                if (fixtureA.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
                    Entity powerUpEntity = (Entity) fixtureA.getBody().getUserData();
                    PowerUp powerUp = powerUpEntity.getComponent(PowerUp.class);
                    Entity enemyEntity = (Entity) fixtureB.getBody().getUserData();
                    Enemy enemy = enemyEntity.getComponent(Enemy.class);
                    //check customer needs
                    if (enemy.needs == powerUp.type) {
                        // consume power-up and kill enemy
                        powerUp.isDestroyed = true;
                        enemy.receivedDamage++;
                    }
                } else if (fixtureB.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
                    Entity powerUpEntity = (Entity) fixtureB.getBody().getUserData();
                    PowerUp powerUp = powerUpEntity.getComponent(PowerUp.class);
                    Entity enemyEntity = (Entity) fixtureA.getBody().getUserData();
                    Enemy enemy = enemyEntity.getComponent(Enemy.class);
                    //check customer needs
                    if (enemy.needs == powerUp.type) {
                        // consume power-up and kill enemy
                        powerUp.isDestroyed = true;
                        enemy.receivedDamage++;
                    }
                }
//            //player
//            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
//                Entity e = (Entity) fixtureA.getBody().getUserData();
//                Player player = e.getComponent(Player.class);
//                //todo do stuff
//            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
//                Entity e = (Entity) fixtureB.getBody().getUserData();
//                Player player = e.getComponent(Player.class);
//                //todo do stuff
//            }
            }
            // player
            else if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT || fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                //todo do stuff
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
