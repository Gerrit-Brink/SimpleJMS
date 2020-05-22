package simpleJMS;

public class Test{
	public static void main(String[] args){
		try{
			SimpleJMS j = new SimpleJMS("C:/Users/JDev/Desktop/q");
			
			j.registerProcessor("1", (SimpleJMSMessage)->{
				System.out.println("1");
			});
			j.registerProcessor("2", (SimpleJMSMessage)->{
				System.out.println("2");
			});
			j.registerProcessor("3", (SimpleJMSMessage)->{
				System.out.println("3");
			});
			
			j.addMessage(createMsg("1", 0));
			j.addMessage(createMsg("2", 0));
			j.addMessage(createMsg("3", 500));
			j.addMessage(createMsg("3", 2000));
			
			j.addMessage(createMsg("2", 0));
			j.addMessage(createMsg("1", 0));
			j.addMessage(createMsg("1", 0));
			j.addMessage(createMsg("1", 0));
			
			j.start();
			
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
