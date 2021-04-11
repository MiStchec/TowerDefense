package enemy.wizardEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import components.Lifebar;
import enemy.Entity;

public class Wizard extends Entity {

    public static final int WIDTH = 110;
    public static final int HEIGHT = 110;

    //Lifebar lifebar = new Lifebar();
    SpriteBatch batch;

    public Wizard() {
        super(WIDTH, HEIGHT, 25, 150, "assetsPack/wizard/wizardRunning/wizardRunning.atlas");
        /*
        lifebar.setZIndex(100);
        lifebar.render();

         */
    }



}
