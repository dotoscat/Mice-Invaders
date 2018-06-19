package dotteri.MiceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import dotteri.MiceInvaders.CircleCollision.Owner;
import dotteri.MiceInvaders.Menu.Element;
import dotteri.MiceInvaders.Menu.Selector;
import dotteri.MiceInvaders.MessageSystem.Message;
import dotteri.MiceInvaders.MessageSystem.Type;

public class Engine implements Screen, InputProcessor, ControllerListener{

	final static public Engine Engine = new Engine();
	
	enum State{
		Running,
		RunningBoss,
		Pause;
	}

	final static int ENTITIES = 128;
	final static int COMPONENT_NONE = 0;
	final static int COMPONENT_PHYSICS = 0x0001;
	final static int COMPONENT_SOLO_GRAPHIC = 0x0002;
	//final static int COMPONENT_PLAYER_INPUT = 0x0004;
	final static int COMPONENT_FLOOR_COLLISION = 0x0008;
	final static int COMPONENT_FLOOR = 0x0010;
	final static int COMPONENT_MULTI_GRAPHIC = 0x0020;
	final static int COMPONENT_LIFE = 0x0040;
	final static int COMPONENT_ENEMY = 0x0080;
	final static int COMPONENT_CIRCLE_COLLISION = 0x0100;
	final static int COMPONENT_SHOOT = 0x0200;
	final static int COMPONENT_LIFE_SPAN = 0x0400;
	final static float JUMP = MiceInvaders.PPM*12.f;
	final static float PLAYER_SPEED = MiceInvaders.PPM*7.f;
	final static float PLATFORM_DISTANCE = MiceInvaders.PPM * 3.f;
	final static float MAX_CHEESE_SPEED = MiceInvaders.PPM * 2.f;
	final static float MIN_CHEESE_SPEED = MiceInvaders.PPM;
	final static float OBJECTIVE = 512.f;
	final static int TIME = 0;//PPM = 1 sec
	final static int PLAYERS = 4;
	final static float CHEESE_DAMP_SPEED = MiceInvaders.PPM / 4.f;
	final static String[] enemy_type = {"bouncer", "invader", "monster"};
	final static int[] enemy_life = {1, 2, 3};
	IGraphic[] layer_1;
	IGraphic[] layer_2;
	IGraphic[] layer_3;
	final static float GRAVITY = -JUMP*1.5f;
	float platform_distance = 0.f;
	public float cheese_speed = MiceInvaders.PPM;
	int enemies = 0;
	int ENEMIES_PER_PLAYER = 3;
	Enemy[] enemy = null;
	private float enemy_spawn_time = 0.f;
	StringBuilder engine_message = new StringBuilder();
	int[] entity_flags = null;
	Physics[] physics = null;
	SoloGraphic[] solo_graphic = null;
	FloorCollision[] floor_collision = null;
	RectCollision[] floor = null;
	MultiGraphic[] multi_graphic = null;
	Life[] life = null;
	CircleCollision[] circle_collision = null;
	Shoot[] shoot = null;
	LifeSpan[] life_span = null;
	Player[] player_input = null;
	public Rectangle play_area = null;
	private int cheese = -1;
	int minutes = 0;
	StringBuilder meters_left;
	StringBuilder timer;
	int players = 2;
	Sprite fish;
	Sprite fish_used;
	public Player[] player;
	int monster = -1;
	boolean boss_is_running = false;
	State state = State.Running;
	Menu pause_menu;
	long last_ms = 0L;
	long ms = 0L;
	int seconds = 0;

	private static class Continue implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.keyDown(PlayerInput.pause);
		}
		
	}
	
	private static class QuitGame implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.keyDown(PlayerInput.pause);
			MiceInvaders.MiceInvaders.setMainMenu();
		}
		
	}
	
	private Engine(){
		meters_left = new StringBuilder();
		timer = new StringBuilder();
		
		play_area = new Rectangle(0.f, 0.f, MiceInvaders.VWIDTH, MiceInvaders.VHEIGHT);
		
		entity_flags = new int[ENTITIES];
		MiceInvaders.fillArrayWithValue(entity_flags, COMPONENT_NONE);
		enemy = new Enemy[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(enemy, Enemy.class);
		physics = new Physics[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(physics, Physics.class);
		solo_graphic = new SoloGraphic[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(solo_graphic, SoloGraphic.class);
		floor_collision = new FloorCollision[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(floor_collision, FloorCollision.class);
		floor = new RectCollision[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(floor, RectCollision.class);
		multi_graphic = new MultiGraphic[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(multi_graphic, MultiGraphic.class);
		for(MultiGraphic a_multi_graphic: multi_graphic) a_multi_graphic.init((int)(MiceInvaders.VWIDTH/MiceInvaders.PPM));
		life = new Life[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(life, Life.class);
		circle_collision = new CircleCollision[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(circle_collision, CircleCollision.class);
		shoot = new Shoot[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(shoot, Shoot.class);
		life_span = new LifeSpan[ENTITIES];
		MiceInvaders.fillArrayWithConstructor(life_span, LifeSpan.class);
		player_input = new Player[ENTITIES];
		
		layer_1 = new IGraphic[ENTITIES];
		layer_2 = new IGraphic[ENTITIES];
		layer_3 = new IGraphic[ENTITIES];
		
		fish = new Sprite( (Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"), 3*16, 0, 8, 16 );
		fish.setSize(MiceInvaders.PPM/2.f, MiceInvaders.PPM);
		fish_used = new Sprite( (Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"), 3*16+8, 0, 8, 16 );
		fish_used.setSize(MiceInvaders.PPM/2.f, MiceInvaders.PPM);
		
		player = new Player[PLAYERS];
		for(int i = 0; i < PLAYERS; i += 1) player[i] = new Player();
		pause_menu = new Menu();
				
		Menu.Selector sound_selector = new Menu.Selector(5);
		sound_selector.setName("sound");
		sound_selector.setNumber(3);
		sound_selector.selectorCallback = MiceInvaders.SoundControl;
		
		Menu.Selector music_selector = new Menu.Selector(5);
		music_selector.setName("music");
		music_selector.setNumber(0);
		music_selector.selectorCallback = MiceInvaders.MusicControl;
		
		Element element_continue = new Menu.Element();
		element_continue.setName("Continue");
		element_continue.callback = new Continue();
		
		Element quit_game = new Menu.Element();
		quit_game.setName("Quit");
		quit_game.callback = new QuitGame();
		
		pause_menu.addElement(element_continue);
		pause_menu.addElement(sound_selector);
		pause_menu.addElement(music_selector);
		pause_menu.addElement(quit_game);
	}
	
	public void startGame(){
		MessageSystem.MessageSystem.cleanMessages();
		cleanEntities();
		state = State.Running;
		boss_is_running = false;
		MiceInvaders.MiceInvaders.camera.translate(0.f, -play_area.y);
		play_area.y = 0.f;
		cheese_speed = MIN_CHEESE_SPEED;
		cheese = createCheese();
		physics[cheese].position.y = 0.f;
		final float start_floor = (PLATFORM_DISTANCE/MiceInvaders.PPM)*2.f;
		createPlatform(0.f, (int)(MiceInvaders.VWIDTH/MiceInvaders.PPM), true, start_floor, Color.WHITE);
		final int width = MathUtils.random(4, 7);
		for(float y = start_floor; y < MiceInvaders.VHEIGHT/MiceInvaders.PPM+PLATFORM_DISTANCE/MiceInvaders.PPM; y += PLATFORM_DISTANCE/MiceInvaders.PPM){
			if (y == start_floor) continue;
			createPlatform(MathUtils.random(0.f, (MiceInvaders.VWIDTH/MiceInvaders.PPM)-width), width, true, y, MiceInvaders.color[MathUtils.random(MiceInvaders.color.length-1)]);
		}
		for(int i = 0; i < players; i += 1){
			int player_entity = createPlayer(i);
			player[i].reset();
			player[i].entity = player_entity;
			physics[player_entity].position.set(MiceInvaders.VWIDTH/8.f + (MiceInvaders.VWIDTH/4.f*i), start_floor * MiceInvaders.PPM + MiceInvaders.PPM);
		}
		enemies = 0;
	}
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		for(Message a_message: MessageSystem.MessageSystem){
			switch(a_message.type){
			case DestroyEntity:
				destroyEntity(a_message.i);
				break;
			case CreateBullet:
				createBullet(a_message.position_x, a_message.position_y,
						a_message.owner, a_message.i, a_message.vel_x, a_message.vel_y, a_message.color);
				break;
			case CreateExplosion:
				createExplosion(a_message.position_x, a_message.position_y, a_message.duration);
				break;
			case CreatePowerUp:
				System.out.println("create power up " + a_message.key);
				createPowerUp(a_message.position_x, a_message.position_y, a_message.key);
				break;
			case None: default:
				continue;
			}
		}
		
		if (state == State.Running){
		
		cheese_speed += delta * -CHEESE_DAMP_SPEED;
		if (cheese_speed < MIN_CHEESE_SPEED){
			cheese_speed = MIN_CHEESE_SPEED;
		}
		if (cheese_speed > MAX_CHEESE_SPEED){
			cheese_speed = MAX_CHEESE_SPEED;
		}
		float y_delta = (play_area.y + delta * cheese_speed) - play_area.y;
		//y_delta = 0.f;
		play_area.y += y_delta;
		physics[cheese].getPosition().y += y_delta;
		MiceInvaders.MiceInvaders.camera.translate(0.f, y_delta);
		MiceInvaders.MiceInvaders.camera.update();
		
		//put a platform for each platform distance
		platform_distance += y_delta;
		if (platform_distance > PLATFORM_DISTANCE){
			platform_distance = 0.f;
			final int width = MathUtils.random(4, 7);
			createPlatform(MathUtils.random(0.f, (MiceInvaders.VWIDTH/MiceInvaders.PPM)-width), width, false, 0.f, MiceInvaders.color[MathUtils.random(MiceInvaders.color.length-1)]);
		}
		
		if (boss_is_running && enemies == 0){
			boss_is_running = false;
		}
		
		if (state == State.Running && (OBJECTIVE - play_area.y / MiceInvaders.PPM) <= 0f && enemies == 0){
			boss_is_running = true;
			createEnemy(MiceInvaders.VWIDTH/2.f, 20, "boss");
		}

		//spawn a enemy if is Running
		if (state == State.Running && !boss_is_running && (OBJECTIVE - play_area.y / MiceInvaders.PPM) > 0f){
		enemy_spawn_time += delta;
		if (enemies < ENEMIES_PER_PLAYER * players && enemy_spawn_time > 2.f){
			enemy_spawn_time = 0.f;
			
			int type = MathUtils.random(2);
			createEnemy(MathUtils.random(MiceInvaders.VWIDTH/4.f, MiceInvaders.VWIDTH-MiceInvaders.VWIDTH/4.f), enemy_life[type], enemy_type[type]);
		}
		}
		
		//Input (Timers, Control input, object triggers)
		//Update (updates and graphics)
		
		doPlayerInputSystem();
		for(int i = 0; i < ENTITIES; i += 1){
			doEnemyInputSystem(i);
			doLifeSpanSystem(i);
			doLifeSystem(i);
			doFloorSystem(i);
			doPlatformSystem(i);
			doShootSystem(i);
			doUpdateCircleCollision(i);
			doPhysicSystem(i);
		}
		
		for(int i = 0; i < ENTITIES; i += 1){
			doFloorCollisionSystem(i);
			doCircleCollisionSystem(i);
			doGraphicsSystem(i);
		}
		
		meters_left.setLength(0);
		meters_left.append("Objective: ");
		int objective = MathUtils.round(OBJECTIVE - play_area.y / MiceInvaders.PPM);
		if (objective < 0){
			objective = 0;
		}
		meters_left.append(objective);
		meters_left.append(" meters");
		doTimer();
						
		}//state running
		
		//draw graphics
		SpriteBatch batch = MiceInvaders.MiceInvaders.batch;
		batch.begin();
		batch.setProjectionMatrix(MiceInvaders.MiceInvaders.camera.combined);
		for (IGraphic a_graphic: layer_1){
			if (a_graphic == null) continue;
			a_graphic.draw(batch);
		}
		for (IGraphic a_graphic: layer_2){
			if (a_graphic == null) continue;
			a_graphic.draw(batch);
		}
		for (IGraphic a_graphic: layer_3){
			if (a_graphic == null) continue;
			a_graphic.draw(batch);
		}
			
		batch.setProjectionMatrix(MiceInvaders.MiceInvaders.screen_camera.combined);//static camera
		MiceInvaders.MiceInvaders.small_font.setColor(Color.BLUE);
		MiceInvaders.MiceInvaders.small_font.draw(batch, meters_left, 8.f, MiceInvaders.VHEIGHT - MiceInvaders.PPM);
		MiceInvaders.MiceInvaders.small_font.draw(batch, timer, MiceInvaders.VWIDTH/2.f, MiceInvaders.VHEIGHT - MiceInvaders.PPM);
		/*
		small_font.draw(batch, points_str, VHEIGHT/2.f, PPM);
		
		final int cat_life = life[player].getLife();
		final int cat_remaining_life= life[player].getRemainingLife();
		final float x = 8.f;
		final float y = 8.f;
		for(int i = 0; i < cat_life; i += 1){
			if (i >= cat_remaining_life){
				fish_used.setPosition(x + i * (PPM/2.f), y) ;
				fish_used.draw(batch);
				continue;
			}
			fish.setPosition(x + i * (PPM/2.f), y) ;
			fish.draw(batch);
		}
		*/
		if (state == State.Pause){
			
			final float message_x = 32.f;
			final float message_y = 32.f;
			final float message_width = MiceInvaders.VWIDTH/2.f;
			final float message_height = MiceInvaders.VHEIGHT/2.f;
			final float message_offset = 16.f;
			
			MiceInvaders.MiceInvaders.background_message.draw(batch, message_x, message_y, message_width, message_height);
			MiceInvaders.MiceInvaders.small_font.draw(batch, engine_message, message_x + message_offset, message_y+message_height-message_offset);
			final int max_elements = pause_menu.elements_added;
			for(int i = 0; i < max_elements; i += 1){
				MiceInvaders.MiceInvaders.small_font.setColor(Color.BLUE);
				if (i == pause_menu.current_element){
					MiceInvaders.MiceInvaders.small_font.setColor(Color.GRAY);
				}
				final Element a_element = pause_menu.elements.get(i);
				MiceInvaders.MiceInvaders.small_font.draw(batch, a_element.name, message_x + message_offset, (message_y+message_height-message_offset)-message_offset*(i+1));
				if (a_element instanceof Menu.Selector){
					final Menu.Selector a_selector = (Selector) a_element;
					MiceInvaders.MiceInvaders.small_font.draw(batch, a_selector.str_number + "/" + Integer.toString(((Menu.Selector) a_element).max_number), message_x + message_offset + (MiceInvaders.VWIDTH/4.f), (message_y+message_height-message_offset)-message_offset*(i+1));
				}
			}
		}
			
		batch.end();
	}

	public void addComponentToEntity(int entity, int component){
		entity_flags[entity] |= component;
	}
	
	public void quitComponentToEntity(int entity, int component){
		if (entityHasComponent(entity, component)){
			entity_flags[entity] ^= component;
		}
	}
	
	public boolean entityHasComponent(int entity, int component){
		return (entity_flags[entity] & component) == component;
	}
		
	public int createEntity(int component){
		for (int i = 0; i < entity_flags.length; i += 1){
			if (entity_flags[i] == COMPONENT_NONE){
				addComponentToEntity(i, component);
				return i;
			}
		}
		return -1;
	}
	
	public void destroyEntity(int entity){
		entity_flags[entity] = COMPONENT_NONE;
		solo_graphic[entity].animation = null;
		layer_1[entity] = null;
		layer_2[entity] = null;
		layer_3[entity] = null;
		if (player_input[entity] != null)player_input[entity].entity = -1;
		player_input[entity] = null;
	}
	
	public void cleanEntities(){
		for(int i = 0; i < ENTITIES; i += 1){
			destroyEntity(i);
		}
	}
	
	public int createPlayer(int i_player){
		int player = createEntity(COMPONENT_PHYSICS | COMPONENT_FLOOR_COLLISION | COMPONENT_SOLO_GRAPHIC 
		| COMPONENT_LIFE | COMPONENT_CIRCLE_COLLISION | COMPONENT_SHOOT);
		if (player > -1){
			circle_collision[player].reset();
			circle_collision[player].offset.set(0.f, 0.f);
			circle_collision[player].getCircle().setRadius(MiceInvaders.PPM/2.f);
			circle_collision[player].setFlag(CircleCollision.VULNERABLE_TO_BULLET | CircleCollision.CAT);
			circle_collision[player].setOwner(Owner.Cat);
			physics[player].reset();
			physics[player].setGravityFactor(1.f);
			physics[player].setMaxFallingSpeed(-MiceInvaders.PPM*7);
			solo_graphic[player].reset();
			solo_graphic[player].getSprite().setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"));
			solo_graphic[player].setOrigin(-MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			solo_graphic[player].getSprite().setRegion(0, 0, MiceInvaders.IPPM, MiceInvaders.IPPM);
			solo_graphic[player].getSprite().setSize(MiceInvaders.PPM, MiceInvaders.PPM);
			floor_collision[player].reset();
			floor_collision[player].getOffset()[0].set(-MiceInvaders.PPM/4.f, -MiceInvaders.PPM/2.f);
			floor_collision[player].getOffset()[1].set(MiceInvaders.PPM/4.f, -MiceInvaders.PPM/2.f);
			floor_collision[player].setVulnerableToCheese(true);
			floor_collision[player].collide = FloorCollision.CAT;
			shoot[player].setShootsBySec(10.f);
			life[player].reset();
			life[player].setLife(7);
			life[player].callbackDeath = Life.PlayerDies;
			layer_2[player] = solo_graphic[player];
			player_input[player] = this.player[i_player];
		}
		return player;
	}
	
	public int createCheese(){
		cheese = createEntity(COMPONENT_PHYSICS | COMPONENT_FLOOR | COMPONENT_MULTI_GRAPHIC);
		if (cheese > -1){
			physics[cheese].getPosition().set(0.f, 0.f);
			multi_graphic[cheese].setUsed(multi_graphic[cheese].getTotal());
			for(int i = 0; i < multi_graphic[cheese].getUsed(); i += 1){
				Sprite sprite = multi_graphic[cheese].getSprite()[i];
				sprite.setX(MiceInvaders.PPM * i);
				sprite.setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/scenery.png"));
				sprite.setSize(MiceInvaders.IPPM, MiceInvaders.IPPM*2);
				multi_graphic[cheese].getAnimation()[i] = MiceInvaders.MiceInvaders.scenery_animations.get("cheese1");
				multi_graphic[cheese].getTime()[i] = MathUtils.random( MiceInvaders.MiceInvaders.scenery_animations.get("cheese1").animationDuration );
			}
			multi_graphic[cheese].setOffsetX(MiceInvaders.PPM);
			multi_graphic[cheese].setOffsetY(MiceInvaders.PPM);
			multi_graphic[cheese].setKeepRelativePositionY(false);
			multi_graphic[cheese].setKeepRelativePositionX(true);
			floor[cheese].getRectangle().setSize(MiceInvaders.VWIDTH, MiceInvaders.PPM);
			floor[cheese].setCheese(true);
			floor[cheese].collide = FloorCollision.CAT | FloorCollision.ENEMY;
			layer_3[cheese] = multi_graphic[cheese];
		}
		return cheese;
	}
	
	public int createPlatform(float x, int width, boolean inside_play_area, float y, Color color){
		int id = createEntity(COMPONENT_PHYSICS | COMPONENT_MULTI_GRAPHIC | COMPONENT_FLOOR);
		if (id > -1){
			physics[id].reset();
			if (inside_play_area){
				physics[id].getPosition().set(x*MiceInvaders.PPM, play_area.y+y*MiceInvaders.PPM);
			}else{
				physics[id].getPosition().set(x*MiceInvaders.PPM, play_area.y+play_area.height+MiceInvaders.PPM);
			}
			if (width < 3) width = 3;
			multi_graphic[id].setOffsetX(MiceInvaders.PPM);
			multi_graphic[id].setOffsetY(MiceInvaders.PPM);
			multi_graphic[id].setKeepRelativePositionY(false);
			multi_graphic[id].setKeepRelativePositionX(true);
			multi_graphic[id].setUsed(width);
			Sprite[] sprites = multi_graphic[id].getSprite();
			for (int i = 0; i < width; i += 1){
				sprites[i].setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/scenery.png"));
				if (i == 0){
					sprites[i].setRegion(0, 0, MiceInvaders.IPPM, MiceInvaders.IPPM);
				}
				else if (i == width - 1){
					sprites[i].setRegion(MiceInvaders.IPPM*2, 0, MiceInvaders.IPPM, MiceInvaders.IPPM);
				}
				else{
					sprites[i].setRegion(MiceInvaders.IPPM, 0, MiceInvaders.IPPM, MiceInvaders.IPPM);
				}
				sprites[i].setSize(MiceInvaders.IPPM, MiceInvaders.IPPM);
				sprites[i].setColor(color);
				sprites[i].setX(i*MiceInvaders.IPPM);
			}
			floor[id].reset();
			floor[id].getRectangle().setSize(MiceInvaders.PPM*width, MiceInvaders.PPM/4.f);
			floor[id].setCheese(false);
			floor[id].collide = FloorCollision.CAT;
			layer_1[id] = multi_graphic[id];
		}
		return id;
	}
	
	public int createPowerUp(float x, float y, String name){
		int id = createEntity(COMPONENT_PHYSICS | COMPONENT_SOLO_GRAPHIC | COMPONENT_CIRCLE_COLLISION | COMPONENT_FLOOR_COLLISION | COMPONENT_LIFE);
		if (id > -1){
			physics[id].reset();
			physics[id].position.set(x, y);
			physics[id].velocity.y = cheese_speed * 2.f;
			physics[id].setGravityFactor(1.f);
			physics[id].setMaxFallingSpeed(MiceInvaders.PPM*7);
			solo_graphic[id].reset();
			solo_graphic[id].setOrigin(-MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			Sprite sprite = solo_graphic[id].sprite;
			sprite.setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"));
			sprite.setSize(MiceInvaders.PPM, MiceInvaders.PPM);
			if (name.contentEquals("life")){
				sprite.setRegion(MiceInvaders.IPPM*9, MiceInvaders.IPPM, MiceInvaders.IPPM, MiceInvaders.IPPM);
			}
			else if (name.contentEquals("multiplier")){
				sprite.setRegion(MiceInvaders.IPPM*9, MiceInvaders.IPPM*2, MiceInvaders.IPPM, MiceInvaders.IPPM);
			}
			floor_collision[id].reset();
			floor_collision[id].offset[0].set(MiceInvaders.PPM/-2.f, -MiceInvaders.PPM/2.f);
			floor_collision[id].offset[1].set(MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			floor_collision[id].setVulnerableToCheese(true);
			floor_collision[id].collide = FloorCollision.CAT;
			life[id].reset();
			life[id].setLife(3);
			circle_collision[id].reset();
			circle_collision[id].setFlag(CircleCollision.POWERUP);
			if (name.contentEquals("life")){
				circle_collision[id].effect = CircleCollision.PowerUpEffect_AddLife;
			}
			else if (name.contentEquals("multiplier")){
				circle_collision[id].effect = null;
			}
			layer_2[id] = solo_graphic[id];
		}
		return id;
	}
	
	public int createEnemy(float x, int total_life, String name){
		int id = createEntity(COMPONENT_PHYSICS | COMPONENT_SOLO_GRAPHIC | COMPONENT_LIFE | COMPONENT_ENEMY | COMPONENT_CIRCLE_COLLISION | COMPONENT_SHOOT);
		if (id > -1){
			physics[id].reset();
			if (name.contentEquals("monster") ){
				physics[id].position.set(x, physics[cheese].position.y + floor[cheese].rect.height + MiceInvaders.PPM);
			}else{
				if (x < play_area.x || x > play_area.x + play_area.width){
					physics[id].getPosition().set(x, play_area.y + play_area.height);	
				}else{
					physics[id].getPosition().set(x, play_area.y + play_area.height + MiceInvaders.PPM);
				}
			}
			physics[id].getVelocity().set(0.f, 0.f);
			
			life[id].reset();
			life[id].setLife(total_life);
			life[id].setDamage(0);
			life[id].callbackDamage = Life.EnemyDamaged;
			if (name.contentEquals("boss")){
				life[id].callbackDeath = Life.BossDefeated;
			}else{
				life[id].callbackDeath = Life.EnemyDies;}
			
			solo_graphic[id].setOrigin(MiceInvaders.PPM / 2.f, MiceInvaders.PPM / 2.f);
			//for bouncer, invader and monster
			Sprite sprite = solo_graphic[id].getSprite();
			sprite.setColor(Color.WHITE);
			sprite.setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"));
			//set region
			if (name.contentEquals("bouncer")){
				sprite.setRegion(MiceInvaders.IPPM*4, MiceInvaders.IPPM, MiceInvaders.IPPM, MiceInvaders.IPPM);
				sprite.setSize(MiceInvaders.IPPM, MiceInvaders.IPPM);
				solo_graphic[id].setOrigin(-MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			}
			else if (name.contentEquals("invader")){
				sprite.setRegion(MiceInvaders.IPPM*4, MiceInvaders.IPPM*2, MiceInvaders.IPPM, MiceInvaders.IPPM);
				sprite.setSize(MiceInvaders.IPPM, MiceInvaders.IPPM);
				solo_graphic[id].setOrigin(-MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			}
			else if (name.contentEquals("monster")){
				sprite.setRegion(MiceInvaders.IPPM*5, MiceInvaders.IPPM, MiceInvaders.IPPM*2, MiceInvaders.IPPM*2);
				sprite.setSize(MiceInvaders.IPPM*2, MiceInvaders.IPPM*2);
				solo_graphic[id].setOrigin(-MiceInvaders.PPM, -MiceInvaders.PPM);
			}
			else if (name.contentEquals("boss")){
				sprite.setRegion(0, 3 * MiceInvaders.IPPM, MiceInvaders.IPPM * 9, MiceInvaders.IPPM * 3);
				sprite.setSize(MiceInvaders.IPPM * 9, MiceInvaders.IPPM * 3);
				solo_graphic[id].setOrigin(-MiceInvaders.IPPM * 9.f / 2.f, -MiceInvaders.IPPM * 3.f / 2.f);
			}
			//end set region
			
			circle_collision[id].reset();
			//set circle offset and radius
			if (name.contentEquals("bouncer")){
				circle_collision[id].getOffset().set(0.f, 0.f);
				circle_collision[id].getCircle().setRadius(MiceInvaders.PPM/2.f);
			}
			else if (name.contentEquals("invader")){
				circle_collision[id].getOffset().set(0.f, 0.f);
				circle_collision[id].getCircle().setRadius(MiceInvaders.PPM/2.f);
			}
			else if (name.contentEquals("monster")){
				circle_collision[id].getOffset().set(0.f, 0.f);
				circle_collision[id].getCircle().setRadius(MiceInvaders.PPM);
			}
			else if (name.contentEquals("boss")){
				circle_collision[id].offset.set(0.f, -MiceInvaders.PPM);
				circle_collision[id].circle.setRadius(MiceInvaders.PPM/2.f);
			}
			//end set circle offset and radius
			circle_collision[id].setFlag(CircleCollision.VULNERABLE_TO_BULLET | CircleCollision.ENEMY);
			circle_collision[id].setOwner(Owner.Enemy);
			
			shoot[id].setShootsBySec(1.f);
			enemy[id].reset();
			//set brain
			if (name.contentEquals("bouncer")){
				enemy[id].setBrain(Enemy.bouncer_brain);}
			else if (name.contentEquals("invader")){
				enemy[id].setBrain(Enemy.invader_brain);}
			else if (name.contentEquals("monster")){
				enemy[id].setBrain(Enemy.monster_brain);}
			else if (name.contentEquals("boss")){
				enemy[id].setBrain(Enemy.boss_brain);}
			//end set brain
			
			//set additional settings
			//(like gravity or whether can touch the floor)
			if (name.contentEquals("bouncer")){
				enemy[id].point = 100L;
			}
			else if (name.contentEquals("invader")){
				enemy[id].point = 300L;
			}
			else if (name.contentEquals("monster")){
				addComponentToEntity(id, COMPONENT_FLOOR_COLLISION);
				floor_collision[id].reset();
				floor_collision[id].setVulnerableToCheese(false);
				floor_collision[id].getOffset()[0].set(-MiceInvaders.PPM, -MiceInvaders.PPM);
				floor_collision[id].getOffset()[1].set(MiceInvaders.PPM, -MiceInvaders.PPM);
				floor_collision[id].collide = FloorCollision.ENEMY;
				physics[id].setGravityFactor(1.f);
				physics[id].setMaxFallingSpeed(-MiceInvaders.PPM*22);
				circle_collision[id].damage_player_on_contact = true;
				enemy[id].point = 1000L;
			}
			else if (name.contentEquals("boss")){
				enemy[id].point = 5000L;
			}
			//end set additional settings
			enemies += 1;
			layer_2[id] = solo_graphic[id];
		}
		return id;
	}
	
	public int createExplosion(float x, float y, float duration){
		int id = createEntity(COMPONENT_PHYSICS | COMPONENT_SOLO_GRAPHIC | COMPONENT_LIFE_SPAN);
		if (id > -1){
			physics[id].reset();
			physics[id].position.set(x, y);
			solo_graphic[id].reset();
			solo_graphic[id].sprite.setColor(Color.WHITE);
			solo_graphic[id].sprite.setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"));
			solo_graphic[id].sprite.setRegion(MiceInvaders.IPPM * 7, MiceInvaders.IPPM, MiceInvaders.IPPM * 2, MiceInvaders.IPPM * 2);
			solo_graphic[id].sprite.setSize(MiceInvaders.PPM * 2.f, MiceInvaders.PPM * 2.f);
			solo_graphic[id].setOrigin(-MiceInvaders.PPM, -MiceInvaders.PPM);
			life_span[id].reset();
			life_span[id].maxtime = duration;
			layer_3[id] = solo_graphic[id];
		}
		return id;
	}
	
	public int createBullet(float x, float y, Owner owner, int player, float vel_x, float vel_y, Color color){
		int id = createEntity(COMPONENT_PHYSICS | COMPONENT_SOLO_GRAPHIC | COMPONENT_CIRCLE_COLLISION);
		if (id > -1){
			physics[id].reset();
			physics[id].getPosition().set(x, y);
			physics[id].getVelocity().set(vel_x, vel_y);
			CircleCollision the_circle_collision = circle_collision[id];
			the_circle_collision.reset();
			the_circle_collision.setOwner(owner);
			the_circle_collision.offset.set(0.f, 0.f);
			the_circle_collision.circle.setRadius(MiceInvaders.PPM/4.f);
			the_circle_collision.player = player;
			the_circle_collision.setFlag(CircleCollision.BULLET);
			solo_graphic[id].setOrigin(-MiceInvaders.PPM/2.f, -MiceInvaders.PPM/2.f);
			solo_graphic[id].animation = MiceInvaders.MiceInvaders.misc_animations.get("bullet_1");
			Sprite sprite = solo_graphic[id].getSprite();
			sprite.setTexture((Texture)MiceInvaders.MiceInvaders.assets.get("data/cat.png"));
			sprite.setRegion(0, MiceInvaders.IPPM*2, MiceInvaders.IPPM, MiceInvaders.IPPM);
			sprite.setSize(MiceInvaders.IPPM, MiceInvaders.IPPM);
			sprite.setColor(color);
			layer_3[id] = solo_graphic[id];
		}
		return id;
	}
	
	public void doPlayerInputSystem(){
		if (state == State.Pause) return;
		Array<Controller> gamepads = Controllers.getControllers();
		for(int i = 0; i < players; i += 1){
			if (player[i].entity == -1){
				continue;
			}
			Player player = this.player[i];
			final int entity = player.entity;
			Controller gamepad = null;
			if (gamepads.size > 0 && PlayerInput.type == PlayerInput.Type.ALL_PLAYERS_GAMEPAD){
				gamepad = gamepads.get(i);
			}
			if (gamepads.size > 0 && PlayerInput.type == PlayerInput.Type.FIRST_PLAYER_KEYBOARD && i > 0){
				gamepad = gamepads.get(i-1);
			}
			final PovDirection pov_direction = gamepad != null? gamepad.getPov(0) : PovDirection.center;
			SoloGraphic graphic = solo_graphic[entity];
			float vel_x = 0.f;
			if (!physics[entity].isBeingPushed()){
				player.movement_input_pressed = false;
				if ((i == 0 && Gdx.input.isKeyPressed(PlayerInput.key_left))
				|| pov_direction == PovDirection.west
				|| pov_direction == PovDirection.northWest
				|| pov_direction == PovDirection.southWest
				){
					vel_x = -PLAYER_SPEED;
					player.setFacingLeft();
					player.movement_input_pressed = true;
					if (floor_collision[entity].isTouchingFloor() && graphic.animation != MiceInvaders.MiceInvaders.cat_animations.get("cat_walk")){
						graphic.animation = MiceInvaders.MiceInvaders.cat_animations.get("cat_walk");
						graphic.time = 0.f;
					}
					if (graphic.sprite.isFlipX()){
						graphic.sprite.flip(true, false);
					}
				}
				else if ((i == 0 && Gdx.input.isKeyPressed(PlayerInput.key_right))
					|| pov_direction == PovDirection.east
					|| pov_direction == PovDirection.northEast
					|| pov_direction == PovDirection.southEast
					){
					vel_x = PLAYER_SPEED;
					player.setFacingRight();
					player.movement_input_pressed = true;
					if (floor_collision[entity].isTouchingFloor() && graphic.animation != MiceInvaders.MiceInvaders.cat_animations.get("cat_walk")){
						graphic.animation = MiceInvaders.MiceInvaders.cat_animations.get("cat_walk");
						graphic.time = 0.f;
					}
					if (!graphic.sprite.isFlipX()){
						graphic.sprite.flip(true, false);
					}
				}else{
					vel_x = 0.f;
					//the player is controlling a cat
					if (graphic.animation != MiceInvaders.MiceInvaders.cat_animations.get("cat_idle")){
						graphic.animation = MiceInvaders.MiceInvaders.cat_animations.get("cat_idle");
						graphic.time = 0.f;
					}
				}
			}
			Vector2 player_velocity = physics[entity].getVelocity();
			if (player_velocity.x < 0.f && vel_x > 0.f
			|| player_velocity.x > 0.f && vel_x < 0.f){
				player_velocity.x = 0.f;
			}
			player_velocity.x += vel_x * Gdx.graphics.getDeltaTime();
			if (player_velocity.x > PLAYER_SPEED){
				player_velocity.x = PLAYER_SPEED;
			}
			else if (player_velocity.x < -PLAYER_SPEED){
				player_velocity.x = -PLAYER_SPEED;
			}
			
			if (floor_collision[entity].isTouchingFloor()){
				player.resetJumps();
			}
			//end player movement
			//player jumps
			if  (
				((i == 0 && Gdx.input.isKeyPressed(PlayerInput.jump))
				|| (gamepad != null && gamepad.getButton(0) ) )
				&& !physics[entity].isBeingPushed()
				){
				if (player.jump()){
					if ((i == 0 && (Gdx.input.isKeyPressed(PlayerInput.key_down))
						|| pov_direction == PovDirection.south || pov_direction == PovDirection.southEast || pov_direction == PovDirection.southWest)
						&& floor_collision[entity].isTouchingFloor() ){
						floor_collision[entity].time_pass = FloorCollision.TIME_PASS;
					}else{
						physics[entity].getVelocity().y = JUMP;
					}
				}
			}else{
				player.setJumpNoPressed();
			}
			//end player jumps
			//player shoots
			if ((i == 0 && (Gdx.input.isKeyPressed(PlayerInput.shoot)) || (gamepad != null && gamepad.getButton(1)) ) && shoot[entity].shoot()){
				final Vector2 position = physics[entity].getPosition();
				float bullet_vel_x = player.isFacingLeft() ? -MiceInvaders.PPM*12.f : MiceInvaders.PPM*12.f;
				float bullet_vel_y = 0.f;
				float bullet_position_y = position.y;
				float bullet_position_x = player.isFacingLeft() ? position.x - MiceInvaders.PPM/2.f  : position.x + MiceInvaders.PPM/2.f;
				if ((i == 0 && Gdx.input.isKeyPressed(PlayerInput.key_up))
				|| pov_direction == PovDirection.north
				|| pov_direction == PovDirection.northEast
				|| pov_direction == PovDirection.northWest
				//|| gamepad.getAxis(0) < 0.f
				){
					bullet_vel_y = MiceInvaders.PPM*12.f;
					bullet_vel_x = 0.f;
					bullet_position_y = position.y + MiceInvaders.PPM/2.f;
					bullet_position_x = position.x;
				}
				else if ((i == 0 && Gdx.input.isKeyPressed(PlayerInput.key_down))
				|| (pov_direction == PovDirection.south
				|| pov_direction == PovDirection.southEast
				|| pov_direction == PovDirection.southWest)
				//|| gamepad.getAxis(1) > 0.f
				){
					bullet_vel_y = -MiceInvaders.PPM*12.f;
					bullet_vel_x = 0.f;
					bullet_position_y = position.y - MiceInvaders.PPM/2.f;
					bullet_position_x = position.x;
				}
				MiceInvaders.MiceInvaders.playSound("data/laser_shot.wav");
				Message message = MessageSystem.MessageSystem.addMessage(Type.CreateBullet);
				message.position_x = bullet_position_x;
				message.position_y = bullet_position_y;
				message.owner = Owner.Cat;
				message.vel_x = bullet_vel_x;
				message.vel_y = bullet_vel_y;
				message.color = Color.LIGHT_GRAY;
				message.i = this.player[i].entity;
			}
			//end player shoots
		}
	}	

	public void doEnemyInputSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_ENEMY | COMPONENT_LIFE)){
			enemy[entity].think(entity);
		}
	}
	
	public void doPhysicSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_PHYSICS)){
			final float delta = Gdx.graphics.getDeltaTime();
			physics[entity].update(delta, GRAVITY);
			if (player_input[entity] != null){
				if (physics[entity].position.x-MiceInvaders.PPM/2.f < 0.f){
					physics[entity].position.x = MiceInvaders.PPM/2.f;
				}
				if (physics[entity].position.x+MiceInvaders.PPM/2.f > MiceInvaders.VWIDTH){
					physics[entity].position.x = MiceInvaders.VWIDTH-MiceInvaders.PPM/2.f;
				}
			}
		}
	}
	
	public void doPlatformSystem(int entity){
		if (cheese == entity) return;
		if (entityHasComponent(entity, COMPONENT_FLOOR) ){
			final Rectangle floor_rectangle = floor[entity].getRectangle();
			if (floor_rectangle.y + floor_rectangle.height < play_area.y){
				Message message = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
				message.i = entity;
			}
		}
	}
	
	public void doFloorSystem(int entity){
		//this is just for update the floor component
		if (entityHasComponent(entity, COMPONENT_FLOOR | COMPONENT_PHYSICS)){
			final Vector2 floor_position = physics[entity].getPosition();
			floor[entity].getRectangle().setPosition(floor_position);
		}
	}
	
	public void doFloorCollisionSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_FLOOR_COLLISION | COMPONENT_PHYSICS)){
			final Vector2 position = physics[entity].getPosition();
			FloorCollision the_floor_collision = floor_collision[entity];
			the_floor_collision.update(position.x, position.y);
			
			the_floor_collision.setTouchFloor(false);
			the_floor_collision.time_pass -= Gdx.graphics.getDeltaTime();
			if (the_floor_collision.time_pass < 0.f){
				the_floor_collision.time_pass = 0.f;
			}
			
			for (int i = 0; i < ENTITIES; i += 1){
				if (i == entity || !entityHasComponent(i, COMPONENT_FLOOR | COMPONENT_PHYSICS)) continue;
				if ((floor_collision[entity].collide & floor[i].collide) != floor_collision[entity].collide) continue;
				if (floor[i].collidesWithFloorPoints(floor_collision[entity].getPoints()) ){
					if (floor[i].isCheese() && floor_collision[entity].isVulnerableToCheese() ){
						physics[entity].getVelocity().y = JUMP * 1.2f;
						if (entityHasComponent(entity, COMPONENT_LIFE)){
							life[entity].addDamage(1, entity);
							life[entity].setInvicible(1.f);
						}
						continue;
					}
					if (the_floor_collision.time_pass <= 0.f && physics[entity].getVelocity().y < 0.f){
						physics[entity].getVelocity().y = 0.f;
						physics[entity].getPosition().y += floor[i].returnDepth( floor_collision[entity].getPoints()[0] );
						floor_collision[entity].setTouchFloor(true);
						if (player_input[entity] != null && !player_input[entity].movement_input_pressed){
							physics[entity].getVelocity().x = 0.f;
						}
					}
				}
			}
			
		}
	}
	
	public void doLifeSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_LIFE | COMPONENT_SOLO_GRAPHIC)){
			if (life[entity].isDefeated() ){
				Message message = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
				message.i = entity;
				if (life[entity].callbackDeath != null) life[entity].callbackDeath.doTask(life[entity], entity);
				return;
			}
			life[entity].update(Gdx.graphics.getDeltaTime());
			if (life[entity].show()){
				solo_graphic[entity].getSprite().setColor(1.f, 1.f, 1.f, 1.f);
			}else{
				solo_graphic[entity].getSprite().setColor(1.f, 1.f, 1.f, 0.2f);
			}
		}
	}
	
	public void doLifeSpanSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_LIFE_SPAN)){
			life_span[entity].time += Gdx.graphics.getDeltaTime();
			if (life_span[entity].isOver()){
				Message message = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
				message.i = entity;
			}
		}
	}
	
	public void doUpdateCircleCollision(int entity){
		if (entityHasComponent(entity, COMPONENT_PHYSICS | COMPONENT_CIRCLE_COLLISION)){
			circle_collision[entity].update( physics[entity].getPosition() );
		}
	}
	
	//find the way to add points to the player bullet what killed an enemy
	
	public void doCircleCollisionSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_PHYSICS | COMPONENT_CIRCLE_COLLISION)){
			if (circle_collision[entity].is(CircleCollision.BULLET) ){
				if (!play_area.contains(physics[entity].getPosition() ) ){
					Message message = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
					message.i = entity;
					return;
				}
				
				for(int i = 0; i < ENTITIES; i += 1){
					if (i == entity || !entityHasComponent(i, COMPONENT_CIRCLE_COLLISION)) continue;
					if (circle_collision[i].is(CircleCollision.VULNERABLE_TO_BULLET)
					&& circle_collision[entity].owner != circle_collision[i].getOwner()
					&& circle_collision[entity].circle.overlaps(circle_collision[i].getCircle() )){
						Message message = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
						message.i = entity;
						if (entityHasComponent(i, COMPONENT_LIFE)){
							life[i].addDamage(1, entity);
							life[i].setInvicible(0.25f);
							if (circle_collision[entity].owner == CircleCollision.Owner.Cat){
								life[i].last_entity = circle_collision[entity].player;
							}
						}
						break;
					}
					
				}
			}
			if (circle_collision[entity].is(CircleCollision.ENEMY)){
				for(int i = 0; i < ENTITIES; i += 1){
					if (entity == i || !entityHasComponent(i, COMPONENT_CIRCLE_COLLISION)) continue;
					if (circle_collision[i].is(CircleCollision.CAT)
					&& entityHasComponent(i, COMPONENT_PHYSICS | COMPONENT_LIFE)
					&& circle_collision[entity].circle.overlaps(circle_collision[i].circle ) ){
					//push the victim!
						final Circle agressor_circle = circle_collision[entity].circle;
						final Circle victim_circle = circle_collision[i].circle;
						final float angle = MathUtils.atan2(victim_circle.y - agressor_circle.y, victim_circle.x - agressor_circle.x);
						final float vel_x = MathUtils.cos(angle) * JUMP;
						final float vel_y = MathUtils.sin(angle) * JUMP;
						physics[i].push(vel_x, vel_y, 0.25f);
						if (circle_collision[entity].damage_player_on_contact){
							life[i].addDamage(1, i);
							life[i].setInvicible(0.25f);
						}
						break;
					}
				}
			}
			if (circle_collision[entity].is(CircleCollision.POWERUP)){
				for(int i = 0; i < ENTITIES; i += 1){
					if (entity == i || !entityHasComponent(i, COMPONENT_CIRCLE_COLLISION)) continue;
					if (circle_collision[i].is(CircleCollision.CAT) && circle_collision[entity].circle.overlaps(circle_collision[i].circle)){
						Message destroy_power_up = MessageSystem.MessageSystem.addMessage(Type.DestroyEntity);
						destroy_power_up.i = entity;
						if (circle_collision[entity].effect != null) circle_collision[entity].effect.doTask(entity, i);
						break;
					}
				}
			}
		}
	}
	
	public void doShootSystem(int entity){
		if (entityHasComponent(entity, COMPONENT_SHOOT)){
			shoot[entity].recharge(Gdx.graphics.getDeltaTime());
		}
	}
	
	private void doTimer(){
		long current_ms = System.currentTimeMillis();
		ms += current_ms - last_ms;
		last_ms = current_ms;
		if (ms > 999L){
			ms = 0L;
			seconds += 1;
		}
		if (seconds > 59){
			seconds = 0;
			minutes += 1;
		}
		if (minutes > TIME){
			minutes = TIME;
		}
		if (seconds > 59){
			seconds = 60;
		}
		if (ms > 1000L){
			ms = 1000L;
		}
		timer.setLength(0);
		timer.append(TIME - minutes);
		timer.append(" : ");
		timer.append( 60 - seconds );
		timer.append(" : ");
		timer.append( 1000L - ms );
	}
	
	public void doGraphicsSystem(int entity){
		final float delta = Gdx.graphics.getDeltaTime();
		final Vector2 the_position = physics[entity].getPosition();
		if (layer_1[entity] != null) layer_1[entity].update(delta, the_position.x, the_position.y); 
		if (layer_2[entity] != null) layer_2[entity].update(delta, the_position.x, the_position.y);
		if (layer_3[entity] != null) layer_3[entity].update(delta, the_position.x, the_position.y); 
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		if (state == State.Running && buttonCode == 2){
			state = State.Pause;
			engine_message.setLength(0);
			engine_message.append("Player 1 pause");
			pause_menu.current_element = 0;
			MiceInvaders.MiceInvaders.multi_input.addProcessor(pause_menu);
			Controllers.addListener(pause_menu);
			return true;
		}
		
		if (state == State.Pause){
			if (buttonCode == 2){
				state = State.Running;
				MiceInvaders.MiceInvaders.multi_input.removeProcessor(pause_menu);
				Controllers.removeListener(pause_menu);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (state == State.Running && keycode == PlayerInput.pause){
			state = State.Pause;
			engine_message.setLength(0);
			engine_message.append("Player 1 pause");
			pause_menu.current_element = 0;
			MiceInvaders.MiceInvaders.multi_input.addProcessor(pause_menu);
			Controllers.addListener(pause_menu);
			return true;
		}
		
		if (state == State.Pause){
			if (keycode == PlayerInput.pause){
				state = State.Running;
				MiceInvaders.MiceInvaders.multi_input.removeProcessor(pause_menu);
				Controllers.removeListener(pause_menu);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
