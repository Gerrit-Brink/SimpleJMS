package simpleJMS;

public class Test{
	public static void main(String[] args){
		try{
			SimpleJMS j = new SimpleJMS("C:/Users/JDev/Desktop/q");
			
			j.registerProcessor("EventType1", (SimpleJMSMessage)->{
				System.out.println("1");
			});
			j.registerProcessor("EventType2", (SimpleJMSMessage)->{
				System.out.println("2");
			});
			j.registerProcessor("EventType3", (SimpleJMSMessage)->{
				System.out.println("3");
			});
			
			j.addMessage(createMsg("EventType1", 0));
			j.addMessage(createMsg("EventType2", 0));
			j.addMessage(createMsg("EventType3", 500));
			j.addMessage(createMsg("EventType3", 2000));
			
			j.addMessage(createMsg("EventType2", 0));
			j.addMessage(createMsg("EventType1", 100));
			j.addMessage(createMsg("EventType1", 200));
			j.addMessage(createMsg("EventType1", 0));
			
			j.start();
			
			//j.stop();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static SimpleJMSMessage createMsg(String type, long delay){
		SimpleJMSMessage m = new SimpleJMSMessage(type);
		if(delay > 0)
			m.setFireOn(System.currentTimeMillis() + delay);
		
		return m;
	}
}
