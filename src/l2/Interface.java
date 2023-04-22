package l2;

public class Interface {
	public Interface(int ID, int bufferSize)
	{
		this.Rx = new buffer.RxBuffer(bufferSize);
		this.Tx = new buffer.TxBuffer(bufferSize);
		this.interfaceID = ID;
		this.numberofHosts = 1;
		this.interfaceState = State.INIT;
	}
	public Interface(int ID, int bufferSize, int hosts)
	{
		this.Rx = new buffer.RxBuffer(bufferSize);
		this.Tx = new buffer.TxBuffer(bufferSize);
		this.interfaceID = ID;
		this.numberofHosts = hosts;
		this.interfaceState = State.INIT;
	}
	
	public buffer.RxBuffer Rx;
	public buffer.TxBuffer Tx;
	int interfaceID;
	int numberofHosts;
	enum State {INIT, UP, DOWN};
	State interfaceState;
	
	public int getID()
	{
		return this.interfaceID;
	}
	public int getHosts()
	{
		return this.numberofHosts;
	}
	public State getState()
	{
		return this.interfaceState;
	}
	public void setState(State s)
	{
		this.interfaceState = s;
		if(this.interfaceState == State.UP) System.out.println("Interface " + this.interfaceID + " changed status to UP");
		else System.out.println("Interface " + this.interfaceID + " changed status to DOWN");
	}
	public Boolean isDown()
	{
		if(this.interfaceState == State.DOWN) return true;
		return false;
	}
	public Boolean isUp()
	{
		if(this.interfaceState == State.UP) return true;
		return false;
	}
}
