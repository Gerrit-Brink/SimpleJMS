package simpleJMS;

public interface SimpleJMSProcessor{
	public void onMessage(SimpleJMSMessage msg);
}
