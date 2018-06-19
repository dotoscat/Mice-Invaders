package dotteri.MiceInvaders;

public class Player {
	int entity = -1;
	public long points = 0L;
	StringBuilder points_str = new StringBuilder();
	long multiplier;
	
	boolean movement_input_pressed = false;
	boolean jump_pressed = false;
	
	int MAX_JUMPS = 2;
	int jumps = 2;
	
	enum Facing{
		Left,
		Right
	}
	Facing facing = Facing.Left;
	
	public void reset(){
		entity = -1;
		points = 0L;
		multiplier = 1L;
		jumps = MAX_JUMPS;
	}
	
	public CharSequence getPointsStr(){
		points_str.setLength(0);
		points_str.append(points);
		return points_str;
	}
	
	boolean jump(){
		if (jump_pressed == false && jumps > 0){
			jumps -= 1;
			jump_pressed = true;
			return true;
		}
		return false;
	}
	
	void setJumpNoPressed(){
		jump_pressed = false;
	}
		
	public void setMaxJumps(int new_max_jumps){
		MAX_JUMPS = new_max_jumps;
	}
		
	public void resetJumps(){
		jumps = MAX_JUMPS;
	}
	
	public int getRemainingJumps(){
		return MAX_JUMPS - jumps;
	}
	
	public void setFacingLeft(){
		facing = Facing.Left;
	}
	
	public void setFacingRight(){
		facing = Facing.Right;
	}
	
	public boolean isFacingLeft(){
		return facing == Facing.Left;
	}
	
	public boolean isFacingRight(){
		return facing == Facing.Right;
	}
	
}
