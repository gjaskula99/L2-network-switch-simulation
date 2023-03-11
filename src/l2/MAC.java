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
		this.address = str.toCharArray();
		str += "0";
		str += Integer.toString(hostID);
		this.address = str.toCharArray();
	}
	
	char[] address = new char[12];
	
	public char[] getMAC()
	{
		return this.address;
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
}
