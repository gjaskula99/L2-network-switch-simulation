package l2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import l2.Interface.State;

public class Simulator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//Global variables
	static Simulator main;
	Integer time = -1;
	Boolean isRunning = false;
	Thread thread;
	static final Integer BUFFERSIZE = 10;
	
	//GUI
	JFrame window = new JFrame();
	JLabel status = new JLabel();
	
	JButton buttonRun = new JButton("RUN");
	JButton buttonStop = new JButton("HALT");
	JLabel iterationsTxt = new JLabel();
	JTextField iterations = new JTextField();
	JProgressBar progressBar = new JProgressBar();
	
	JCheckBox ethernet0 = new JCheckBox();
	JCheckBox ethernet1 = new JCheckBox();
	JCheckBox ethernet2 = new JCheckBox();
	JCheckBox ethernet3 = new JCheckBox();
	JCheckBox ethernet4 = new JCheckBox();
	JCheckBox ethernet5 = new JCheckBox();
	JCheckBox ethernet6 = new JCheckBox();
	JCheckBox ethernet7 = new JCheckBox();
	JTextField bufferSize = new JTextField();
	JLabel bufferSizeTxt = new JLabel();
	ButtonGroup switchingMode = new ButtonGroup();
	JLabel switchingModeTxt = new JLabel();
	JRadioButton switchingMode_SF = new JRadioButton();
	JRadioButton switchingMode_CT = new JRadioButton();
	ButtonGroup frameLength = new ButtonGroup();
	JLabel frameLengthTxt = new JLabel();
	JRadioButton frameLengthFixed = new JRadioButton();
	JRadioButton frameLengthVary = new JRadioButton();
	
	//Logic
	Switch mySwitch = new Switch(BUFFERSIZE);
	
	//Constructior
	Simulator()
	{
		//Setup GUI
		window.setBounds(EXIT_ON_CLOSE, ABORT, 1200, 800);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setTitle("L2 network switch simulator");
		window.setLayout(null);
		
		status.setBounds(20, 20, 980, 40);
		status.setFont(new Font("Serif", Font.PLAIN, 24));;
		setStatus("Ready", false);
		
		iterationsTxt.setBounds(1050, 30, 140, 30);
		iterationsTxt.setText("Run for seconds:");
		iterations.setBounds(1050, 60, 100, 30);
		iterations.setText(Integer.toString(0));
		buttonRun.setBounds(1050, 90, 100, 30);
		buttonRun.addActionListener(this);
		buttonStop.setBounds(1050, 120, 100, 30);
		buttonStop.setForeground(Color.RED);
		buttonStop.setEnabled(false);
		buttonStop.addActionListener(this);
		
		progressBar.setBounds(1000, 10, 180, 20);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100000000);
		progressBar.setForeground(Color.GREEN);
		progressBar.setValue(0);
		progressBar.setVisible(true);
		
		//Switch settings
		ethernet0.setText("eth0");
		ethernet0.setBounds(100, 200, 90, 30);
		ethernet0.addActionListener(this);
		ethernet1.setText("eth1");
		ethernet1.setBounds(200, 200, 90, 30);
		ethernet1.addActionListener(this);
		ethernet2.setText("eth2");
		ethernet2.setBounds(300, 200, 90, 30);
		ethernet2.addActionListener(this);
		ethernet3.setText("eth3");
		ethernet3.setBounds(400, 200, 90, 30);
		ethernet3.addActionListener(this);
		ethernet4.setText("eth4");
		ethernet4.setBounds(500, 200, 90, 30);
		ethernet4.addActionListener(this);
		ethernet5.setText("eth5");
		ethernet5.setBounds(600, 200, 90, 30);
		ethernet5.addActionListener(this);
		ethernet6.setText("eth6");
		ethernet6.setBounds(700, 200, 90, 30);
		ethernet6.addActionListener(this);
		ethernet7.setText("eth7");
		ethernet7.setBounds(800, 200, 90, 30);
		ethernet7.addActionListener(this);
		
		bufferSize.setBounds(1000, 250, 100, 40);
		bufferSize.setText(Integer.toString(BUFFERSIZE));
		bufferSize.setEnabled(false);
		bufferSizeTxt.setBounds(900, 250, 90, 40);
		bufferSizeTxt.setText("Buffer size:");
		
		switchingMode.add(switchingMode_SF);
		switchingMode.add(switchingMode_CT);
		switchingModeTxt.setBounds(900, 300, 200, 20);
		switchingModeTxt.setText("Switching mode:");
		switchingMode_SF.setBounds(900, 330, 200, 20);
		switchingMode_SF.setText("Store and forward");
		switchingMode_SF.setSelected(true);
		switchingMode_SF.setEnabled(false); //TODO
		switchingMode_CT.setBounds(900, 350, 200, 20);
		switchingMode_CT.setText("Cut through");
		switchingMode_CT.setEnabled(false); //TODO
		
		frameLength.add(frameLengthFixed);
		frameLength.add(frameLengthVary);
		frameLengthTxt.setBounds(900, 400, 200, 20);
		frameLengthTxt.setText("Frame length:");
		frameLengthFixed.setBounds(900, 430, 200, 20);
		frameLengthFixed.setText("Fixed 64B");
		frameLengthFixed.setSelected(true);
		frameLengthFixed.setEnabled(false); //TODO
		frameLengthVary.setBounds(900, 450, 200, 20);;
		frameLengthVary.setText("64B - 1536B");
		frameLengthVary.setEnabled(false); //TODO
		
		//Add to JPanel
		window.add(buttonRun);
		window.add(buttonStop);
		window.add(iterationsTxt);
		window.add(iterations);
		window.add(status);
		window.add(progressBar);
		
		window.add(ethernet0);
		window.add(ethernet1);
		window.add(ethernet2);
		window.add(ethernet3);
		window.add(ethernet4);
		window.add(ethernet5);
		window.add(ethernet6);
		window.add(ethernet7);
		window.add(bufferSize);
		window.add(bufferSizeTxt);
		window.add(switchingModeTxt);
		window.add(switchingMode_SF);
		window.add(switchingMode_CT);
		window.add(frameLengthTxt);
		window.add(frameLengthFixed);
		window.add(frameLengthVary);
		
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
			setStatus("Switching unset interfaces to DOWN", false);
			mySwitch.initializeRemainingInterfaces();
			setStatus("Running", false);
			disableGUI();
			
			time =  Integer.valueOf( iterations.getText() );
			progressBar.setValue(0);
			progressBar.setMaximum(time);
			LocalDateTime then = LocalDateTime.now();
			while (true)
			{
				if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= time) break;
				//progressBar.setValue( progressBar.getValue()+1 );
				//if(progressBar.getValue() >= 100000000) progressBar.setValue(0);
				progressBar.setValue((int) ChronoUnit.SECONDS.between(then, LocalDateTime.now()));
				//
			}
			isRunning = false;
			progressBar.setValue(100);
			enableGUI();
			
			System.out.print(mySwitch.listInterfaces());
			//System.out.print(mySwitch.ethernet[2].Rx.getString());
			System.out.print(mySwitch.CAM.listTable());
			setStatus("Completed", false);
			
			this.interrupt();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == buttonRun)
		{
			isRunning = !isRunning;
			
			//Data validation
			double d;
			try {
		        d = Double.parseDouble( String.valueOf(iterations.getText()) );
		    } 
			catch (NumberFormatException nfe) {
				setStatus("Invalid input - not a number", true);
		        return;
		    }
			if( d <= 0)
			{
				setStatus("Invalid input - must be greater than 0", true);
				return;
			}
			
			//Start simulator
			Task progress = new Task();
			thread = new Thread(progress);
			thread.start();
		}
		if(e.getSource() == buttonStop)
		{
			setStatus("Simulation HALTED!", false);
			thread.stop(); //UNSAFE
			enableGUI();
		}
		if(e.getSource() == ethernet0)
		{
			if(ethernet0.isSelected()) mySwitch.ethernet[0].setState(State.UP);
			else mySwitch.ethernet[0].setState(State.DOWN);
		}
		if(e.getSource() == ethernet1)
		{
			if(ethernet1.isSelected()) mySwitch.ethernet[1].setState(State.UP);
			else mySwitch.ethernet[1].setState(State.DOWN);
		}
		if(e.getSource() == ethernet2)
		{
			if(ethernet2.isSelected()) mySwitch.ethernet[2].setState(State.UP);
			else mySwitch.ethernet[2].setState(State.DOWN);
		}
		if(e.getSource() == ethernet3)
		{
			if(ethernet3.isSelected()) mySwitch.ethernet[3].setState(State.UP);
			else mySwitch.ethernet[3].setState(State.DOWN);
		}
		if(e.getSource() == ethernet4)
		{
			if(ethernet4.isSelected()) mySwitch.ethernet[4].setState(State.UP);
			else mySwitch.ethernet[4].setState(State.DOWN);
		}
		if(e.getSource() == ethernet5)
		{
			if(ethernet5.isSelected()) mySwitch.ethernet[5].setState(State.UP);
			else mySwitch.ethernet[5].setState(State.DOWN);
		}
		if(e.getSource() == ethernet6)
		{
			if(ethernet6.isSelected()) mySwitch.ethernet[6].setState(State.UP);
			else mySwitch.ethernet[6].setState(State.DOWN);
		}
		if(e.getSource() == ethernet7)
		{
			if(ethernet7.isSelected()) mySwitch.ethernet[7].setState(State.UP);
			else mySwitch.ethernet[7].setState(State.DOWN);
		}
	}
	
	
	public void refreshGUI()
	{
		//
	}
	
	public void enableGUI() //After simulation
	{
		buttonRun.setEnabled(true);
		buttonStop.setEnabled(false);
		ethernet0.setEnabled(true);
		ethernet1.setEnabled(true);
		ethernet2.setEnabled(true);
		ethernet3.setEnabled(true);
		ethernet4.setEnabled(true);
		ethernet5.setEnabled(true);
		ethernet6.setEnabled(true);
		ethernet7.setEnabled(true);
	}
	
	public void disableGUI() //Before simulation
	{
		buttonRun.setEnabled(false);
		buttonStop.setEnabled(true);
		ethernet0.setEnabled(false);
		ethernet1.setEnabled(false);
		ethernet2.setEnabled(false);
		ethernet3.setEnabled(false);
		ethernet4.setEnabled(false);
		ethernet5.setEnabled(false);
		ethernet6.setEnabled(false);
		ethernet7.setEnabled(false);
	}
	
	public void setStatus(String msg, Boolean isError)
	{
		if(isError) status.setForeground(Color.RED);
		else status.setForeground(Color.BLACK);
		status.setText("Status: " + msg);
		System.out.println(status.getText());
	}
}
