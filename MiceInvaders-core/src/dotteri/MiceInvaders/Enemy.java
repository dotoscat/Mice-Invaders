package dotteri.MiceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import dotteri.MiceInvaders.CircleCollision.Owner;
import dotteri.MiceInvaders.MessageSystem.Message;
import dotteri.MiceInvaders.MessageSystem.Type;

public class Enemy {
		
	private float time_move;
	private float maxtime_move;
	
	private float time_shoot;
	float maxtime_shoot;
	
	int i_player = -1;
	
	Circle sensor;
	Vector2 sensor_offset;
	
	IEnemyBrain brain = null;
	public long point = 0L;
	static IEnemyBrain bouncer_brain = new BouncerBrain();
	static IEnemyBrain invader_brain = new InvaderBrain();
	static IEnemyBrain monster_brain = new MonsterBrain();
	static IEnemyBrain boss_brain = new BossBrain();
	
	public void reset(){
		time_move = 0.f;
		maxtime_move = 0.f;
		time_shoot = 0.f;
		maxtime_shoot = 0.f;
		point = 0L;
		brain = null;
	}
	
	static public class BouncerBrain implements IEnemyBrain{
		
		@Override
		public void think(Enemy enemy, int i_enemy) {
			final Vector2 player_position = Engine.Engine.physics[Engine.Engine.player[enemy.i_player].entity].position;
			final Vector2 enemy_position = Engine.Engine.physics[i_enemy].position;
			
			final float angle = MathUtils.atan2(player_position.y - enemy_position.y,
					player_position.x - enemy_position.x);
			final float vel_x = MathUtils.cos(angle) * Engine.Engine.cheese_speed * 3f;
			final float vel_y = MathUtils.sin(angle) * Engine.Engine.cheese_speed * 3f;
			
			enemy.move(vel_x, vel_y, 1.f, i_enemy);

		}		
	}
	
	static public class InvaderBrain implements IEnemyBrain{
		
		@Override
		public void think(Enemy enemy, int i_enemy) {
			final Vector2 player_position = Engine.Engine.physics[Engine.Engine.player[enemy.i_player].entity].position;
			final Vector2 enemy_position = Engine.Engine.physics[i_enemy].position;
			
			if (!enemy.isMoving()){
			
				float angle = MathUtils.atan2(player_position.y - enemy_position.y, player_position.x - enemy_position.x);
				float mod_angle = MathUtils.random(0.f, 45.f) * MathUtils.degreesToRadians;
				mod_angle = MathUtils.randomBoolean() ? mod_angle : -mod_angle;
				angle += mod_angle;
				enemy.move( MathUtils.cos(angle) * Engine.Engine.cheese_speed * 2.f, MathUtils.sin(angle) * Engine.Engine.cheese_speed * 2.f, MathUtils.random(1.f, 3.f), i_enemy);
			}
			
			if (!enemy.isShooting() && MathUtils.random(60) == 0){
				Engine.Engine.shoot[i_enemy].setShootsBySec(0.5f);
				enemy.shoot(MathUtils.random(1.f, 3.f), enemy.i_player);
			}
			
		}
		
	}
	
	static public class MonsterBrain implements IEnemyBrain{

		@Override
		public void think(Enemy enemy, int i_enemy) {
			// TODO Auto-generated method stub			
			if (!enemy.isShooting() && MathUtils.random(60) == 0){
				Engine.Engine.shoot[i_enemy].setShootsBySec(0.25f);
				enemy.shoot(MathUtils.random(1.f, 3.f), enemy.i_player);
			}
			
			if (enemy.isMoving()){
				Physics physics = Engine.Engine.physics[i_enemy];
				if (physics.position.x < 0.f && physics.velocity.x < 0.f){
					enemy.moveX(-physics.velocity.x, 1.f, i_enemy);
				}
				else if (physics.position.x > MiceInvaders.VWIDTH && physics.velocity.x > 0.f){
					enemy.moveX(-physics.velocity.x, 1.f, i_enemy);
				}
				final float vel_x = physics.velocity.x;
				if (vel_x <= 0.f && !Engine.Engine.solo_graphic[i_enemy].sprite.isFlipX()){
					Engine.Engine.solo_graphic[i_enemy].sprite.flip(true, false);
				}
				if (vel_x > 0.f && Engine.Engine.solo_graphic[i_enemy].sprite.isFlipX()){
					Engine.Engine.solo_graphic[i_enemy].sprite.flip(true, false);
				}
				return;
			}
			
			final Vector2 player_position = Engine.Engine.physics[Engine.Engine.player[enemy.i_player].entity].getPosition();
			
			float vel_x = ( Engine.Engine.physics[i_enemy].position.x - player_position.x) >= 0.f ? -MiceInvaders.PPM*2.f : MiceInvaders.PPM*2.f;

			if (Engine.Engine.floor_collision[i_enemy].touch_floor)
				enemy.moveX(vel_x, MathUtils.random(7f, 12f), i_enemy);
			if (vel_x <= 0.f && !Engine.Engine.solo_graphic[i_enemy].sprite.isFlipX()){
				Engine.Engine.solo_graphic[i_enemy].sprite.flip(true, false);
			}
			if (vel_x > 0.f && Engine.Engine.solo_graphic[i_enemy].sprite.isFlipX()){
				Engine.Engine.solo_graphic[i_enemy].sprite.flip(true, false);
			}
		}
				
	}
	
	static public class BossBrain implements IEnemyBrain{

		@Override
		public void think(Enemy enemy, int i_enemy) {
			// TODO Auto-generated method stub
			Physics enemy_physics = Engine.Engine.physics[i_enemy];
			if (enemy_physics.position.y < Engine.Engine.play_area.y + Engine.Engine.play_area.height / 2.f){
				enemy.addMoveXY(0.f, Engine.Engine.cheese_speed, 1.f, i_enemy);
			}
		}
		
	}
	
	public Enemy(){
		sensor = new Circle();
		sensor_offset = new Vector2();
	}
	
	public void think(int i_enemy){
		//The enemy aims for a player...
		if ( i_player == -1 || Engine.Engine.player[i_player].entity == -1 ){
			for(int i = 0; i < Engine.Engine.players; i += 1){
				if (Engine.Engine.player[i].entity == -1) continue;
				i_player = i;
				break;
			}
		}
		if (i_player == -1 || Engine.Engine.player[i_player].entity == -1) return;
		
		final float dt = Gdx.graphics.getRawDeltaTime();
		time_move += dt;
		time_shoot += dt;
		sensor.x = sensor_offset.x + Engine.Engine.physics[i_enemy].position.x;
		sensor.y = sensor_offset.y + Engine.Engine.physics[i_enemy].position.y;
				
		if (isShooting()){
			shootToPlayer(i_enemy, Engine.Engine);
		}
		if (!isMoving()){
			Engine.Engine.physics[i_enemy].getVelocity().set(0.f, 0.f);
		}
		if (brain == null) return;
		brain.think(this, i_enemy);
	}
	
	public void setBrain(IEnemyBrain brain){
		this.brain = brain;
	}
	
	public void move(float vel_x, float vel_y, float duration, int i_enemy){
		maxtime_move = duration;
		time_move = 0.f;
		Engine.Engine.physics[i_enemy].getVelocity().set(vel_x, vel_y);
	}
	
	public void moveX(float vel_x, float duration, int i_enemy){
		maxtime_move = duration;
		time_move = 0.f;
		Engine.Engine.physics[i_enemy].velocity.x = vel_x;
	}
	
	public void moveY(float vel_y, float duration, int i_enemy){
		maxtime_move = duration;
		time_move = 0.f;
		Engine.Engine.physics[i_enemy].velocity.y = vel_y;
	}
	
	public void addMoveXY(float vel_x, float vel_y, float duration, int i_enemy){
		maxtime_move = duration;
		time_move = 0.f;
		Engine.Engine.physics[i_enemy].velocity.x += vel_x;
		Engine.Engine.physics[i_enemy].velocity.y += vel_y;
	}
		
	public boolean isMoving(){
		return maxtime_move > 0.f && time_move < maxtime_move;
	}
	
	public void shoot(float duration, int i_player){
		maxtime_shoot = duration;
		time_shoot = 0.f;
		this.i_player = i_player;
	}
	
	public boolean isShooting(){
		return maxtime_shoot > 0.f && time_shoot < maxtime_shoot;
	}
	
	static public void shootToPlayer(int enemy, Engine game){
		if (game.player[ game.enemy[enemy].i_player].entity == -1)return;
		final Vector2 enemy_position = game.physics[enemy].position;
		final Vector2 player_position = game.physics[ game.player[ game.enemy[enemy].i_player ].entity ].position;
		if (game.shoot[enemy].shoot()){
			final float angle = MathUtils.atan2(player_position.y - enemy_position.y, player_position.x - enemy_position.x);
			Message message = MessageSystem.MessageSystem.addMessage(Type.CreateBullet);
			message.position_x = enemy_position.x;
			message.position_y = enemy_position.y;
			message.owner = Owner.Enemy;
			message.color = Color.YELLOW;
			message.vel_x = MathUtils.cos(angle) * MiceInvaders.PPM * 3.f;
			message.vel_y = MathUtils.sin(angle) * MiceInvaders.PPM * 3.f;
			MiceInvaders.MiceInvaders.playSound("data/laser_shot.wav", 2f);
		}
	}
	
	static public void shootToPosition(int enemy, Engine game, float pos_x, float pos_y){
		final Circle enemy_position = game.circle_collision[enemy].getCircle();
		if (game.shoot[enemy].shoot()){
			final float angle = MathUtils.atan2(pos_y - enemy_position.y, pos_x - enemy_position.x);
			Message message = MessageSystem.MessageSystem.addMessage(Type.CreateBullet);
			message.position_x = enemy_position.x;
			message.position_y = enemy_position.y;
			message.owner = Owner.Enemy;
			message.color = Color.YELLOW;
			message.vel_x = MathUtils.cos(angle) * MiceInvaders.PPM * 3.f;
			message.vel_y = MathUtils.sin(angle) * MiceInvaders.PPM * 3.f;
			MiceInvaders.MiceInvaders.playSound("data/laser_shot.wav", 2f);
		}
	}
	
	static public void shootToThatDirection(int enemy, Engine game, float vel_x, float vel_y){
		final Circle enemy_position = game.circle_collision[enemy].getCircle();
		if (game.shoot[enemy].shoot()){
			Message message = MessageSystem.MessageSystem.addMessage(Type.CreateBullet);
			message.position_x = enemy_position.x;
			message.position_y = enemy_position.y;
			message.owner = Owner.Enemy;
			message.color = Color.YELLOW;
			message.vel_x = vel_x;
			message.vel_y = vel_y;
			MiceInvaders.MiceInvaders.playSound("data/laser_shot.wav", 2f);
		}
	}
	
}
