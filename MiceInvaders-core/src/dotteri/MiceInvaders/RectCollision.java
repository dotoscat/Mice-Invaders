package dotteri.MiceInvaders;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RectCollision{
	
	Rectangle rect;
	
	boolean cheese = false;
	int collide = 0;
	
	public void reset(){
		collide = 0;
	}
	
	public RectCollision(){
		rect = new Rectangle();
	}
	
	public void setCheese(boolean set){
		cheese = set;
	}
	
	public boolean isCheese(){
		return cheese;
	}
	
	public Rectangle getRectangle(){
		return rect;
	}
	
	boolean collidesWithFloorPoints(final Vector2[] points){
		return rect.contains(points[0]) || rect.contains(points[1]);
	}
	
	float returnDepth(final Vector2 point){
		return (rect.y + rect.height) - point.y;
	}
	
}
