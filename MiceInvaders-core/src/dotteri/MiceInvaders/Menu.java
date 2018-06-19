package dotteri.MiceInvaders;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Menu implements InputProcessor, ControllerListener{

	static public class Element implements InputProcessor, ControllerListener{
		
		StringBuilder name;
		public Callback callback;
		public boolean disabled = false;
		
		public Element(){
			name = new StringBuilder();
			callback = null;
		}
		
		public void setName(String new_name){
			name.setLength(0);
			name.append(new_name);
		}
		
		public void confirm(){
			if (callback != null) callback.doTask(this);
		}

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
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
		public boolean touchDown(int screenX, int screenY, int pointer,
				int button) {
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
			return false;
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonCode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean axisMoved(Controller controller, int axisCode,
				float value) {
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
		
	}
		
	static public class Selector extends Element{
		
		int max_number;
		public int number = 0;
		StringBuilder str_number;
		public SelectorCallback selectorCallback;
		public String[] tag;
		
		public Selector(int max){
			max_number = max;
			str_number = new StringBuilder();
		}
		
		public Selector(int max, String... tags){
			max_number = max;
			str_number = new StringBuilder();
			tag = tags;
		}
		
		public void setNumber(int new_number){
			if (new_number <= 0) number = 0;
			if (new_number >= max_number) number = max_number;
			number = new_number;
			str_number.setLength(0);
			str_number.append(number);
		}
		
		public boolean increment(){
			if (number + 1 <= max_number){
				number += 1;
				str_number.setLength(0);
				str_number.append(number);
				return true;
			}
			return false;
		}
		
		public boolean decrement(){
			if (number - 1 >= 0){
				number -= 1;
				str_number.setLength(0);
				str_number.append(number);
				return true;
			}
			return false;
		}
		
		@Override
		public boolean keyDown(int keycode){
			if (keycode == key_left){
				decrement();
				if (selectorCallback != null) selectorCallback.doTask(this);
			}
			if (keycode == key_right){
				increment();
				if (selectorCallback != null) selectorCallback.doTask(this);
			}
			return false;
		}
				
		@Override
		public boolean povMoved(Controller controller, int povCode,
				PovDirection value) {
			// TODO Auto-generated method stub
			if (value == PovDirection.west){
				decrement();
				if (selectorCallback != null) selectorCallback.doTask(this);
			}
			if (value == PovDirection.east){
				increment();
				if (selectorCallback != null) selectorCallback.doTask(this);
			}
			return false;
		}
		
	}
	
	static public interface BackCallback{
		public void doTask(Menu menu);
	}
	
	static public interface Callback{
		public void doTask(Element element);
	}
	
	static public interface SelectorCallback{
		public void doTask(Selector selector);
	}
	
	int current_element = 0;
	Array<Element> elements;
	int elements_added = 0;
	
	public BackCallback back_callback;
	
	static int key_up = Keys.UP;
	static int key_down = Keys.DOWN;
	static int key_left = Keys.LEFT;
	static int key_right = Keys.RIGHT;
	static int key_ok = Keys.X;
	static int key_back = Keys.Z;
	
	static int button_ok = 0;
	static int button_back = 1;
	
	public Menu(){
		elements = new Array<Element>();
	}
	
	public void addElement(Element element){
		elements.add(element);
		elements_added += 1;
	}
	
	public void increment(){
		int n = 1;
		while(current_element + n < elements_added){
			if (elements.get(current_element + n).disabled){
				n += 1;
				continue;
			}
			else{
				current_element += n;
				return;
			}
		}
	}
	
	public void decrement(){
		int n = 1;
		while(current_element - n >= 0){
			if (elements.get(current_element - n).disabled){
				n += 1;
				continue;
			}
			else{
				current_element -= n;
				return;
			}
		}
	}
	
	public void confirm(){
		Element element = elements.get(current_element);
		if (!element.disabled) elements.get(current_element).confirm();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (key_down == keycode){
			increment();
		}
		if (key_up == keycode){
			decrement();
		}
		if (key_ok == keycode){
			confirm();
		}
		if (key_back == keycode && back_callback != null){
			back_callback.doTask(this);
		}
		return elements.get(current_element).keyDown(keycode);
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
		if (buttonCode == button_ok){
			confirm();
		}
		if (buttonCode == button_back && back_callback != null){
			back_callback.doTask(this);
		}
		return elements.get(current_element).buttonDown(controller, buttonCode);
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
		if (value == PovDirection.south){
			increment();
		}
		if (value == PovDirection.north){
			decrement();
		}
		return elements.get(current_element).povMoved(controller, povCode, value);
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
		
}
