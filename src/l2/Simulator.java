package l2;

public class Simulator {
	Simulator()
	{
		Interface FE0 = new Interface(0, 4);
		FE0.Rx.push(new traffic.Frame(1, 2, 1, 3, 7));
		FE0.Rx.push(new traffic.Frame(64, 2, 1, 3, 7));
		FE0.Rx.push(new traffic.Frame(0, 2, 1, 3, 7));
		FE0.Rx.push(new traffic.Frame(0, 6, 9));
		FE0.Rx.push(new traffic.Frame(0, 6, 9));
		System.out.print(FE0.Rx.getString());
	}
	
	public static void main(String[] args)
	{
		new Simulator();
	}
}
