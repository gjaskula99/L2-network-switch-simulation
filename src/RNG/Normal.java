package RNG;

public class Normal extends Random{
	double mean;
	double deviation;
	public Normal()
	{
		mean = 0.0;
		deviation = 0.0;
	}
	public Normal(double m, double d)
	{
		mean = m;
		deviation = d;
	}
	
	public double getNext() {
		double x = 0;
		for(Integer i = 0; i < 6; i++)
		{
			x += Math.random();
		}
		return ((x / 6)-0.5)*10*deviation + mean;
	}
	public double getNext(Integer n) {
		double x = 0;
		for(Integer i = 0; i < n; i++)
		{
			x += Math.random();
		}
		return ((x / n)-0.5)*10*deviation + mean;
	}
}
