package RNG;

public class Normal extends Random{
	public Normal()
	{
	}
	
	public double getNext() {
		double x = 0;
		Uniform uniform = new Uniform();
		for(Integer i = 0; i < 6; i++)
		{
			x += uniform.getNext();
		}
		return x / 6;
	}
	public double getNext(Integer n) {
		double x = 0;
		Uniform uniform = new Uniform();
		for(Integer i = 0; i < n; i++)
		{
			x += uniform.getNext();
		}
		return x / n;
	}
}
