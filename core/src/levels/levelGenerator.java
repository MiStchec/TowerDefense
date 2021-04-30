package levels;

import MainRef.ResourceHandler;
import MainRef.TowerDefense;
import abilities.Ability;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import enemy.scorpionEntity.Scorpion;
import enemy.wizardEntity.Wizard;
import levels.menu.testActor;
import levels.menu.MainMenuScreen;

import java.util.*;


public class levelGenerator implements Screen {
    final TowerDefense game;
    testActor pauseButtonActor, abilityButtonActor, spawnButtonActor;
    Window pause, abilityList;
    Stage stage;
    TooltipManager toolTipManager;
    ShapeRenderer shapeRenderer;
    ClickListener placementListener;
    private ResourceHandler resourceHandler = new ResourceHandler();
    SpriteBatch batch;
    LevelOne level;
    PathfindingEnemy scorpionEnemy, wizardEnemy, fireBallAbility;
    Scorpion scorpion;
    Wizard wizard;
    Ability fireBall, damage = new Ability();
    private Array<Vector2> abilityPath;
    private float timePassed;
    private boolean isPaused;
    private boolean rangeCircle = false;
    private String pauseButton = "menuAssets/mainMenuAssets/buttonAssets/button_pause.png";
    private String abilityButton = "menuAssets/mainMenuAssets/buttonAssets/optiButton(FINAL_VERSION).png";
    private Skin uiSkin, fireAbilitySkin, fireBallSkin, windowSkin;

    //TODO
    LinkedList<PathfindingEnemy> enemyList = new LinkedList<>();
    Array<PathfindingEnemy> ability = new Array<>();
    ArrayList<ImageButton> abilityButtonArray = new ArrayList();
    private String fireAbilityToolTip = "Deals "+ damage.getFireDamage() + " Damage against 1 Enemy";
    private String thunderAbilityToolTip = "Deals "+ damage.getThunderDamage() + " Damage to all enemies";
    //TODO

    public levelGenerator(final TowerDefense game) {
        this.game = game;
    }
    //TODO Add Lightning Ability Image
    //TODO Add Lightning Ability Effect

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        toolTipManager = new TooltipManager();
        toolTipManager.initialTime = 0.0f;
        toolTipManager.resetTime = 0.0f;
        toolTipManager.subsequentTime = 0.0f;
        toolTipManager.hideAll();
        toolTipManager.instant();
        Gdx.input.setInputProcessor(stage);
        resourceHandler.loadSound("menuAssets/mainMenuAssets/buttonAssets/buttonClick.mp3", "buttonClickSound");
        uiSkin = new Skin(Gdx.files.internal("menuAssets/mainMenuAssets/menuSkin/skin/uiskin.json"), new TextureAtlas("menuAssets/mainMenuAssets/menuSkin/skin/uiskin.atlas"));
        fireAbilitySkin = new Skin(Gdx.files.internal("abilities/abilitesSkin/fire/fireAbilitySkin.json"), new TextureAtlas("abilities/abilitesSkin/fire/fireAbilitySkin.atlas"));
        windowSkin = new Skin(Gdx.files.internal("menuAssets/mainMenuAssets/menuSkin/testWindowSkin/windowStyle.json"), new TextureAtlas("menuAssets/mainMenuAssets/menuSkin/testWindowSkin/windowStyle.atlas"));
        //----------------------------------------------------------PauseMenu------------------------------------------------------//
        //TODO outsource to a different file
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = windowSkin.getDrawable("default-window");
        pause = new Window("Pause", uiSkin);
        pause.setVisible(false);
        pause.padTop(64);
        pause.setBackground(windowStyle.background);
        //pause.setSize(stage.getWidth() / 2.5f, stage.getHeight() / 2.5f);
        pause.setPosition(stage.getWidth() / 2 - pause.getWidth() / 2, stage.getHeight() / 2 - pause.getHeight() / 2);
        //----------------------------------------------------------PauseMenuButtons------------------------------------------------------//
        TextButton continueButton = new TextButton("Continue the Game",uiSkin);
        TextButton exitButton = new TextButton("Exit to Main Menu", uiSkin);
        exitButton.setSize(250f,250f);
        pause.add(continueButton).row();
        pause.add(exitButton);
        pause.pack();
        //----------------------------------------------------------PauseMenuButtonFunctionality------------------------------------------------------//
        continueButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isPaused = !isPaused;
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                pause.setVisible(false);
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        //--------------------------------------------------------AbilityMenu----------------------------------------------------//
        //TODO outsource to individual file
        abilityList = new Window("Abilities", uiSkin);
        abilityList.setVisible(false);
        abilityList.padBottom(5);
        abilityList.setPosition(stage.getWidth() / 2f, stage.getHeight());
        abilityList.setMovable(true);
        //--------------------------------------------------------AbilityMenuButtons----------------------------------------------------//
        ImageButtonStyle style = new ImageButtonStyle();
        style.imageUp = fireAbilitySkin.getDrawable("fire_up");
        style.imageOver = fireAbilitySkin.getDrawable("fire_over");
        style.imageChecked = fireAbilitySkin.getDrawable("fire_checked");
        final ImageButton fireAbility = new ImageButton(style);
        final ImageButton thunderAbility = new ImageButton(style);
        abilityButtonArray.add(fireAbility);
        abilityButtonArray.add(thunderAbility);
        //--------------------------------------------------------AbilityMenuButtonFunctionality----------------------------------------------------//
        //fireAbility.addListener(new TextTooltip(fireAbilityToolTip, toolTipManager, uiSkin));
        fireAbility.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                if(fireAbility.isChecked()){
                    rangeCircle = true;
                    stage.addListener(placementListener = new ClickListener(Input.Buttons.LEFT) {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if(!fireAbility.isPressed()) {
                                super.clicked(event, x, y);
                                createAbility();
                                setUpAbility(Gdx.input.getX() - fireBall.getWIDTH() / 2f, 720 - Gdx.input.getY() - fireBall.getHEIGHT() / 2f);
                                //Gdx.app.log("Mouse_X", String.valueOf(Gdx.input.getX()));
                                //Gdx.app.log("Mouse_Y", String.valueOf(Gdx.input.getY()));
                                Gdx.app.log("Ability", abilityButtonArray.get(0).toString());
                                fireAbility.setChecked(false);
                                stage.removeListener(placementListener);
                                rangeCircle = !rangeCircle;
                            }
                            else{
                                rangeCircle = !rangeCircle;
                                fireAbility.setChecked(false);
                                stage.removeListener(placementListener);
                            }
                        }
                    });
                }
            }
        });
        thunderAbility.addListener(new TextTooltip(thunderAbilityToolTip, toolTipManager, uiSkin));
        thunderAbility.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                if(thunderAbility.isChecked()){
                    dealThunderDamage();
                    thunderAbility.setChecked(false);
                }
            }
        });
        for (ImageButton imgButton : abilityButtonArray){
            abilityList.add(imgButton);
        }
        abilityList.pack();
        //----------------------------------------------------------GameplayButtons------------------------------------------------------//
        pauseButtonActor = new testActor(pauseButton, Gdx.graphics.getWidth()/100*0.5f, Gdx.graphics.getHeight()/100*89f, 90, 90);
        pauseButtonActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                isPaused = !isPaused;
                pause.setVisible(!pause.isVisible());
            }
        });
        abilityButtonActor = new testActor(abilityButton, Gdx.graphics.getWidth()*0.10f, Gdx.graphics.getHeight()*0.90f, 125,50);
        abilityButtonActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                abilityList.setVisible(!abilityList.isVisible());
            }
        });
        spawnButtonActor = new testActor(abilityButton, Gdx.graphics.getWidth()*0.20f, Gdx.graphics.getHeight()*0.80f, 125,50);
        spawnButtonActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                resourceHandler.getSound("buttonClickSound").play(0.5f);
                damage.setThunderDamage(damage.getThunderDamage() + 5f);
                thunderAbilityToolTip = "Deals "+ damage.getThunderDamage() + " Damage to all enemies";
                Gdx.app.log("Thunder Damage", String.valueOf(damage.getThunderDamage()));
            }
        });

        batch = new SpriteBatch();
        level = new LevelOne();
        level.createBackground();
        stage.addActor(pauseButtonActor);
        stage.addActor(abilityButtonActor);
        stage.addActor(spawnButtonActor);
        stage.addActor(pause);
        stage.addActor(abilityList);
        this.createAllEnemies();
        this.setUpEnemies();
    }

    @Override
    public void render(float delta) {
        level.renderBackground();
        stage.act(Gdx.graphics.getDeltaTime());
        updateToolTips();
        if (!isPaused){
            this.updateAllEntites();
        }
        checkFireAbilityCollision();
        batch.begin();
        drawAllEntites();
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if(rangeCircle){
            drawCircle();
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
        stage.draw();
    }
    //
    public void updateToolTips(){
        fireAbilityToolTip = "Deals "+ damage.getFireDamage() + " Damage against 1 Enemy";
        thunderAbilityToolTip = "Deals "+ damage.getThunderDamage() + " Damage to all enemies";
        abilityButtonArray.get(0).addListener(new TextTooltip(fireAbilityToolTip, toolTipManager, uiSkin));
        abilityButtonArray.get(0).addListener(new TextTooltip(fireAbilityToolTip, toolTipManager, uiSkin));
        abilityButtonArray.get(1).addListener(new TextTooltip(thunderAbilityToolTip, toolTipManager, uiSkin));
    }
    public void checkFireAbilityCollision(){
        if(!(fireBallAbility == null)) {
            Iterator<PathfindingEnemy> abilityIterator = ability.iterator();
            //Gdx.app.log("Array Index",ability.toString());
            for (Iterator<PathfindingEnemy> iterator = enemyList.iterator(); iterator.hasNext(); ) {
                if(abilityIterator.hasNext()) {
                    PathfindingEnemy enemy = iterator.next();
                    if (enemy.getBoundingRectangle().overlaps(ability.get(0).getBoundingRectangle())) {
                        enemy.setLifeCount(enemy.getLifeCount() - damage.getFireDamage());
                        Gdx.app.log(String.valueOf(enemy), String.valueOf(enemy.getLifeCount()));
                        ability.removeValue(ability.get(0),true);
                    }
                    if (enemy.getLifeCount() <= 0) {
                        iterator.remove();
                    }
                }
                else{
                    break;
                }
            }
        }
    }
    public void dealThunderDamage(){
        for (Iterator<PathfindingEnemy> iterator = enemyList.iterator(); iterator.hasNext(); ) {
                PathfindingEnemy enemy = iterator.next();
                enemy.setLifeCount(enemy.getLifeCount() - damage.getThunderDamage());
                Gdx.app.log(String.valueOf(enemy), String.valueOf(enemy.getLifeCount()));
                if (enemy.getLifeCount() <= 0) {
                    iterator.remove();
                }
            }
        }

    public void drawCircle(){
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1,1,1,0.5f);
        shapeRenderer.circle(Gdx.input.getX(), 720 - Gdx.input.getY(), 50f);
        shapeRenderer.end();
    }
    //TODO outsource Abilities to their own files
    public void createAbility(){
        for(ImageButton imageButton: abilityButtonArray){
            if(imageButton.isChecked()){
                Gdx.app.log("Create Ability", String.valueOf(imageButton));
                fireBall = new Ability();
            }
        }
    }
    public Array<Vector2> abilityMovementPath(float x, float y){
        abilityPath = new Array<Vector2>();
        abilityPath.add(new Vector2(x, y));
        return abilityPath;
    }
    public void setUpAbility(float x, float y){
        ability = new Array<>();
        fireBallAbility = new PathfindingEnemy(fireBall.idleFrame(), abilityMovementPath(x, y));
        fireBallAbility.setPosition(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.65f);
        ability.add(fireBallAbility);
    }
    public void createAllEnemies(){
        scorpion = new Scorpion();
        wizard = new Wizard();
    }
    public void drawAllEntites(){
        for(PathfindingEnemy drawEnemy: enemyList){
            drawEnemy.draw(batch);
        }
        for(PathfindingEnemy drawAbility: ability){
            drawAbility.draw(batch);
        }
    }
    public void updateAllEntites(){
        for(PathfindingEnemy updateEnemy: enemyList){
            updateEnemy.update();
        }
        for(PathfindingEnemy updateAbility: ability){
            updateAbility.updateAbility();
        }
    }
    public void setUpEnemies(){
        enemyList = new LinkedList<>();
        //velocity not quite working yet, origin too
        scorpionEnemy = new PathfindingEnemy(scorpion.idleFrame(), LevelOne.levelOnePath(), 20);
        //scorpionEnemy.setOrigin(-150, 150);
        scorpionEnemy.setPosition(-150, 150);
        scorpionEnemy.setSize(scorpion.getWIDTH(), scorpion.getHEIGHT());
        //velocity not quite working yet, origin too
        wizardEnemy = new PathfindingEnemy(wizard.idleFrame(), LevelOne.levelOnePath(), 50);
        //wizardEnemy.setOrigin(-150, 150);
        wizardEnemy.setPosition(-150, 150);
        wizardEnemy.setSize(wizard.getWIDTH(), wizard.getHEIGHT());
        enemyList.add(wizardEnemy);
        enemyList.add(scorpionEnemy);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose(){
        batch.dispose();
        scorpion.getStage().dispose();
        scorpionEnemy.getTexture().dispose();
        wizard.getStage().dispose();
        wizardEnemy.getTexture().dispose();
        level.disposeBackground();
    }
}
