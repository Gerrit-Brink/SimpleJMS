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
	private LinkedBlockingQueue<Path> msgsToProcess;
	private Timer timer;
	private MessageProcessorThread msgProcessor;
	
	public SimpleJMS(String location) throws Exception{
		queueLocation = Paths.get(location);
		if(!Files.exists(queueLocation))
			Files.createDirectory(queueLocation);
		
		msgProcessor = new MessageProcessorThread(queueLocation);
	}
	
	public void start() throws Exception{
		msgsToProcess = new LinkedBlockingQueue<Path>();
		msgProcessor.setMessageToProcess(msgsToProcess).start();
		timer = new Timer("SimpleJMS Timer");
		for(Path p : Files.newDirectoryStream(queueLocation))
			queueMessage(p.getFileName());
	}
	
	private void queueMessage(Path fileName) throws InterruptedException{
		long executeOn = new Long(fileName.toString().split("_")[0]);
		if(executeOn <= System.currentTimeMillis()){
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
		Path fileName = Paths.get(msg.getFireOn() + "_" + UUID.randomUUID().toString().replace("-", ""));
		Util.writeObject(msg, queueLocation.resolve(fileName));
		if(msgsToProcess != null)//If the msgsToProcess is null then it means the Queue hasn't been started
			queueMessage(fileName);
	}
	
	public SimpleJMS registerEventHandler(String type, SimpleJMSEventHandler proc){
		msgProcessor.registerEventHandler(type, proc);
		return this;
	}
}

class MessageProcessorThread extends Thread{
	private Path queueLocation;
	private LinkedBlockingQueue<Path> messagesToProcess;
	private Map<String, SimpleJMSEventHandler> eventHandlers = new ConcurrentHashMap<>();
	public MessageProcessorThread(Path l){
		queueLocation = l;
	}
	public MessageProcessorThread setMessageToProcess(LinkedBlockingQueue<Path> q){
		messagesToProcess = q;
		return this;
	}
	public void registerEventHandler(String type, SimpleJMSEventHandler proc){
		eventHandlers.put(type, proc);
	}
	public void run(){
		try{
			Path fileName = messagesToProcess.take();
			while(fileName != null){
				Path fullMsgPath = queueLocation.resolve(fileName);
				SimpleJMSMessage msg = (SimpleJMSMessage)Util.readObject(fullMsgPath);
				eventHandlers.get(msg.getType()).onMessage(msg);
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
