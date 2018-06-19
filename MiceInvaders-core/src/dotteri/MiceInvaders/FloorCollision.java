package dotteri.MiceInvaders;

import com.badlogic.gdx.math.Vector2;

public class FloorCollision {
	
	public Vector2[] offset;
	private Vector2[] point;
	public boolean touch_floor;
	boolean vulnerable_to_cheese;
	float time_pass;
	final static float TIME_PASS = 0.5f;
	int collide = 0;
	
	static final int ENEMY = 0x01;
	static final int CAT = 0x02;
	
	public void reset(){
		time_pass = 0.f;
		touch_floor = false;
		vulnerable_to_cheese = false;
		collide = 0;
	}
	
	public FloorCollision(){
		offset = new Vector2[2];
		offset[0] = new Vector2();
		offset[1] = new Vector2();
		point = new Vector2[2];
		point[0] = new Vector2();
		point[1] = new Vector2();
		touch_floor = false;
		vulnerable_to_cheese = false;
	}
	
	public void setVulnerableToCheese(boolean set){
		vulnerable_to_cheese = set;
	}
	
	public boolean isVulnerableToCheese(){
		return vulnerable_to_cheese;
	}
	
	public Vector2[] getOffset(){
		return offset;
	}
	
	public void update(float x, float y){
		point[0].x = offset[0].x + x;
		point[0].y = offset[0].y + y;
		point[1].x = offset[1].x + x;
		point[1].y = offset[1].y + y;
	}
	
	public final Vector2[] getPoints(){
		return point;
	}
	
	public void setTouchFloor(boolean touch){
		touch_floor = touch;
	}
	
	public boolean isTouchingFloor(){
		return touch_floor;
	}
	
}
