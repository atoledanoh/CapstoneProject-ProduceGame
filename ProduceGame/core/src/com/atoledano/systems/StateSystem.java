package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.State;

public class StateSystem extends IteratingSystem {

    ComponentMapper<State> stateComponentMapper;

    public StateSystem() {
        super(Aspect.all(State.class));
    }

    @Override
    protected void process(int entityId) {
        State state = stateComponentMapper.get(entityId);
        state.addStateTime(world.getDelta());
    }

}
