package buffer;

import traffic.Frame;

public class TxBuffer extends Buffer {

	public TxBuffer(int size) {
		super(size);
		transmitting = new int[size];
		this.Idle = -1;
		this.IdleSwitch = -1;
		this.head = 0;
		this.tail = 0;
	}
	int transmitting[];
	public int Idle;
	public int IdleSwitch;
	private int head;
    private int tail;
	
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
		buffer[tail] = f;
		transmitting[tail] = f.getLength();
        tail = (tail + 1) % maxSize;  // Move tail forward circularly
		currentSize++;
		return true;
	}
	public Frame pop()
	{
		assert this.currentSize > 0;
		Frame f = buffer[head];
		head = (head + 1) % maxSize;  // Move head forward circularly
        currentSize--;
		return f;
	}
}
