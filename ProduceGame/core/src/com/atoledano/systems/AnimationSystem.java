package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.Anim;
import com.atoledano.components.Renderer;
import com.atoledano.components.State;

public class AnimationSystem extends IteratingSystem {

    ComponentMapper<Renderer> rendererComponentMapper;
    ComponentMapper<Anim> animComponentMapper;
    ComponentMapper<State> stateComponentMapper;

    public AnimationSystem() {
        super(Aspect.all(Renderer.class, Anim.class, State.class));
    }

    @Override
    protected void process(int i) {
        Renderer renderer = rendererComponentMapper.get(i);
        Anim anim = animComponentMapper.get(i);
        State state = stateComponentMapper.get(i);

        renderer.setRegion(anim.getTextureRegion(state.getCurrentState(), state.getStateTime()));

    }

}
