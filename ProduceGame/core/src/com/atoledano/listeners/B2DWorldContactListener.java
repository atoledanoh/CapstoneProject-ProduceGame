package com.atoledano.listeners;

import com.artemis.Entity;
import com.atoledano.components.*;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.physics.box2d.*;

public class B2DWorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        //enemy AND...
        if (fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT || fixtureB.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
            //...produce
            if (fixtureA.getFilterData().categoryBits == GameManager.PRODUCE_BIT) {
                Entity produceEntity = (Entity) fixtureA.getBody().getUserData();
                Produce produce = produceEntity.getComponent(Produce.class);
                Entity enemyEntity = (Entity) fixtureB.getBody().getUserData();
                Enemy enemy = enemyEntity.getComponent(Enemy.class);
                //check customer needs
                if (enemy.needs == produce.type) {
                    // consume power-up and kill enemy
                    produce.isDestroyed = true;
                    enemy.receivedDamage++;
                }
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PRODUCE_BIT) {
                Entity produceEntity = (Entity) fixtureB.getBody().getUserData();
                Produce produce = produceEntity.getComponent(Produce.class);
                Entity enemyEntity = (Entity) fixtureA.getBody().getUserData();
                Enemy enemy = enemyEntity.getComponent(Enemy.class);
                //check customer needs
                if (enemy.needs == produce.type) {
                    // consume power-up and kill enemy
                    produce.isDestroyed = true;
                    enemy.receivedDamage++;
                }
            }
        }

        // player
        if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT || fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
            if (fixtureA.getFilterData().categoryBits == GameManager.PRODUCECRATE_BIT) {

                Entity produceCrateEntity = (Entity) fixtureA.getBody().getUserData();
                ProduceCrate produceCrate = produceCrateEntity.getComponent(ProduceCrate.class);
                Entity playerEntity = (Entity) fixtureB.getBody().getUserData();
                Player player = playerEntity.getComponent(Player.class);
                if (player.produceLeft < player.produceCapacity) {
                    player.types.add(produceCrate.type);
                    GameManager.types.add(produceCrate.type);
                    player.produceLeft++;
                }
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PRODUCECRATE_BIT) {

                Entity produceCrateEntity = (Entity) fixtureB.getBody().getUserData();
                ProduceCrate produceCrate = produceCrateEntity.getComponent(ProduceCrate.class);
                Entity playerEntity = (Entity) fixtureB.getBody().getUserData();
                Player player = playerEntity.getComponent(Player.class);
                if (player.produceLeft < player.produceCapacity) {
                    player.types.add(produceCrate.type);
                    GameManager.types.add(produceCrate.type);
                    player.produceLeft++;
                }
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
