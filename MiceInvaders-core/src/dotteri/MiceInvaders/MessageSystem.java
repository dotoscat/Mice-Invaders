package dotteri.MiceInvaders;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;

import dotteri.MiceInvaders.CircleCollision.Owner;
import dotteri.MiceInvaders.MessageSystem.Message;

public class MessageSystem implements Iterable<Message>, Iterator<Message> {

	final static public MessageSystem MessageSystem = new MessageSystem();
		
	static public enum Type{
		None,
		CreateExplosion,
		CreateBullet,
		DestroyEntity,
		CreatePowerUp,
	}
	
	static public class Message{
		public Type type = Type.None;
		public float position_x;
		public float position_y;
		public float vel_x;
		public float vel_y;
		public float duration;
		public Color color;
		public Owner owner;
		public String key;
		public int i = 0;
	}
	
	private int MESSAGES = 64;
	private Message[] message = new Message[MESSAGES];
	private int current_message = 0;
	
	private MessageSystem(){
		for(int i = 0; i < MESSAGES; i += 1) message[i] = new Message();
	}
	
	public Message addMessage (Type type){
		for(Message a_message: message){
			if (a_message.type == Type.None){
				a_message.type = type;
				return a_message;
			}
		}
		return null;
	}
		
	public void cleanMessages(){
		for(Message a_message: message) a_message.type = Type.None;
	}

	@Override
	public Iterator<Message> iterator() {
		current_message = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return current_message < MESSAGES-1;
	}

	@Override
	public Message next() {
		if (current_message == -1){
			current_message += 1;
			return message[0];
		}
		message[current_message].type = Type.None;
		current_message += 1;
		return message[current_message];
	}

	@Override
	public void remove() {
		//does nothing
	}
	
}
