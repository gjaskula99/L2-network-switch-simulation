package RNG;

public class Uniform extends Random{
	public Uniform()
	{
	}
	
	public double getNext()
	{
		return Math.random();
	}
	public int getNextInt(int min, int max)
	{
	    return (int) ((Math.random() * (max - min)) + min);
	}
}
