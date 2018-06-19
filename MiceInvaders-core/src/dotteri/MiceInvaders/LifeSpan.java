package dotteri.MiceInvaders;

public class LifeSpan {
	
	float time;
	float maxtime;
	
	public void reset(){
		time = 0.f;
		maxtime = 0.f;
	}
	
	public boolean isOver(){
		return time > maxtime;
	}
	
}
