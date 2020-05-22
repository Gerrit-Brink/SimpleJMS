package simpleJMS;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class SimpleJMSMessage implements Serializable{
	private String type; //Type of message which corresponds to the Type of SimpleJMSEventHandler registered
	private long fireOn; //Date and time in milliseconds when the message should fire
	private HashMap<String, Object> props = new HashMap<>();
	
	public SimpleJMSMessage(String type){
		this.type = type;
	}
	public SimpleJMSMessage(String type, long fireOn){
		this.type = type;
		this.fireOn = fireOn;
	}
	
	//SETTERS
	/**
	 * @param type - Type of message which corresponds to the Type of SimpleJMSEventHandler registered
	 */
	public SimpleJMSMessage setType(String type){
		this.type = type;
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
	public String getType(){
		return type;
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
