package simpleJMS;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class SimpleJMSMessage implements Serializable{
	private Enum<?> eventType; //Type of message which corresponds to the Type of SimpleJMSEventHandler registered
	private long fireOn; //Date and time in milliseconds when the message should fire
	private HashMap<String, Object> props = new HashMap<>();
	
	public SimpleJMSMessage(Enum<?> eventType){
		this.eventType = eventType;
	}
	public SimpleJMSMessage(Enum<?> eventType, long fireOn){
		this.eventType = eventType;
		this.fireOn = fireOn;
	}
	
	//SETTERS
	/**
	 * @param type - Type of message which corresponds to the Type of SimpleJMSEventHandler registered
	 */
	public SimpleJMSMessage setType(Enum<?> eventType){
		this.eventType = eventType;
		return this; 
	}
	/**
	 * @param fireOn - Date and time in milliseconds when the message should fire
	 */
	public SimpleJMSMessage setFireOn(long fireOn){
		this.fireOn = fireOn;
		return this; 
	}
	public SimpleJMSMessage set(String s, Object o){
		props.put(s, o);
		return this; 
	}

	//GETTERS
	public Enum<?> getType(){
		return eventType;
	}
	public long getFireOn(){
		return fireOn;
	}
	public Object get(String s){
		return props.get(s);
	}
	public String getString(String s){
		return (String)props.get(s);
	}
	public Long getLong(String s){
		return (Long)props.get(s);
	}
	public Integer getInt(String s){
		return (Integer)props.get(s);
	}
	public boolean getBool(String s){
		return "true".equals(props.get(s));
	}
}
