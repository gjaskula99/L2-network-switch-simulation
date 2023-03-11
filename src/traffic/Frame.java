package traffic;

public class Frame {
	public Frame()
	{
		this.length = 0;
		this.sourceAddress = new l2.MAC();
		this.destinationAddress = new l2.MAC();
		this.state = state.BLANK;
	}
	
	public Frame(int initLength, int sourceInterface, int destInterface, int sourceHost, int destHost)
	{
		if(initLength < 1) this.length = 1;
		else if(initLength > 24) this.length = 24;
		else this.length = initLength;
		this.sourceAddress = new l2.MAC(sourceInterface, sourceHost);
		this.destinationAddress = new l2.MAC(destInterface, destHost);
		this.state = state.INIT;
	}
	
	public Frame(int initLength, int sourceInterface, int sourceHost)
	{
		if(initLength < 1) this.length = 1;
		else if(initLength > 24) this.length = 24;
		else this.length = initLength;
		this.sourceAddress = new l2.MAC(sourceInterface, sourceHost);
		this.destinationAddress = new l2.MAC();
		this.state = state.INIT;
	}
	
	int length; //64 - 1536 (1-24 * 64)
	l2.MAC sourceAddress;
	l2.MAC destinationAddress;
	
	enum State {BLANK, INIT, RECEIVING, RECEIVED, SWITCINHG, SWITCHED, TRANSMITING, DONE, LOST};
	public State state;
	
	public int getLength()
	{
		return this.length;
	}
	public l2.MAC getSource()
	{
		return this.sourceAddress;
	}
	public l2.MAC getDestination()
	{
		return this.destinationAddress;
	}
	public String getString()
	{
		String str = "";
		str += "SRC:";
		str += this.sourceAddress.getString() + " DST:";
		str += this.destinationAddress.getString() + " L=";;
		str += this.length + "\n";
		return str;
	}
}
