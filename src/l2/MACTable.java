package l2;

import java.util.Vector;

public class MACTable {
	public MACTable(int valid, int size)
	{
		this.defaultValidity = valid;
		this.maxSize = size;
	}
	
	Vector<MAC> address = new Vector<MAC>();
	Vector<Integer> interf = new Vector<Integer>();
	Vector<Integer> validFor = new Vector<Integer>();
	int defaultValidity;
	int maxSize;
	
	public boolean exists(MAC addr)
	{
		if(address.contains(addr)) return true;
		return false;
	}
	public int getInterfaceByMAC(MAC addr)
	{
		int index = address.indexOf(addr);
		if(index == -1) return -1;
		return interf.get(index);
	}
	public boolean push(MAC addr, int inter)
	{
		if(this.address.size() >= this.maxSize) return false;
		this.address.add(addr);
		this.interf.add(inter);
		this.validFor.add(this.defaultValidity);
		return true;
	}
	public void validate()
	{
		for(int i = 0; i < this.address.size(); i++)
		{
			if(this.validFor.get(i) == 0)
			{
				this.address.remove(i);
				this.interf.remove(i);
				this.validFor.remove(i);
			}
		}
	}
	public void pop()
	{
		this.validate();
	}
	public void decrement()
	{
		for(int i = 0; i < this.validFor.size(); i++) this.validFor.set(i, this.validFor.get(i) - 1);
	}
	public String listTable()
	{
		String str = "";
		for(int i = 0; i < this.address.size(); i++)
		{
			str += this.address.get(i).getString();
			str += " ethernet0/0/";
			str += this.interf.get(i);
			str += " valid for ";
			str += this.validFor.get(i);
		}
		return str;
	}
}
