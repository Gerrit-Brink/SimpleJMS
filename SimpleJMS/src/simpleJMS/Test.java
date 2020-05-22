package simpleJMS;

public class Test{
	
	//Set some System Properties, this would typically be done outside the web/app server in a config file somewhere
	static{
		System.setProperty("queue.another.location", "C:/Users/JDev/Desktop/q2");
		System.setProperty("queue.onemore.location", "C:/Users/JDev/Desktop/q3");
	}
	
	private static SimpleJMS myQueue, anotherQueue, oneMoreQueue;//Define as many as you want, each has it's own execution thread
	
	private static final String EVENT_NOTIFY_CLIENT 	= "EVENT_NOTIFY_CLIENT",
								EVENT_SEND_EMAIL		= "EVENT_SEND_EMAIL",
								EVENT_REMIND_ME_LATER 	= "EVENT_REMIND_ME_LATER";
	
	public static void main(String[] args){
		try{
			//Initialize the JMS with a storage location for message persistence, create as many as you want
			myQueue 	 = new SimpleJMS("C:/Users/JDev/Desktop/q");
			anotherQueue = new SimpleJMS(System.getProperty("queue.another.location"));
			oneMoreQueue = new SimpleJMS(System.getProperty("queue.onemore.location"));
			
			//Register event handlers in-line
			myQueue.registerEventHandler(EVENT_NOTIFY_CLIENT, (SimpleJMSMessage)->{
				System.out.println("EVENT ONE FIRED = " + EVENT_NOTIFY_CLIENT);
			}).registerEventHandler(EVENT_SEND_EMAIL, (SimpleJMSMessage)->{
				System.out.println("EVENT TWO FIRED = " + EVENT_SEND_EMAIL);
			});
			
			//Register separated event handlers for larger code sets, RemindMeLaterClass implemented lower down in this test class
			myQueue.registerEventHandler(EVENT_REMIND_ME_LATER, new RemindMeLaterClass());
			
			//Add Messages to Queue, the queue hasn't started here so they will be persisted to disk
			myQueue.addMessage(new SimpleJMSMessage(EVENT_NOTIFY_CLIENT));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_SEND_EMAIL));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_REMIND_ME_LATER, System.currentTimeMillis() + 500));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_SEND_EMAIL));
			
			//Start the Queue
			myQueue.start();
			
			//Add more messages to the Queue
			myQueue.addMessage(new SimpleJMSMessage(EVENT_NOTIFY_CLIENT,   System.currentTimeMillis() + 100));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_REMIND_ME_LATER, System.currentTimeMillis() + 5000));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_NOTIFY_CLIENT,   System.currentTimeMillis() + 200));
			myQueue.addMessage(new SimpleJMSMessage(EVENT_NOTIFY_CLIENT));
			
			//myQueue.stop();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

class RemindMeLaterClass implements SimpleJMSEventHandler{
	public void onMessage(SimpleJMSMessage msg){
		System.out.println("EVENT THREE FIRED = EVENT_REMIND_ME_LATER");
	}
}
