package com.atoledano.components;

import com.artemis.Component;

public class ProduceCrate extends Component {

    public Type type;


    public ProduceCrate() {
        type = Type.getRandomType();
    }
}
