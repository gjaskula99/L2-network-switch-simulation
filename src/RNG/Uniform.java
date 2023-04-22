package RNG;

public class Uniform extends Random{
	public Uniform()
	{
	}
	
	public double getNext()
	{
		return Math.random();
	}
}
