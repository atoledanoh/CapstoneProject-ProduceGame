package com.atoledano.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Anim extends Component {
    private final HashMap<String, Animation> anims;

    public Anim() {
        anims = new HashMap<>();
    }
    
    public Anim(HashMap<String, Animation> anims) {
        this.anims = anims;
    }

    public TextureRegion getTextureRegion(String state, float stateTime) {
        return anims.get(state).getKeyFrame(stateTime);
    }

}
