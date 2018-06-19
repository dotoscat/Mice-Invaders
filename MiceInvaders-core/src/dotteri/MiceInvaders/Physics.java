package dotteri.MiceInvaders;

import com.badlogic.gdx.math.Vector2;

public class Physics {
	public Vector2 position;
	public Vector2 velocity;
	public float gravity_factor;
	public float max_falling_speed;
	float push_time = 0.f;
	float total_push_time = 0.f;
	
	public Physics(){
		position = new Vector2();
		velocity = new Vector2();
		gravity_factor = 0.f;
		max_falling_speed = 0.f;
	}
	
	public void reset(){
		push_time = 0.f;
		total_push_time = 0.f;
		gravity_factor = 0.f;
		velocity.set(0.f, 0.f);
		max_falling_speed = 0.f;
	}
	
	public float getGravityFactor(){
		return gravity_factor;
	}
	
	public void setGravityFactor(float factor){
		gravity_factor = factor;
	}
	
	public float getMaxFallingSpeed(){
		return max_falling_speed;
	}
	
	public void setMaxFallingSpeed(float speed){
		max_falling_speed = speed;
	}
	
	public Vector2 getVelocity(){
		return velocity;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void push(float vel_x, float vel_y, float time){
		if (push_time < total_push_time) return;
		total_push_time = time;
		push_time = 0.f;
		velocity.x = vel_x;
		velocity.y = vel_y;
	}
	
	public boolean isBeingPushed(){
		return push_time < total_push_time;
	}
	
	public void update(float delta, float gravity_force){
		if (push_time < total_push_time) push_time += delta;
		position.x += velocity.x * delta;
		velocity.y += gravity_force * gravity_factor * delta;
		if(max_falling_speed < 0.f && velocity.y < max_falling_speed){
			velocity.y = max_falling_speed;
		}
		position.y += velocity.y * delta;
	}
	
}
