package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;

public class Explosion extends Component {
    
    public static short defaultMaskBits = GameManager.PLAYER_BIT | GameManager.BOMB_BIT | GameManager.ENEMY_BIT | GameManager.BREAKABLE_BIT;
    
}
