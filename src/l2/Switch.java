package l2;

import l2.Interface.State;

public class Switch {
	public Switch(int buffer)
	{
		ethernet = new Interface[8];
		for(int i = 0; i < 8; i++) ethernet[i] = new Interface(i, buffer);
		CAM = new MACTable(100, 32);
	}
	public Switch(int buffer, int hosts, int CAMsize)
	{
		ethernet = new Interface[8];
		for(int i = 0; i < 8; i++) ethernet[i] = new Interface(i, buffer, hosts);
		CAM = new MACTable(100, CAMsize);
	}
	
	public Interface[] ethernet;
	public MACTable CAM;
	
	public void initializeRemainingInterfaces()
	{
		for(int i = 0; i < 8; i++)
		{
			if(ethernet[i].getState() == State.INIT) ethernet[i].setState(State.DOWN);
		}
	}
	
	public void receive()
	{
		for(int i = 0; i < 8; i++)
		{
			if(this.ethernet[i].getState() != State.UP) continue;
			if(this.ethernet[i].Rx.isEmpty()) continue;
			//do things
		}
	}
	public void transmit()
	{
		for(int i = 0; i < 8; i++)
		{
			if(this.ethernet[i].getState() != State.UP) continue;
			if(this.ethernet[i].Tx.isEmpty()) continue;
			//do things
		}
	}
	
	public String listInterfaces()
	{
		String str = "";
		for(int i = 0; i < 8; i++)
		{
			str += "ethernet0/0/";
			str += i;
			str += " LINK ";
			State s = ethernet[i].getState();
			if(s == s.INIT) str += "INIT";
			else if(s == s.UP) str += "UP";
			else if(s == s.DOWN) str += "DOWN";
			else str += "UNKNOWN STATE";
			str += "\n";
		}
		return str;
	}
	
	public Integer getNumberOfActiveInterfaces()
	{
		Integer x = 0;
		for(Integer i = 0; i < 8; i++) if(ethernet[i].getState() == State.UP) x++;
		return x;
	}
}
