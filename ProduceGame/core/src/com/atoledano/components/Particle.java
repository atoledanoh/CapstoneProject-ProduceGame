package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Particle extends Component {
    public ParticleEffect particleEffect;

    public Particle(String particleFileString, float x, float y) {
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal(particleFileString), Gdx.files.internal("particles"));
        particleEffect.setPosition(x, y);
        particleEffect.scaleEffect(1 / GameManager.PPM);
        particleEffect.start();
    }

}
