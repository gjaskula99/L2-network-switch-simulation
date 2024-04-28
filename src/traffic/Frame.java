package traffic;

public class Frame {
	public Frame()
	{
		this.length = 0;
		this.sourceAddress = new l2.MAC();
		this.destinationAddress = new l2.MAC();
	}
	
	public Frame(int initLength, int sourceInterface, int destInterface, int sourceHost, int destHost)
	{
		if(initLength < 64) this.length = 64;
		else if(initLength > 1536) this.length = 1536;
		else this.length = initLength;
		this.sourceAddress = new l2.MAC(sourceInterface, sourceHost);
		this.destinationAddress = new l2.MAC(destInterface, destHost);
		this.isBroadcast = false;
	}
	
	public Frame(int initLength, int sourceInterface, int sourceHost)
	{
		if(initLength < 64) this.length = 64;
		else if(initLength > 1536) this.length = 1536;
		else this.length = initLength;
		this.sourceAddress = new l2.MAC(sourceInterface, sourceHost);
		this.destinationAddress = new l2.MAC();
		this.isBroadcast = true;
	}
	
	public Frame(String mac)
	{
		this.length = 64;
		this.sourceAddress = new l2.MAC(mac);
		this.destinationAddress = new l2.MAC();
		this.isBroadcast = true;
	}
	
	int length; //64 - 1536 (1-24 * 64)
	l2.MAC sourceAddress;
	l2.MAC destinationAddress;
	Boolean isBroadcast;
	
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
	public Boolean getBroadcast()
	{
		return this.isBroadcast;
	}
	public void setBroadcast()
	{
		this.isBroadcast = true;
	}
}
