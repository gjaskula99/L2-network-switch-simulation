package buffer;

import traffic.Frame;

public class TxBuffer extends Buffer {

	public TxBuffer(int size) {
		super(size);
		transmitting = new int[size];
		this.Idle = -1;
		this.IdleSwitch = -1;
	}
	int transmitting[];
	public int Idle;
	public int IdleSwitch;
	
	public int getStatus(int index)
	{
		return this.transmitting[index];
	}
	public void setStatus(int index, int newStatus)
	{
		if(newStatus < this.buffer[index].getLength())
		{
			this.transmitting[index] = newStatus;
		}
	}
	public boolean updateStatus(int index)
	{
		this.transmitting[index]--;
		if(transmitting[index] == 0) return true;
		return false;
	}
	public boolean push(Frame f)
	{
		if(this.isFull()) return false;
		buffer[this.currentSize] = f;
		transmitting[this.currentSize] = f.getLength();
		currentSize++;
		return true;
	}
	public Frame pop()
	{
		assert this.currentSize > 0;
		currentSize--;
		Frame f = buffer[this.currentSize];
		this.transmitting[this.currentSize] = 0;
		for(int i = currentSize; i >= 1; i--)
		{
			buffer[i - 1] = buffer[i];
		}
		return f;
	}
}
