package dotteri.MiceInvaders;

public class Shoot {
	
	float ratio = 0.f;
	float ratio_time = 0.f;
	
	public void setShootsBySec(float shoots){
		ratio = 1.f/shoots;
	}
	
	public void recharge(float delta){
		ratio_time += delta;
	}
	
	public boolean shoot(){
		if (ratio_time > ratio){
			ratio_time = 0.f;
			return true;
		}
		return false;
	}
	
}
