package enemy.scorpionEntity;

import MainRef.Assets;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import enemy.Entity;
import levels.LevelOne;


public class Scorpion extends Entity {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;


    public Scorpion() {
        super(WIDTH, HEIGHT, 20, Assets.manager.get(Assets.scorpionEnemy, TextureAtlas.class));
    }

    //TO DO: Skorpion muss schon animiert sein, damit ich nur den Skorpion callen muss

}
