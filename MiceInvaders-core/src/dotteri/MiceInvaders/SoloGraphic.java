package dotteri.MiceInvaders;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class SoloGraphic implements IGraphic {
	
	Animation animation;
	Sprite sprite;
	Vector2 origin;
	float time;
	
	public SoloGraphic(){
		animation = null;
		sprite = new Sprite();
		origin = new Vector2();
	}
	
	public void reset(){
		origin.set(0.f, 0.f);
		time = 0.f;
	}
	
	public void setAnimation(Animation animation){
		this.animation = animation;
	}
	
	public Animation getAnimation(){
		return animation;
	}
	
	public Sprite getSprite(){
		return sprite;
	}
		
	public void setOrigin(float x, float y){
		origin.x = x;
		origin.y = y;
		sprite.setOrigin(x, y);
	}

	@Override
	public void update(float delta, float x, float y) {
		time += delta;
		sprite.setPosition(x + origin.x, y + origin.y);
		if (animation == null) return;
		final boolean flip_x = sprite.isFlipX();
		sprite.setRegion(animation.getKeyFrame(time, true));
		sprite.flip(flip_x, false);
	}

	@Override
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

}
