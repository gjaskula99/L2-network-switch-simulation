package buffer;

import traffic.Frame;

public class TxBuffer extends Buffer {

	public TxBuffer(int size) {
		super(size);
		transmitting = new int[size];
	}
	int transmitting[];
	
	public int getStatus(int index)
	{
		return this.transmitting[index];
	}
	public void setStatus(int index, int newStatus)
	{
		this.transmitting[index] = newStatus;
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
