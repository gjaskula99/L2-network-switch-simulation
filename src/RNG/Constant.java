package RNG;

public class Constant extends Random{
	public Constant(double Mean)
	{
		this.mean = Mean;
	}
	double mean;
	
	public double getNext()
	{
		return this.mean;
	}
}
