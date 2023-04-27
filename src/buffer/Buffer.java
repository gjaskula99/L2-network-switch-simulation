package buffer;

import traffic.Frame;

public class Buffer {
	public Buffer(int size)
	{
		this.buffer = new Frame[size];
		this.maxSize = size;
		this.currentSize = 0;
	}
	
	public Frame buffer[];
	int maxSize;
	int currentSize;
	
	public boolean isFull()
	{
		if(this.currentSize == this.maxSize) return true;
		return false;
	}
	public boolean isEmpty()
	{
		if(this.currentSize == 0) return true;
		return false;
	}
	public int getSize()
	{
		return this.maxSize;
	}
	public int getCurrentSize()
	{
		return this.currentSize;
	}
	public boolean push(Frame f)
	{
		if(this.isFull()) return false;
		buffer[this.currentSize] = f;
		currentSize++;
		return true;
	}
	public Frame pop()
	{
		assert this.currentSize > 0;
		currentSize--;
		Frame f = buffer[0];
		for(int i = currentSize; i >= 1; i--)
		{
			buffer[i - 1] = buffer[i];
		}
		return f;
	}
	
	public void clear()
	{
		for(int i = 0; i < currentSize; i++) this.pop();
	}
	
	public String getString()
	{
		if(this.isEmpty()) return "BUFFER EMPTY";
		String str = "";
		for(int i = 0; i < this.currentSize; i++)str += this.buffer[i].getString();
		return str;
	}
}
