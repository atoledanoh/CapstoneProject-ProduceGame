package com.atoledano.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Renderer extends Component {

    private final Sprite sprite;

    public Renderer(TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
    }

    public Renderer(TextureRegion textureRegion, float width, float height) {
        this(textureRegion);
        sprite.setSize(width, height);
    }

    public void setRegion(TextureRegion textureRegion) {
        sprite.setRegion(textureRegion);
    }

    public void setOrigin(float x, float y) {
        sprite.setOrigin(x, y);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x - sprite.getOriginX(), y - sprite.getOriginY());
    }

    public void setRotation(float degrees) {
        sprite.setRotation(degrees);
    }

    public void setScale(float x, float y) {
        sprite.setScale(x, y);
    }

    public void setColor(Color tint) {
        sprite.setColor(tint);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
