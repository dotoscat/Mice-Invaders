package dotteri.MiceInvaders;

import com.badlogic.gdx.Input.Keys;

public class PlayerInput{
			
	static int jump = Keys.Z;//cancel
	static int shoot = Keys.X;//confirm
	static int pause = Keys.C;
		
	static int key_left = Keys.LEFT;
	static int key_right = Keys.RIGHT;
	static int key_up = Keys.UP;
	static int key_down = Keys.DOWN;
	
	static enum Type{
		FIRST_PLAYER_KEYBOARD,
		ALL_PLAYERS_GAMEPAD
	}
	static public Type type = Type.FIRST_PLAYER_KEYBOARD;
		
}
