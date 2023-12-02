package l2;

public class MAC {
	public MAC()
	{
		String str = "FFFFFFFFFFFF";
		this.address = str.toCharArray();
	}
	
	public MAC(int interfaceID, int hostID)
	{
		String str = "172818000";
		str += Integer.toString(interfaceID);
		//this.address = str.toCharArray();
		str += "0";
		str += Integer.toString(hostID);
		this.address = str.toCharArray();
	}
	
	public MAC(String address)
	{
		this.address = address.toCharArray();
	}
	
	char[] address = new char[12];
	
	public char[] getMAC()
	{
		return this.address;
	}
	public boolean isBroadcast()
	{
		String str = "FFFFFFFFFFFF";
		if(this.address == str.toCharArray()) return true;
		return false;
	}
	public String getString()
	{
		String str = "";
		for(int i = 0; i < 12; i++)
		{
			str += this.address[i];
			str += this.address[++i];
			if(i != 11) str += "-";
		}
		return str;
	}
	public char getInterface()
	{
		return this.address[9];
	}
	public char getHost()
	{
		return this.address[11];
	}
}
