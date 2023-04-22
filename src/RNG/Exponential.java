package RNG;

public class Exponential extends Random{
	public Exponential(double Lambda)
	{
		this.lambda = Lambda;
	}
	double lambda;
	
	public double getNext()
	{
		double r = Math.random();
		return Math.pow(-lambda, -1) * Math.log(r);
	}
}

