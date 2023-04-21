package l2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
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
	static final Integer PORTNUMBER = 8;
	
	//GUI
	JFrame window = new JFrame();
	JLabel status = new JLabel();
	
	JButton buttonRun = new JButton("RUN");
	JButton buttonStop = new JButton("HALT");
	JLabel iterationsTxt = new JLabel();
	JTextField iterations = new JTextField();
	JProgressBar progressBar = new JProgressBar();
	
	JCheckBox[] ethernet = new JCheckBox[8];
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
	
	ImageIcon switchOFF = new ImageIcon("res/switchOFF.png");
	ImageIcon switchON = new ImageIcon("res/switchON.png");
	ImageIcon portOFF = new ImageIcon("res/portOFF.png");
	ImageIcon portON = new ImageIcon("res/portON.png");
	ImageIcon portTRX = new ImageIcon("res/portTRX.png");
	JLabel picSwitch = new JLabel(switchOFF);
	JLabel[] picPort = new JLabel[PORTNUMBER];
	
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
		
		status.setBounds(20, 10, 980, 40);
		status.setFont(new Font("Serif", Font.PLAIN, 24));;
		setStatus("Creating GUI", false);
		
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
		for(Integer i = 0; i < PORTNUMBER; i++)
		{
			ethernet[i] = new JCheckBox();
			ethernet[i].setText("eth" + i);
			ethernet[i].setBounds(100 + (100*i), 200, 90, 30);
			ethernet[i].addActionListener(this);
			window.add(ethernet[i]);
		}
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
		
		picSwitch.setBounds(53, 50, 947, 150);
		for(Integer i = 0; i < PORTNUMBER; i++)
		{
			picPort[i] = new JLabel();
			picPort[i].setBounds(250+(54*i), 120, 54, 47);
			picPort[i].setIcon(portOFF);
			window.add(picPort[i]);
		}
		//Add to JPanel
		window.add(buttonRun);
		window.add(buttonStop);
		window.add(iterationsTxt);
		window.add(iterations);
		window.add(status);
		window.add(progressBar);
		
		window.add(bufferSize);
		window.add(bufferSizeTxt);
		window.add(switchingModeTxt);
		window.add(switchingMode_SF);
		window.add(switchingMode_CT);
		window.add(frameLengthTxt);
		window.add(frameLengthFixed);
		window.add(frameLengthVary);
		
		window.add(picSwitch);
		
		window.setVisible(true);
		setStatus("Ready", false);
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
			picSwitch.setIcon(switchON);
			
			
			time =  Integer.valueOf( iterations.getText() );
			progressBar.setValue(0);
			progressBar.setMaximum(time);
			LocalDateTime then = LocalDateTime.now();
			while (true)
			{
				if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= time) break;
				progressBar.setValue((int) ChronoUnit.SECONDS.between(then, LocalDateTime.now()));
				//ACTUAL SWITCH
				for(Integer i = 0; i < PORTNUMBER; i++) //RX
				{
					
				}
				for(Integer i = 0; i < PORTNUMBER; i++) //SWITCHING
				{
					
				}
				for(Integer i = 0; i < PORTNUMBER; i++) //TX
				{
					
				}
			}
			isRunning = false;
			progressBar.setValue( progressBar.getMaximum() );
			enableGUI();
			
			System.out.print(mySwitch.listInterfaces());
			//System.out.print(mySwitch.ethernet[2].Rx.getString());
			System.out.print(mySwitch.CAM.listTable());
			setStatus("Completed", false);
			picSwitch.setIcon(switchOFF);
			
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
			double d; //Iterations
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
		for(Integer i = 0; i < PORTNUMBER; i++) {
			if(e.getSource() == ethernet[i])
			{
				if(ethernet[i].isSelected())
					{
					mySwitch.ethernet[i].setState(State.UP);
					picPort[i].setIcon(portON);
					}
				else {
					mySwitch.ethernet[i].setState(State.DOWN);
					picPort[i].setIcon(portOFF);
				}
			}
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
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(true);
	}
	
	public void disableGUI() //Before simulation
	{
		buttonRun.setEnabled(false);
		buttonStop.setEnabled(true);
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(false);
	}
	
	public void setStatus(String msg, Boolean isError)
	{
		if(isError) status.setForeground(Color.RED);
		else status.setForeground(Color.BLACK);
		status.setText("Status: " + msg);
		System.out.println(status.getText());
	}
}
