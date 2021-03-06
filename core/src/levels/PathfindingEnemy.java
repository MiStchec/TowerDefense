package levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathfindingEnemy extends Sprite {

    private Vector2 velocity = new Vector2();
    private float speed = 75, tolerance = 3, abilitySpeed = 350;
    private TextureRegion entity;
    public Array<Vector2> getPath() {
        return path;
    }
    protected float timeAlive = 0;
    protected float timeOfDmgTaken = -1;
    public static final float BLINK_TIME_AFTER_DMG = 0.25f;
    private Array<Vector2> path;
    private int waypoint = 0;
    private float lifeCount;
    //TODO
    private TextureAtlas damageAnimationAtlas = new TextureAtlas(Gdx.files.internal("assetsPack/scorpions/scorpionRunning/scorpionPack.atlas"));
    private TextureRegion currentFrame;
    private Animation runningAnimation;
    private float elapsed_time = 0f;
    Array<TextureAtlas.AtlasRegion> damageRunningFrames = damageAnimationAtlas.findRegions("1_enemies_1_run");
    //TODO


    public PathfindingEnemy(TextureRegion entity, Array<Vector2> path){
        super(entity);
        this.path = path;
        this.setPosition(path.first().x, path.first().y);
        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
    public PathfindingEnemy(TextureRegion entity, Array<Vector2> path, float x, float y){
        super(entity);
        this.path = path;
        this.setPosition(path.first().x, path.first().y);
        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.setPosition(x, y);
    }

    public PathfindingEnemy(TextureRegion entity, float x, float y){
        super(entity);
        this.setPosition(x, y);
    }
    public PathfindingEnemy(TextureRegion entity, float lifeCount, Array<Vector2> path){
        super(entity);
        this.lifeCount = lifeCount;
        this.path = path;
        this.setPosition(path.first().x, path.first().y);
        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public PathfindingEnemy(TextureRegion entity, Array<Vector2> path, float lifeCount){
        super(entity);
        this.path = path;
        this.lifeCount = lifeCount;
        this.setPosition(LevelOne.levelOneTopPath().first().x, LevelOne.levelOneTopPath().first().y);
    }
    public void updateAbility(){
        float angle = (float) Math.atan2(path.get(waypoint).y - getY(), path.get(waypoint).x - getX());
        velocity.set((float) Math.cos(angle) * abilitySpeed, (float) Math.sin(angle) * abilitySpeed);
        setPosition(getX() + velocity.x * Gdx.graphics.getDeltaTime(), getY() + velocity.y * Gdx.graphics.getDeltaTime());
        if(isWaypointReached(abilitySpeed)){
            setPosition(path.get(waypoint).x, path.get(waypoint).y);
            if(waypoint + 1 >= path.size){
                waypoint = 0;
            }
            else{
                waypoint++;
            }
        }
    }
    public void update(SpriteBatch batch, Array<Vector2> path, float delta){
        super.draw(batch);
        timeAlive+= delta;
        this.path = path;
        float angle = (float) Math.atan2(path.get(waypoint).y - getY(), path.get(waypoint).x - getX());
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);

        setPosition(getX() + velocity.x * Gdx.graphics.getDeltaTime(), getY() + velocity.y * Gdx.graphics.getDeltaTime());

        if(isWaypointReached(speed)){
            setPosition(path.get(waypoint).x, path.get(waypoint).y);
            if(waypoint + 1 >= path.size){
                waypoint = 0;
            }
            else{
                waypoint++;
            }
        }
    }
    public void updateAnimation(SpriteBatch batch, Array<Vector2> path, float delta, TextureRegion currentFrame){
        //super.draw(batch);
        timeAlive+= delta;
        this.path = path;
        float angle = (float) Math.atan2(path.get(waypoint).y - getY(), path.get(waypoint).x - getX());
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);

        batch.draw(currentFrame, getX() + velocity.x * Gdx.graphics.getDeltaTime(),getY() + velocity.y * Gdx.graphics.getDeltaTime());
        setPosition(getX() + velocity.x * Gdx.graphics.getDeltaTime(), getY() + velocity.y * Gdx.graphics.getDeltaTime());

        if(isWaypointReached(speed)){
            setPosition(path.get(waypoint).x, path.get(waypoint).y);
            if(waypoint + 1 >= path.size){
                waypoint = 0;
            }
            else{
                waypoint++;
            }
        }

    }
    public void updateBossAnimation(SpriteBatch batch, Array<Vector2> path, float delta, TextureRegion currentFrame, float speed){
        //super.draw(batch);
        timeAlive+= delta;
        this.path = path;
        float angle = (float) Math.atan2(path.get(waypoint).y - getY(), path.get(waypoint).x - getX());
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);

        batch.draw(currentFrame, getX() + velocity.x * Gdx.graphics.getDeltaTime(),getY() + velocity.y * Gdx.graphics.getDeltaTime());
        setPosition(getX() + velocity.x * Gdx.graphics.getDeltaTime(), getY() + velocity.y * Gdx.graphics.getDeltaTime());

        if(isWaypointReached(speed)){
            setPosition(path.get(waypoint).x, path.get(waypoint).y);
            if(waypoint + 1 >= path.size){
                waypoint = 0;
            }
            else{
                waypoint++;
            }
        }
    }
    public void setPosition(){
        setPosition(getX() + velocity.x * Gdx.graphics.getDeltaTime(), getY() + velocity.y * Gdx.graphics.getDeltaTime());
    }
    public void setBossPosition(float x, float y){
        setPosition(x, y);
    }

    public boolean isWaypointReached(float speed){
        return path.get(waypoint).x - getX() <= speed / tolerance * Gdx.graphics.getDeltaTime() && path.get(waypoint).y - getY() <= speed / tolerance * Gdx.graphics.getDeltaTime() ;
    }
    public float getLifeCount() {
        return lifeCount;
    }

    public void setLifeCount(float lifeCount) {
        this.lifeCount = lifeCount;
    }


}
