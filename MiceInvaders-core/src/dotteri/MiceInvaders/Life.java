package dotteri.MiceInvaders;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import dotteri.MiceInvaders.MessageSystem.Message;
import dotteri.MiceInvaders.MessageSystem.Type;

public class Life {

	int life;
	int damage;
	
	float invicibility = 0.f;
	
	int last_entity;
	
	static enum State{
		ReceiveDamage,
		Invicible
	}
	
	static public interface Callback{
		public void doTask(Life life, int i);
	}
	private static class EnemyDamaged implements Callback{

		@Override
		public void doTask(Life life, int i) {
			MiceInvaders.MiceInvaders.playSound("data/hit.wav", 1.f + MathUtils.random(-0.1f, 0.1f));
		}
		
	}
	static public EnemyDamaged EnemyDamaged = new EnemyDamaged();
	
	protected static class EnemyDies implements Callback{

		@Override
		public void doTask(Life life, int i) {
			// TODO Auto-generated method stub
			Physics[] physics = Engine.Engine.physics;
			{
				Message message = MessageSystem.MessageSystem.addMessage(Type.CreateExplosion);
				final Vector2 entity_position = physics[i].position;
				message.position_x = entity_position.x;
				message.position_y = entity_position.y;
				message.duration = 0.25f;
			}
			{
				Message message_powerup = MessageSystem.MessageSystem.addMessage(Type.CreatePowerUp);
				final String[] powerup_key = {"life", "multiplier"};
				message_powerup.position_x = physics[i].position.x;
				message_powerup.position_y = physics[i].position.y;
				message_powerup.key = powerup_key[MathUtils.random(powerup_key.length-1)];
			}
			if (Engine.Engine.entity_flags[life.last_entity] != 0){
				Engine.Engine.player_input[life.last_entity].points += Engine.Engine.enemy[i].point;}
			Engine.Engine.enemies -= 1;
			Engine.Engine.cheese_speed += MiceInvaders.PPM;
			MiceInvaders.MiceInvaders.playSound("data/explosion.wav", 1.f + MathUtils.random(-0.1f, 0.1f));
		}
		
	}
	static public EnemyDies EnemyDies = new EnemyDies();
	
	static public class BossDefeated implements Life.Callback{

		@Override
		public void doTask(Life life, int i) {
			// TODO Auto-generated method stub
			Life.EnemyDies.doTask(life, i);
			Engine.Engine.boss_is_running = false;
		}
		
	}
	public static BossDefeated BossDefeated = new BossDefeated();
	
	static private class PlayerDies implements Life.Callback{

		@Override
		public void doTask(Life life, int i) {
			Message message = MessageSystem.MessageSystem.addMessage(Type.CreateExplosion);
			final Physics physics = Engine.Engine.physics[i];
			message.position_x = physics.position.x;
			message.position_y = physics.position.y;
			message.duration = 2.f;
		}
		
	}
	static public PlayerDies PlayerDies = new PlayerDies();
	
	Callback callbackDamage;
	Callback callbackDeath;
	
	State state = State.ReceiveDamage;
	float time = 0.f;
	final static float INVICIBILITY_BLINK = 0.12f;
	float blink_time = 0.f;
	boolean show = true;
	
	public void reset(){
		damage = 0;
		state = State.ReceiveDamage;
		time = 0.f;
		invicibility = 0.f;
		blink_time = 0.f;
		show = true;
		callbackDamage = null;
		callbackDeath = null;
	}
	
	public void setLife(int life){
		this.life = life;
	}
	
	public int getLife(){
		return life;
	}
	
	public void setDamage(int amount){
		damage = amount;
	}
	
	public void quitDamage(int amount){
		damage -= amount;
		if (damage < 0) damage = 0;
	}
	
	public void addDamage(int amount, int i){
		if (state == State.Invicible)return;
		damage += amount;
		if (damage < life){
			if (callbackDamage != null) callbackDamage.doTask(this, i);
		}
	}
	
	public void setInvicible(float new_invicibility){
		if (state == State.Invicible)return;
		invicibility = new_invicibility;
		state = State.Invicible;
		time = 0.f;
		blink_time = 0.f;
	}
	
	public boolean isInvicible(){
		return state == State.Invicible;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public boolean isDefeated(){
		return damage >= life;
	}
	
	public int getRemainingLife(){
		if (isDefeated()) return 0;
		return life - damage;
	}
	
	public void update(float delta){
		if (state == State.Invicible){
			time += delta;
			blink_time += delta;
			if (blink_time > INVICIBILITY_BLINK){
				show = !show;
				blink_time = 0.f;
			}
			if (time > invicibility){
				state = State.ReceiveDamage;
			}
		}
	}
	
	public boolean show(){
		if (state == State.ReceiveDamage) return true;
		return show;
	}
	
}
