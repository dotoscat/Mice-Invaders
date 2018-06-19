package dotteri.MiceInvaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IGraphic {

	public void update(final float delta, final float x, final float y);
	public void draw(SpriteBatch batch);
	
}
