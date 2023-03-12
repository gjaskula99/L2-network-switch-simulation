package l2;

import l2.Interface.State;

public class Simulator {
	Simulator()
	{
		Switch mySwitch = new Switch(10);
		mySwitch.ethernet[2].setState(State.UP);
		mySwitch.initializeRemainingInterfaces();
		mySwitch.ethernet[2].Rx.push(new traffic.Frame(69, 2, 1, 3, 7));
		mySwitch.ethernet[2].Rx.push(new traffic.Frame(0, 2, 1));
		mySwitch.CAM.push(new l2.MAC(2, 3), 2);
		
		System.out.print(mySwitch.listInterfaces());
		System.out.print(mySwitch.ethernet[2].Rx.getString());
		System.out.print(mySwitch.CAM.listTable());
	}
	
	public static void main(String[] args)
	{
		new Simulator();
	}
}
