package dotteri.MiceInvaders;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class CircleCollision {

	public Circle circle = null;
	Vector2 offset = null;
	
	static int BULLET = 0x01;
	static int VULNERABLE_TO_BULLET = 0x2;
	static int CAT = 0x04;
	static int ENEMY = 0x08;
	static int POWERUP = 0x10;
	
	private int flags = 0;
	
	boolean damage_player_on_contact = false;
	Effect effect;
	
	public static enum Owner{
		None,
		Cat,
		Enemy
	}
	Owner owner = Owner.None;
	int player;
	
	public static interface Effect{
		public void doTask(int i, int i2);
	}
	
	static private class PowerUpEffect_AddLife implements Effect{

		@Override
		public void doTask(int i, int i2) {
			Engine.Engine.life[i2].quitDamage(1);
		}
		
	}
	static public PowerUpEffect_AddLife PowerUpEffect_AddLife = new PowerUpEffect_AddLife();
		
	public CircleCollision(){
		circle = new Circle();
		offset = new Vector2();
	}
	
	public void reset(){
		flags = 0;
		damage_player_on_contact = false;
		effect = null;
	}
	
	public void update(float x, float y){
		circle.x = x + offset.x;
		circle.y = y + offset.y;
	}
	
	public void update(final Vector2 position){
		update(position.x, position.y);
	}
	
	public Circle getCircle(){
		return circle;
	}
	
	public Vector2 getOffset(){
		return offset;
	}
			
	public boolean is(int flag){
		if ((flags & flag) == flag) return true;
		return false;
	}
	
	public void setFlag(int flag){
		flags |= flag;
	}
	
	public void quitFlag(int flag){
		if (is(flag)) flags ^= flag;
	}
		
	public void setOwner(Owner new_owner){
		owner = new_owner;
	}
	
	public Owner getOwner(){
		return owner;
	}
	
}
