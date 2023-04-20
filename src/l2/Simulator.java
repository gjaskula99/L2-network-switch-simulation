package l2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import l2.Interface.State;

public class Simulator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//Global variables
	static Simulator main;
	Integer time = -1;
	Boolean isRunning = false;
	Thread thread;
	
	//GUI
	JFrame window = new JFrame();
	JButton buttonRun = new JButton("RUN");
	JLabel iterationsTxt = new JLabel();
	JTextField iterations = new JTextField();
	JProgressBar progressBar = new JProgressBar();
	
	
	//Logic
	Switch mySwitch = new Switch(10);
	
	//Constructior
	Simulator()
	{
		//Setup GUI
		window.setBounds(EXIT_ON_CLOSE, ABORT, 1200, 800);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setTitle("L2 network switch simulator");
		window.setLayout(null);
		
		iterationsTxt.setBounds(1050, 30, 100, 30);
		iterationsTxt.setText("Run for seconds:");
		iterations.setBounds(1050, 60, 100, 30);
		iterations.setText(Integer.toString(0));
		buttonRun.setBounds(1050, 90, 100, 30);
		buttonRun.addActionListener(this);
		
		progressBar.setBounds(1000, 10, 180, 20);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100000000);
		progressBar.setForeground(Color.GREEN);
		progressBar.setValue(0);
		progressBar.setVisible(true);
		
		window.add(buttonRun);
		window.add(iterationsTxt);
		window.add(iterations);
		
		window.add(progressBar);
		
		window.setVisible(true);
		
		/*mySwitch.ethernet[2].setState(State.UP);
		mySwitch.initializeRemainingInterfaces();
		mySwitch.ethernet[2].Rx.push(new traffic.Frame(69, 2, 1, 3, 7));
		mySwitch.ethernet[2].Rx.push(new traffic.Frame(0, 2, 1));
		mySwitch.CAM.push(new l2.MAC(2, 3), 2);*/
	}
	
	public static void main(String[] args)
	{
		new Simulator();
	}
	
	//Actual simulation
	public class Task extends Thread
	{
		Task()
		{	
		}
		
		public void run()
		{
			time =  Integer.valueOf( iterations.getText() );
			progressBar.setValue(0);
			LocalDateTime then = LocalDateTime.now();
			while (true)
			{
				if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= time) break;
				progressBar.setValue( progressBar.getValue()+1 );
				if(progressBar.getValue() >= 100000000) progressBar.setValue(0);
				//
			}
			isRunning = false;
			progressBar.setValue(100);
			buttonRun.setEnabled(true);
			
			System.out.print(mySwitch.listInterfaces());
			//System.out.print(mySwitch.ethernet[2].Rx.getString());
			System.out.print(mySwitch.CAM.listTable());
			this.interrupt();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == buttonRun)
		{
			isRunning = !isRunning;
			buttonRun.setEnabled(false);
		}
		Task progress = new Task();
		thread = new Thread(progress);
		thread.start();
	}
	
	
	public void refreshGUI()
	{
		//
	}
}
