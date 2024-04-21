package buffer;

import traffic.Frame;

public class RxBuffer extends Buffer {

	public RxBuffer(int size) {
		super(size);
		this.receiving = new int[size];
		this.Idle = 0;
		this.head = 0;
		this.tail = 0;
	}
	int receiving[];
	public int Idle; //Time to next frame
	private int head;
    private int tail;
	
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
		buffer[tail] = f;
		receiving[tail] = f.getLength();
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
