package RNG;

public class Uniform extends Random{
	public Uniform()
	{
	}
	
	public double getNext()
	{
		return Math.random();
	}
	public int getNextInt(int Min, int Max)
	{
		return Min + (int)(Math.random() * ((Max - Min) + 1));
	}
}
