package dotteri.MiceInvaders;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	static public MiceInvaders MiceInvaders = null;
	
	public static void main(String[] args) {		
		Main.MiceInvaders = new MiceInvaders();
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Metal Cheese Maniacs";
		cfg.useGL20 = true;
		cfg.width = 960;
		cfg.height = 640;
		cfg.resizable = false;
		
		new LwjglApplication(Main.MiceInvaders, cfg);
	}
}
