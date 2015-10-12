package network;

import gameworld.Game;
import gameworld.Player;

import java.awt.event.KeyEvent;
import java.io.Serializable;

/**
 * 
 * @author Matt Byers
 * 
 * Network Events are used for transferring data through the socket input/output streams.
 * 
 * Network Events can be of various types that hold different types of data therefore
 * the EventType must be checked before calling any of the getters as they will be null if the type is incorrect.
 */
public class NetworkEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//The type of event that occurred.
	private final EventType type;
	
	//KeyCode of the pressed key, -1 if this is not a KEY_PRESS event.
	private final int keyCode;
	
	//Message, null if not a MESSAGE event.
	private final String message;
	
	//Username of the client that created the NetworkEvent
	private final String user;
	
	//The direction the client should move the player in.
	private final Game.Direction dir;
	
	//Game state used for broadcasting a gui update to clients
	private Player state;
	
	
	/**
	 * Creates a network event of the KEY_PRESS type.
	 * @param e - The KeyEvent associated with the event.
	 * @param user - The user that created the event.
	 */
	public NetworkEvent(KeyEvent e, String user){
		this.type = EventType.KEY_PRESS;
		this.keyCode = e.getKeyCode();
		this.user = user;
		
		this.message = null;
		this.dir = null;
	}
	
	/**
	 * Creates a network event of the type MESSAGE.
	 * @param msg - The message.
	 * @param user - The user that created the event.
	 */
	public NetworkEvent(String msg, String user){
		this.type = EventType.MESSAGE;
		this.message = msg;
		this.user = user;
		
		this.keyCode = -1;
		this.dir = null;
	}
	
	/**
	 * Network event with Game parameter signals a server event,
	 * that tells the clients GUI to update.
	 */
	public NetworkEvent(Player state){
		this.type = EventType.UPDATE_GAME;
		this.user = "Server";
		this.state = state; 
		
		this.keyCode = -1;
		this.message = null;
		this.dir = null;
	}
	
	/**
	 * User events of this type don't require any extra parameters, therefore need to be given a specific type.
	 * @param user - The user of the event.
	 * @param type - The event type.
	 */
	public NetworkEvent(String user, EventType type){
		this.type = type;
		this.user = user;
		
		this.state = null;
		this.keyCode = -1;
		this.message = null;
		this.dir = null;
	}
	
	public NetworkEvent(String user, Game.Direction dir){
		this.type = EventType.MOVE_PLAYER;
		this.user = user;
		this.dir = dir;
		
		this.state = null; 
		this.keyCode = -1;
		this.message = null;
	}
	
	
	//Getters
	public String getUser() { return user; }
	public EventType getType() { return type; }
	public int getKeyCode() { return keyCode; }
	public String getMessage() { return message; }
	public Player getState() { return state; }
	public Game.Direction getDir() { return dir; }
	
	//All possible types of NetworkEvents.
	public enum EventType{
		KEY_PRESS,
		MESSAGE,
		UPDATE_GAME,
		CYCLE_ANIMATIONS,
		MOVE_PLAYER,
		CLOSE;
	}
	
}
