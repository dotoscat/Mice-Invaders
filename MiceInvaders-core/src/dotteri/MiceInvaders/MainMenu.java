package dotteri.MiceInvaders;

import java.util.Calendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dotteri.MiceInvaders.Menu.Element;
import dotteri.MiceInvaders.Menu.Selector;
import dotteri.MiceInvaders.PlayerInput.Type;

public class MainMenu implements Screen{
		
	static final public MainMenu MainMenu = new MainMenu();
	
	static final private String Title = "Metal Cheese Maniacs";
	static final private String author = "Oscar Triano Garcia @the_catwolf";
	static final private String version = "alpha1";
	static BitmapFont.TextBounds title_textbounds = new BitmapFont.TextBounds();
	static BitmapFont.TextBounds author_textbounds = new BitmapFont.TextBounds();
	static BitmapFont.TextBounds version_textbounds = new BitmapFont.TextBounds();
	
	public Menu gamemode_selection = null;
	Menu player_options = null;
	Menu app_options = null;
	Menu current_menu = null;
		
	static class Exit implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			Gdx.app.exit();
		}
		
	}
	
	static class StartChallengeMode implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			//set the engine to the challenge mode
			MiceInvaders.MiceInvaders.multi_input.removeProcessor(MainMenu.current_menu);
			Controllers.removeListener(MainMenu.current_menu);
			MainMenu.current_menu = MainMenu.player_options;
			MainMenu.setPlayersOptions();
			MiceInvaders.MiceInvaders.multi_input.addProcessor(MainMenu.current_menu);
			Controllers.addListener(MainMenu.current_menu);
			MainMenu.current_menu.current_element = 0;
		}
		
	}
	
	static class Options implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			MiceInvaders.MiceInvaders.multi_input.removeProcessor(MainMenu.current_menu);
			Controllers.removeListener(MainMenu.current_menu);
			MainMenu.current_menu = MainMenu.app_options;
			MiceInvaders.MiceInvaders.multi_input.addProcessor(MainMenu.current_menu);
			Controllers.addListener(MainMenu.current_menu);
			
		}
		
	}
	
	static class BackPlayerOptions implements Menu.BackCallback{

		@Override
		public void doTask(Menu menu) {
			MiceInvaders.MiceInvaders.multi_input.removeProcessor(MainMenu.current_menu);
			Controllers.removeListener(MainMenu.current_menu);
			MainMenu.current_menu = MainMenu.gamemode_selection;
			MiceInvaders.MiceInvaders.multi_input.addProcessor(MainMenu.current_menu);
			Controllers.addListener(MainMenu.current_menu);
		}
		
	}
	
	static class BackAppOptions implements Menu.BackCallback{

		@Override
		public void doTask(Menu menu) {
			MiceInvaders.MiceInvaders.multi_input.removeProcessor(MainMenu.current_menu);
			Controllers.removeListener(MainMenu.current_menu);
			MainMenu.current_menu = MainMenu.gamemode_selection;
			MiceInvaders.MiceInvaders.multi_input.addProcessor(MainMenu.current_menu);
			Controllers.addListener(MainMenu.current_menu);
		}
		
	}
	
	static class PlayerControlOptions implements Menu.SelectorCallback{
		
		@Override
		public void doTask(Selector selector) {
			// TODO Auto-generated method stub
			if (selector.number == selector.max_number) selector.number -= 1;
			PlayerInput.type = PlayerInput.Type.values()[selector.number];
			MainMenu.MainMenu.setPlayersOptions();
		}
		
	}
	
	static class Start1Player implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.Engine.players = 1;
			Engine.Engine.startGame();
			MiceInvaders.MiceInvaders.setEngine();
		}
		
	}
	
	static class Start2Players implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.Engine.players = 2;
			Engine.Engine.startGame();
			MiceInvaders.MiceInvaders.setEngine();
		}
		
	}
	
	static class Start3Players implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.Engine.players = 3;
			Engine.Engine.startGame();
			MiceInvaders.MiceInvaders.setEngine();
		}
		
	}
	
	static class Start4Players implements Menu.Callback{

		@Override
		public void doTask(Element element) {
			// TODO Auto-generated method stub
			Engine.Engine.players = 4;
			Engine.Engine.startGame();
			MiceInvaders.MiceInvaders.setEngine();
		}
		
	}
	
	static{
		MiceInvaders.MiceInvaders.big_font.getBounds(Title, title_textbounds);
		MiceInvaders.MiceInvaders.small_font.getBounds(author, author_textbounds);
		MiceInvaders.MiceInvaders.small_font.getBounds(version, version_textbounds);
	}
	
	private MainMenu(){
		gamemode_selection = new Menu();
		Element element_challenge = new Menu.Element();
		element_challenge.setName("Challenge");
		element_challenge.callback = new StartChallengeMode();
		Element element_exit = new Menu.Element();
		element_exit.setName("Exit");
		element_exit.callback = new Exit();
		Element element_endless = new Menu.Element();
		element_endless.setName("Endless");
		element_endless.disabled = true;
		Element element_options = new Menu.Element();
		element_options.setName("Options");
		element_options.callback = new Options();
				
		gamemode_selection.addElement(element_challenge);
		gamemode_selection.addElement(element_endless);
		gamemode_selection.addElement(element_options);
		gamemode_selection.addElement(element_exit);
		gamemode_selection.current_element = 0;
		
		current_menu = gamemode_selection;
		
		player_options = new Menu();
		player_options.back_callback = new BackPlayerOptions();
		Selector select_player_control = new Menu.Selector(2, "First player keyboard", "All players gamepad");
		select_player_control.selectorCallback = new PlayerControlOptions();
		player_options.addElement(select_player_control);
		Element one_player = new Menu.Element();
		one_player.setName("Start 1 Player");
		one_player.callback = new Start1Player();
		Element two_players = new Menu.Element();
		two_players.setName("Start 2 Players");
		two_players.callback = new Start2Players();
		two_players.disabled = true;
		Element three_players = new Menu.Element();
		three_players.setName("Start 3 players");
		three_players.callback = new Start3Players();
		three_players.disabled = true;
		Element four_players = new Menu.Element();
		four_players.setName("Start 4 players");
		four_players.callback = new Start4Players();
		four_players.disabled = true;
		player_options.addElement(one_player);
		player_options.addElement(two_players);
		player_options.addElement(three_players);
		player_options.addElement(four_players);
		
		app_options = new Menu();
		app_options.back_callback = new BackAppOptions();
		Selector options_sound = new Menu.Selector(5);
		options_sound.setName("Sound");
		options_sound.setNumber(0);
		options_sound.selectorCallback = MiceInvaders.SoundControl;
		Selector options_music = new Menu.Selector(5);
		options_music.setNumber(0);
		options_music.setName("Music");
		options_music.selectorCallback = MiceInvaders.MusicControl;
		app_options.addElement(options_sound);
		app_options.addElement(options_music);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		SpriteBatch batch = MiceInvaders.MiceInvaders.batch;
		batch.setProjectionMatrix(MiceInvaders.MiceInvaders.screen_camera.combined);
		batch.begin();
		MiceInvaders.MiceInvaders.big_font.setColor(Color.BLACK);
		MiceInvaders.MiceInvaders.big_font.draw
		(batch, Title, (MiceInvaders.VWIDTH - title_textbounds.width) / 2.f, MiceInvaders.VHEIGHT - title_textbounds.height);
		MiceInvaders.MiceInvaders.small_font.setColor(Color.BLACK);
		MiceInvaders.MiceInvaders.small_font.draw(batch, author, 8.f, 16.f);
		MiceInvaders.MiceInvaders.small_font.draw(batch, version, MiceInvaders.VWIDTH - (8.f + version_textbounds.width), 16.f);
		
		for(int i = 0; i < current_menu.elements_added; i += 1){
			Element current_element = current_menu.elements.get(i);
			if (current_element instanceof Menu.Selector){
				Menu.Selector selector = (Selector) current_element;
				if (selector.tag != null){
					for(int i2 = 0; i2 < selector.max_number; i2 += 1){
						if (i == current_menu.current_element && i2 == selector.number){
							MiceInvaders.MiceInvaders.normal_font.setColor(Color.DARK_GRAY);
						}else if(i != current_menu.current_element && i2 == selector.number){
							MiceInvaders.MiceInvaders.normal_font.setColor(Color.WHITE);
						}else{
							MiceInvaders.MiceInvaders.normal_font.setColor(Color.ORANGE);
						}
						MiceInvaders.MiceInvaders.normal_font.draw(batch, selector.tag[i2], MiceInvaders.VWIDTH/4.f + i2 * (MiceInvaders.VWIDTH/3.f), MiceInvaders.VHEIGHT / 2.f - MiceInvaders.PPM * i);
					}
				}
				else{
					if (current_menu.current_element == i){
						MiceInvaders.MiceInvaders.normal_font.setColor(Color.WHITE);
					}else if (current_element.disabled){
						MiceInvaders.MiceInvaders.normal_font.setColor(Color.CLEAR);
					}else{
						MiceInvaders.MiceInvaders.normal_font.setColor(Color.ORANGE);
					}
					MiceInvaders.MiceInvaders.normal_font.draw(batch, selector.name + " " + selector.number + " / " + selector.max_number, MiceInvaders.VWIDTH/4.f, MiceInvaders.VHEIGHT / 2.f - MiceInvaders.PPM * i);
				}
			}else{//if the element is not a selector...
				if (current_menu.current_element == i){
					MiceInvaders.MiceInvaders.normal_font.setColor(Color.WHITE);
				}else if (current_element.disabled){
					MiceInvaders.MiceInvaders.normal_font.setColor(Color.CLEAR);
				}else{
					MiceInvaders.MiceInvaders.normal_font.setColor(Color.ORANGE);}
				}
			MiceInvaders.MiceInvaders.normal_font.draw(batch, current_element.name, MiceInvaders.VWIDTH/4.f, MiceInvaders.VHEIGHT / 2.f - MiceInvaders.PPM * i);
		}
		
		batch.end();
	}

	public void setPlayersOptions(){
		int connected_gamepads = Controllers.getControllers().size;
		for(int i = 0; i < Engine.PLAYERS; i += 1){
			//1 is the players control type
			Element element = MainMenu.player_options.elements.get(1 + i);
			if (PlayerInput.type == Type.ALL_PLAYERS_GAMEPAD){
				if (i < connected_gamepads){
					element.disabled = false;
					continue;
				}
				element.disabled = true;
			}
			if (PlayerInput.type == Type.FIRST_PLAYER_KEYBOARD){
				if (i < connected_gamepads+1){//+1 is the keyboard...
					element.disabled = false;
					continue;
				}
				element.disabled = true;
			}
		}
		
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

}
