package dotteri.MiceInvaders;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MultiGraphic implements IGraphic {
	
	float[] time;
	public Animation[] animation;
	public Sprite[] sprite;
	int used = 0;
	int n = 0;
	boolean keep_relative_position_x = false;
	boolean keep_relative_position_y = false;
	float offset_x = 0.f;
	float offset_y = 0.f;
	
	public void init(int n){
		used = 0;
		this.n = n;
		animation = new Animation[n];
		sprite = new Sprite[n];
		MiceInvaders.fillArrayWithConstructor(sprite, Sprite.class);
		time = new float[n];
		resetTime();
	}
	
	public void setKeepRelativePositionX(boolean set){
		keep_relative_position_x = set;	
	}
	
	public void setKeepRelativePositionY(boolean set){
		keep_relative_position_y = set;
		
	}
	
	public void setOffsetX(float offset){
		offset_x = offset;
	}
	
	public void setOffsetY(float offset){
		offset_y = offset;
	}
	
	public int getTotal(){
		return n;
	}
	
	public void setUsed(int used){
		this.used = used;
	}
	
	public int getUsed(){
		return used;
	}
	
	public void setY(float y){
		if (keep_relative_position_y){
			float i = 0.f;
			for(Sprite a_sprite: sprite){
				a_sprite.setY(y + offset_y * i);
				i += 1.f;
			}
			return;
		}
		for(Sprite a_sprite: sprite){
			a_sprite.setY(y);
		}
	}
	
	public void setX(float x){
		if (keep_relative_position_x){
			float i = 0.f;
			for(Sprite a_sprite: sprite){
				a_sprite.setX(x + offset_x * i);
				i += 1.f;
			}
			return;
		}
		for(Sprite a_sprite: sprite){ 
			a_sprite.setX(x);
		}
	}
	
	public float[] getTime(){
		return time;
	}
	
	public Animation[] getAnimation(){
		return animation;
	}
	
	public Sprite[] getSprite(){
		return sprite;
	}
	
	public void updateAndDraw(float delta, SpriteBatch batch){
		for(int i = 0; i < used; i += 1){
			time[i] += delta;
			if (animation[i] != null){
				sprite[i].setRegion(animation[i].getKeyFrame(time[i], true));
			}
			sprite[i].draw(batch);
		}
	}
	
	public void resetTime(){
		for (int i = 0; i < n; i += 1) time[i] = 0.f;
	}

	@Override
	public void update(float delta, float x, float y) {
		setX(x);
		setY(y);
		for(int i = 0; i < used; i += 1){
			time[i] += delta;
			if (animation[i] != null){
				sprite[i].setRegion(animation[i].getKeyFrame(time[i], true));
			}
		}
		
	}

	@Override
	public void draw(SpriteBatch batch) {
		for (int i = 0; i < used; i += 1) sprite[i].draw(batch);
	}
	
}
