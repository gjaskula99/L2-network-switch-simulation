package buffer;

import traffic.Frame;

public class Buffer {
	public Buffer(int size)
	{
		this.buffer = new Frame[size];
		this.maxSize = size;
		this.currentSize = 0;
		this.head = 0;
		this.tail = 0;
	}
	
	public Frame buffer[];
	int maxSize;
	int currentSize;
	private int head;
    private int tail;
	
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
	public boolean push(Frame f) {
        if (this.isFull()) return false;
        buffer[tail] = f;
        tail = (tail + 1) % maxSize;  // Move tail forward circularly
        currentSize++;
        return true;
    }

    public Frame pop() {
        if (this.isEmpty()) throw new IllegalStateException("Buffer is empty");
        Frame f = buffer[head];
        head = (head + 1) % maxSize;  // Move head forward circularly
        currentSize--;
        return f;
    }

    public void clear() {
        head = 0;
        tail = 0;
        currentSize = 0;
    }
	
	public String getString()
	{
		if(this.isEmpty()) return "BUFFER EMPTY";
		String str = "";
		for(int i = 0; i < this.currentSize; i++)str += this.buffer[i].getString();
		return str;
	}
}
