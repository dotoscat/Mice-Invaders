package dotteri.MiceInvaders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Calendar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.controllers.*;

import dotteri.MiceInvaders.Menu.Element;
import dotteri.MiceInvaders.Menu.Selector;

public class MiceInvaders extends Game implements InputProcessor{
	
	public static MiceInvaders MiceInvaders;
	
	public static float VWIDTH = 480.f;
	public static float VHEIGHT = 320.f;
	
	public AssetManager assets;
	public OrthographicCamera camera;
	public OrthographicCamera screen_camera;
	public SpriteBatch batch;
	public ShaderProgram default_shader;
	
	final public static float PPM = 16.f;
	final public static int IPPM = 16;
	final static public Color[] color = {Color.BLUE, Color.CYAN, Color.GREEN, Color.RED, Color.MAGENTA, Color.WHITE};
	
	ObjectMap<String, Animation> scenery_animations = null;
	ObjectMap<String, Animation> cat_animations = null;
	ObjectMap<String, Animation> misc_animations = null;
	
	BitmapFont small_font;
	BitmapFont big_font;
	BitmapFont normal_font;
	
	Color background = null;
	
	long ms_last_ms = 0L;
	long sum_ms = 0L;
	long loop  = 0L;
	
	NinePatch background_message = null;
	
	//[0.f-1.f]
	float sound_level;
	OrderedMap<String, Sound> sounds;
	float music_level;
	
	Music current_theme;
	
	InputMultiplexer multi_input = new InputMultiplexer();
	
	InputProcessor current_input_processor = null;
	ControllerListener current_controller_listener = null;
		
	static private class SoundControl implements Menu.SelectorCallback{

		@Override
		public void doTask(Selector selector) {
			MiceInvaders.setSound(selector.number);
			MiceInvaders.playSound("data/laser_shot.wav");
		}
		
	}
	static public SoundControl SoundControl = new SoundControl();
	
	static private class MusicControl implements Menu.SelectorCallback{

		@Override
		public void doTask(Selector selector) {
			MiceInvaders.setMusic(selector.number);
		}
		
	}
	static public MusicControl MusicControl = new MusicControl();
	
	@Override
	public void create() {
		MiceInvaders = this;
		scenery_animations = new ObjectMap<String, Animation>();
		cat_animations = new ObjectMap<String, Animation>();
		misc_animations = new ObjectMap<String, Animation>();
		
		assets = new AssetManager();
		assets.load("data/cat.png", Texture.class);
		assets.load("data/scenery.png", Texture.class);
		assets.load("data/ui.png", Texture.class);
		assets.load("data/laser_shot.wav", Sound.class);
		assets.load("data/explosion.wav", Sound.class);
		assets.load("data/hit.wav", Sound.class);
		//assets.load("data/Enceladus_I_Cant_Defeat_Airman.ogg", Music.class);
		assets.finishLoading();
				
		background_message = new NinePatch( new TextureRegion((Texture)assets.get("data/ui.png"), 16, 16, 16 * 3, 16 * 3 ), 16, 16, 16, 16 );
		
		FreeTypeFontGenerator hud_font_generator = new FreeTypeFontGenerator( Gdx.files.internal("data/Welbut__.ttf") );
		small_font = hud_font_generator.generateFont(10);
		big_font = hud_font_generator.generateFont(33);
		normal_font = hud_font_generator.generateFont(14);
		
		hud_font_generator.dispose();
				
		camera = new OrthographicCamera(VWIDTH, VHEIGHT);
		camera.translate(VWIDTH/2.f, VHEIGHT/2.f);
		screen_camera = new OrthographicCamera(VWIDTH, VHEIGHT);
		screen_camera.translate(VWIDTH/2.f, VHEIGHT/2.f);
		screen_camera.update();
		batch = new SpriteBatch();
		default_shader = SpriteBatch.createDefaultShader();
		batch.setShader(default_shader);
				
		loadAnimations (misc_animations, (Texture)assets.get("data/cat.png"), "data/misc_animation.json");
		loadAnimations(cat_animations, (Texture)assets.get("data/cat.png"), "data/cat_animation.json");
		loadAnimations(scenery_animations, (Texture)assets.get("data/scenery.png"), "data/animation_scenery.json");
		
		multi_input.addProcessor(this);
		
		Gdx.input.setInputProcessor(multi_input);
		//Controllers.addListener(this);
		
		background = new Color();
		background.set(135.f/255.f, 206.f/255.f, 235.f/255.f, 255.f/255.f);
						
		sounds = new OrderedMap<String, Sound>();
		
		//current_theme = assets.get("data/Enceladus_I_Cant_Defeat_Airman.ogg");
		//current_theme.setLooping(true);
		//current_theme.setVolume(music_level);
		//current_theme.play();
		
		setMainMenu();
		MiceInvaders.MiceInvaders.setSound(3);
		MiceInvaders.MiceInvaders.setMusic(0);
	}
	
	public void setEngine(){
		multi_input.removeProcessor(current_input_processor);
		Controllers.removeListener(current_controller_listener);
		multi_input.addProcessor(Engine.Engine);
		Controllers.addListener(Engine.Engine);
		current_input_processor = Engine.Engine;
		current_controller_listener = Engine.Engine;
		setScreen(Engine.Engine);
		multi_input.removeProcessor(MainMenu.MainMenu.current_menu);
		Controllers.removeListener(MainMenu.MainMenu.current_menu);
	}
	
	public void setMainMenu(){
		multi_input.removeProcessor(current_input_processor);
		Controllers.removeListener(current_controller_listener);
		setScreen(MainMenu.MainMenu);
		multi_input.addProcessor(MainMenu.MainMenu.gamemode_selection);
		Controllers.addListener(MainMenu.MainMenu.gamemode_selection);
		MainMenu.MainMenu.current_menu = MainMenu.MainMenu.gamemode_selection;
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		default_shader.dispose();
		assets.dispose();
		if (loop > 0L)
			System.out.println("average render ms loop: " + sum_ms / loop);
	}

	@Override
	public void render() {
		long ms_last_ms = System.currentTimeMillis();
		Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		super.render();
		
		loop += 1L;
		sum_ms += System.currentTimeMillis() - ms_last_ms;
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
		
	public void setSound(int level){
		sound_level = (float)level / 5.f;
	}
	
	public void playSound(String name){
		Sound the_sound = assets.get(name);
		the_sound.play(sound_level);
	}
	
	public void playSound(String name, float pitch){
		Sound the_sound = assets.get(name);
		the_sound.play(sound_level, pitch, 0.f);
	}
	
	public void setMusic(int level){
		music_level = (float)level / 5.f;
		if (current_theme != null) current_theme.setVolume(music_level);
	}
	
	static public <T> void fillArrayWithConstructor(Object[] array, Class<T> type){
		Constructor<T> constructor = null;
		try {
			constructor = type.getConstructor();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < array.length; i += 1){
			try {
				array[i] = constructor.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static public void fillArrayWithValue(int[] array, int value){
		for(int i = 0; i < array.length; i += 1){
			array[i] = value;
		}
	}

	static public void loadAnimations(ObjectMap<String, Animation> map, Texture texture, String animation_file){
		JsonReader reader = new JsonReader();
		JsonValue loaded = reader.parse( Gdx.files.internal(animation_file));
		
		JsonValue loaded_frames = loaded.get("frames");
		ObjectMap<String, TextureRegion> frames = new ObjectMap<String, TextureRegion>();
		JsonIterator loaded_frames_iterator = loaded_frames.iterator();
		
		while(loaded_frames_iterator.hasNext()){
			JsonValue a_frame = loaded_frames_iterator.next();
			frames.put(a_frame.name(), new TextureRegion(texture,
					a_frame.getInt("x"), a_frame.getInt("y"), a_frame.getInt("w"), a_frame.getInt("h")) );
		}

		JsonIterator loaded_animations_iterator = loaded.iterator();
		Array<TextureRegion> selected_frames = new Array<TextureRegion>();
		while(loaded_animations_iterator.hasNext()){
			JsonValue an_animation = loaded_animations_iterator.next();
			if (an_animation.name().equalsIgnoreCase("frames") ) continue; //find the method to the matching name) continue;
			float frame_duration = an_animation.getFloat("frame_duration");
			JsonValue animation_frames = an_animation.get("frames");
			JsonIterator animation_frames_iterator = animation_frames.iterator();
			while (animation_frames_iterator.hasNext()){
				JsonValue a_frame = animation_frames_iterator.next();
				selected_frames.add(frames.get(a_frame.asString()));
			}
			map.put(an_animation.name(), new Animation(frame_duration, selected_frames));
			selected_frames.clear();
		}
		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (Gdx.input.isKeyPressed(Keys.ALT_LEFT) && keycode == Keys.NUM_1){
			byte[] pixelData = ScreenUtils.getFrameBufferPixels(true);
			Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Format.RGBA8888);
			ByteBuffer pixels = pixmap.getPixels();
			pixels.clear();
			pixels.put(pixelData);
			pixels.position(0);
			FileHandle capture = Gdx.files.local("capture_" + Calendar.getInstance().getTime().toString() + ".png");
			PixmapIO.writePNG(capture, pixmap);
			pixmap.dispose();
		}
		if (Gdx.input.isKeyPressed(Keys.ALT_LEFT) && keycode == Keys.NUM_2){
			//startGame();
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
