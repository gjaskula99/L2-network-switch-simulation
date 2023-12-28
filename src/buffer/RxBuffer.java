package buffer;

import traffic.Frame;

public class RxBuffer extends Buffer {

	public RxBuffer(int size) {
		super(size);
		this.receiving = new int[size];
		this.Idle = 0;
	}
	int receiving[];
	public int Idle; //Time to next frame
	
	public int getStatus(int index)
	{
		return this.receiving[index];
	}
	public void setStatus(int index, int newStatus)
	{
		this.receiving[index] = newStatus;
	}
	public boolean updateStatus(int index)
	{
		this.receiving[index]--;
		if(receiving[index] == 0) return true;
		return false;
	}
	public boolean push(Frame f)
	{
		if(this.isFull()) return false;
		buffer[this.currentSize] = f;
		receiving[this.currentSize] = f.getLength();
		currentSize++;
		return true;
	}
	public Frame pop()
	{
		assert this.currentSize > 0;
		currentSize--;
		Frame f = buffer[0];
		this.receiving[0] = 0;
		for(int i = 0; i < currentSize; i++)
		{
			buffer[i] = buffer[i + 1];
		}
		return f;
	}
}
