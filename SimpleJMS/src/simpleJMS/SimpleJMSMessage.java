package simpleJMS;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class SimpleJMSMessage implements Serializable{
	private String type;
	private long fireOn;
	private HashMap<String, Object> props = new HashMap<>();
	
	public SimpleJMSMessage(String type){
		this.type = type;
	}
	public String getType(){
		return type;
	}
	public void setType(String type){
		this.type = type;
	}
	public long getFireOn(){
		return fireOn;
	}
	public void setFireOn(long fireOn){
		this.fireOn = fireOn;
	}
	public void put(String s, Object o){
		props.put(s, o);
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
	public Object getObj(String s){
		return props.get(s);
	}
}
