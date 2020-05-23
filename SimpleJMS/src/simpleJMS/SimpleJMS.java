package simpleJMS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleJMS{
	
	private Path queueLocation;
	private LinkedBlockingQueue<String> msgsToProcess;
	private Timer timer;
	private MessageProcessorThread msgProcessor;
	private Map<Enum<?>, SimpleJMSEventHandler> eventHandlers;
	
	public SimpleJMS(String location) throws Exception{
		queueLocation = Files.createDirectories(Paths.get(location));
		msgProcessor = new MessageProcessorThread();
		eventHandlers = new ConcurrentHashMap<>();
	}
	
	public void start() throws Exception{
		msgsToProcess = new LinkedBlockingQueue<>();
		msgProcessor.setMessagesToProcess(msgsToProcess).start();
		timer = new Timer("SimpleJMS Timer");
		
		for(Path p : Files.newDirectoryStream(queueLocation))
			queueMessage(p.getFileName().toString());
	}
	
	private void queueMessage(String fileName) throws InterruptedException{
		long executeOn = new Long(fileName.substring(0, fileName.indexOf("_")));
		if(System.currentTimeMillis() >= executeOn){
			msgsToProcess.put(fileName);
		}else{
			timer.schedule(new TimerTask(){
				public void run(){
					try{
						msgsToProcess.put(fileName);
					}catch(InterruptedException e){}
				}
			}, new Date(executeOn));
		}
	}
	
	public void stop(){
		if(timer != null)
			timer.cancel();
		timer = null;
		
		msgProcessor.interrupt();
		msgsToProcess = null;
	}
	
	public void addMessage(SimpleJMSMessage msg) throws Exception{
		String fileName = msg.getFireOn() + "_" + UUID.randomUUID();
		Util.writeObject(msg, queueLocation.resolve(fileName));
		if(msgsToProcess != null)//If the msgsToProcess is null then it means the Queue hasn't been started
			queueMessage(fileName);
	}
	
	public SimpleJMS registerEventHandler(Enum<?> eventType, SimpleJMSEventHandler proc){
		eventHandlers.put(eventType, proc);
		return this;
	}
	
	class MessageProcessorThread extends Thread{
		private LinkedBlockingQueue<String> messagesToProcess;
		public MessageProcessorThread setMessagesToProcess(LinkedBlockingQueue<String> q) {
			messagesToProcess = q;
			return this;
		}
		public void run(){
			try{
				String fileName = messagesToProcess.take();
				while(fileName != null){
					Path fullMsgPath = queueLocation.resolve(fileName);
					SimpleJMSMessage msg = (SimpleJMSMessage)Util.readObject(fullMsgPath);
					eventHandlers.get(msg.getEventType()).onMessage(msg);
					Files.delete(fullMsgPath);
					
					fileName = messagesToProcess.take();
				}
			}catch(InterruptedException e){
				System.out.println("SimpleJMS.ProcessorThread Stopping for location = " + queueLocation);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

class Util{

	static void writeObject(Object o, Path p) throws IOException{
		try(FileOutputStream fos = new FileOutputStream(p.toString());
				ObjectOutputStream oos = new ObjectOutputStream(fos)){
			oos.writeObject(o);
		}
	}
	
	static Object readObject(Path p) throws IOException, ClassNotFoundException{
		try(FileInputStream fis = new FileInputStream(p.toString());
				ObjectInputStream ois = new ObjectInputStream(fis)){
			return ois.readObject();
		}
	}
}
