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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import l2.Interface.State;
import traffic.Frame;
import RNG.Random;
import RNG.Uniform;
import RNG.Exponential;
import RNG.Normal;

public class Simulator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//Global variables
	static Simulator main;
	Integer time = -1;
	Boolean isRunning = false;
	Thread thread;
	static final Integer BUFFERSIZE = 10;
	static final Integer PORTNUMBER = 8;
	
	enum RNGTYPE {UNIFORM, EXP, NORMAL};
	RNGTYPE rngSelected = RNGTYPE.EXP;
	Exponential handlingRng = new Exponential(0.001);
	Uniform lengthRng = new Uniform();
	
	double TotalLost = 0.0;
	double DataInVal = 0.0;
	double DataOutVal = 0.0;
	Integer handlingMode = 0;
	Boolean frameFixedSize = true;
	
	//GUI
	JFrame window = new JFrame();
	JLabel status = new JLabel();
	JLabel copy = new JLabel();
	
	JButton buttonRun = new JButton("RUN");
	JButton buttonStop = new JButton("HALT");
	JButton buttonClr = new JButton("Clear stats");
	JLabel iterationsTxt = new JLabel();
	JTextField iterations = new JTextField();
	String[] rngTypeString = {"Uniform", "Exponential", "Normal"};
	JComboBox<String> rngType = new JComboBox<String>(rngTypeString);
	JLabel rngTypeTxt = new JLabel();
	String[] handlingString = {"Instant", "10% of frame length", "25% of frame length", "50% of frame length" , "100% of frame length" , "Exponential"};
	JComboBox<String> handling = new JComboBox<String>(handlingString);
	JLabel handlingTxt = new JLabel();
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
	JTextField frameMinDelay = new JTextField();
	JLabel frameMinDelayTxt = new JLabel();
	
	ImageIcon switchOFF = new ImageIcon("res/switchOFF.png");
	ImageIcon switchON = new ImageIcon("res/switchON.png");
	ImageIcon portOFF = new ImageIcon("res/portOFF.png");
	ImageIcon portON = new ImageIcon("res/portON.png");
	ImageIcon portTRX = new ImageIcon("res/portTRX.png");
	JLabel picSwitch = new JLabel(switchOFF);
	JLabel[] picPort = new JLabel[PORTNUMBER];
	
	JLabel[] packetsRx = new JLabel[PORTNUMBER];
	JLabel[] packetsTx = new JLabel[PORTNUMBER];
	JLabel[] packetsLst = new JLabel[PORTNUMBER];
	JLabel[] packetsBrd = new JLabel[PORTNUMBER];
	JLabel packetsRxTxt = new JLabel("Rx:");
	JLabel packetsTxTxt = new JLabel("Tx:");
	JLabel packetsLstTxt = new JLabel("Lost:");
	JLabel packetsBrdTxt = new JLabel("Broadcast:");
	JLabel packetsLstTotal = new JLabel("Total lost: 0.0%");
	JLabel dataInbound = new JLabel("Data in: 0 Mb");
	JLabel dataServed = new JLabel("Data served: 0 Mb");
	
	JTextArea CAM = new JTextArea();
	JScrollPane CAMScroll = new JScrollPane(CAM);
	JLabel CAMTxt = new JLabel();
	JButton buttonFlushCAM = new JButton();
	JButton buttonClearBuffers = new JButton();
	JTextArea Buffer = new JTextArea();
	JScrollPane BufferScroll = new JScrollPane(Buffer);
	JLabel BufferTxt = new JLabel();
	Integer[] InterfaceStrings = {0, 1, 2, 3, 4, 5, 6, 7};
	JComboBox<Integer> BufferSelect = new JComboBox<Integer>(InterfaceStrings);
	
	//Logic
	Switch mySwitch = new Switch(BUFFERSIZE);
	Random[] interfaceRNG = new Random[PORTNUMBER];
	Uniform targetPortRNG = new Uniform();
	
	//Constructior
	Simulator()
	{
		//Setup GUI
		setStatus("Creating GUI", false);
		
		window.setBounds(EXIT_ON_CLOSE, ABORT, 1200, 800);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setTitle("L2 network switch simulator");
		window.setLayout(null);
		
		status.setBounds(20, 10, 980, 40);
		status.setFont(new Font("Serif", Font.PLAIN, 24));;
		copy.setBounds(5, 740, 1180, 30);
		copy.setFont(new Font("Serif", Font.PLAIN, 10));
		copy.setText("©Grzegorz Jaskuła 2023, supervised by prof. Maciej Stasiak, Poznan University of Technology. This software is for educational purposes only and is provided as it is.");
		
		iterationsTxt.setBounds(1050, 30, 140, 30);
		iterationsTxt.setText("Run for seconds");
		iterations.setBounds(1050, 60, 100, 30);
		iterations.setText(Integer.toString(0));
		buttonRun.setBounds(1050, 90, 100, 30);
		buttonRun.addActionListener(this);
		buttonStop.setBounds(1050, 120, 100, 30);
		buttonStop.setForeground(Color.RED);
		buttonStop.setEnabled(false);
		buttonStop.addActionListener(this);
		buttonClr.setBounds(30, 500, 170, 30);
		buttonClr.setEnabled(true);
		buttonClr.addActionListener(this);
		
		rngType.setBounds(1020, 170, 170, 20);
		rngType.setSelectedIndex(1);
		rngType.addActionListener(this);
		rngTypeTxt.setBounds(1020, 150, 170, 20);
		rngTypeTxt.setText("Traffic generator type");
		
		handling.setBounds(1020, 210, 170, 20);
		handling.setSelectedIndex(0);
		handling.addActionListener(this);
		handlingTxt.setBounds(1020, 190, 170, 20);
		handlingTxt.setText("Frame handle time");
		
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
			
			//Stats
			packetsRx[i] = new JLabel("0");
			packetsTx[i] = new JLabel("0");
			packetsLst[i] = new JLabel("0");
			packetsBrd[i] = new JLabel("0");
			packetsRx[i].setBounds(110 + (100*i), 250, 60, 30);
			packetsTx[i].setBounds(110 + (100*i), 280, 60, 30);
			packetsLst[i].setBounds(110 + (100*i), 310, 60, 30);
			packetsBrd[i].setBounds(110 + (100*i), 340, 60, 30);
			
			window.add(packetsRx[i]);
			window.add(packetsTx[i]);
			window.add(packetsLst[i]);
			window.add(packetsBrd[i]);
		}
		packetsRxTxt.setBounds(20, 250, 80, 30);
		packetsTxTxt.setBounds(20, 280, 80, 30);
		packetsLstTxt.setBounds(20, 310, 80, 30);
		packetsBrdTxt.setBounds(20, 340, 80, 30);
		packetsLstTotal.setBounds(30, 380, 500, 50);
		dataInbound.setBounds(30, 420, 500, 30);
		dataServed.setBounds(30, 450, 500, 30);
		packetsRxTxt.setFont(new Font("Serif", Font.BOLD, 16));
		packetsTxTxt.setFont(new Font("Serif", Font.BOLD, 16));
		packetsLstTxt.setFont(new Font("Serif", Font.BOLD, 16));
		packetsBrdTxt.setFont(new Font("Serif", Font.BOLD, 16));
		packetsLstTotal.setFont(new Font("Serif", Font.BOLD, 20));
		dataInbound.setFont(new Font("Serif", Font.BOLD, 20));
		dataServed.setFont(new Font("Serif", Font.BOLD, 20));
		packetsLstTotal.setForeground(Color.RED);
		bufferSize.setBounds(1050, 250, 100, 30);
		bufferSize.setText(Integer.toString(BUFFERSIZE));
		bufferSize.setEnabled(false);
		bufferSizeTxt.setBounds(950, 250, 90, 30);
		bufferSizeTxt.setText("Buffer size");
		
		switchingMode.add(switchingMode_SF);
		switchingMode.add(switchingMode_CT);
		switchingModeTxt.setBounds(950, 300, 200, 20);
		switchingModeTxt.setText("Switching mode");
		switchingMode_SF.setBounds(950, 330, 200, 20);
		switchingMode_SF.setText("Store and forward");
		switchingMode_SF.setSelected(true);
		switchingMode_SF.setEnabled(false); //TODO
		switchingMode_CT.setBounds(950, 350, 200, 20);
		switchingMode_CT.setText("Cut through");
		switchingMode_CT.setEnabled(false); //TODO
		
		frameLength.add(frameLengthFixed);
		frameLength.add(frameLengthVary);
		frameLengthTxt.setBounds(950, 400, 140, 20);
		frameLengthTxt.setText("Frame length");
		frameLengthFixed.setBounds(950, 430, 200, 20);
		frameLengthFixed.setText("Fixed 64B");
		frameLengthFixed.setSelected(true);
		frameLengthFixed.addActionListener(this);
		frameLengthVary.setBounds(950, 450, 200, 20);;
		frameLengthVary.setText("64B - 1536B");
		frameLengthVary.addActionListener(this);
		
		frameMinDelay.setBounds(950, 520, 200, 20);
		frameMinDelay.setText(String.valueOf(0));
		frameMinDelayTxt.setBounds(950, 500, 200, 20);
		frameMinDelayTxt.setText("Minimal delay between frames");
		
		picSwitch.setBounds(53, 50, 947, 150);
		for(Integer i = 0; i < PORTNUMBER; i++)
		{
			picPort[i] = new JLabel();
			picPort[i].setBounds(250+(54*i), 120, 54, 47);
			picPort[i].setIcon(portOFF);
			window.add(picPort[i]);
		}
		
		//Status display
		CAMTxt.setBounds(50, 570, 200, 25);
		CAMTxt.setText("CAM TABLE");
		CAMScroll.setBounds(50, 600, 550, 150);
		CAMScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        CAMScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        CAM.setText("CAM Table will be listed here");
        CAM.setEditable(false);
        buttonFlushCAM.setBounds(450, 570, 150, 25);
        buttonFlushCAM.setText("Flush memory");
        buttonFlushCAM.addActionListener(this);
        
        BufferTxt.setBounds(620, 570, 150, 25);
		BufferTxt.setText("INTERFACE BUFFER");
		BufferScroll.setBounds(620, 600, 550, 150);
		BufferScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        BufferScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Buffer.setText("Buffer of selected interface will be showed here");
        Buffer.setEditable(false);
        BufferSelect.setBounds(740, 570, 60, 25);
        BufferSelect.addActionListener(this);
        buttonClearBuffers.setText("Clear all buffers");
        buttonClearBuffers.setBounds(820, 570, 150, 25);
        buttonClearBuffers.addActionListener(this);
		
		//Add to JPanel
        setStatus("Setting up window", false);
		window.add(buttonRun);
		window.add(buttonStop);
		window.add(buttonClr);
		window.add(iterationsTxt);
		window.add(iterations);
		window.add(status);
		window.add(rngType);
		window.add(rngTypeTxt);
		window.add(handling);
		window.add(handlingTxt);
		window.add(progressBar);
		window.add(copy);
		
		window.add(packetsRxTxt);
		window.add(packetsTxTxt);
		window.add(packetsLstTxt);
		window.add(packetsBrdTxt);
		window.add(packetsLstTotal);
		window.add(dataInbound);
		window.add(dataServed);
		
		window.add(bufferSize);
		window.add(bufferSizeTxt);
		window.add(switchingModeTxt);
		window.add(switchingMode_SF);
		window.add(switchingMode_CT);
		window.add(frameLengthTxt);
		window.add(frameLengthFixed);
		window.add(frameLengthVary);
		window.add(frameMinDelay);
		window.add(frameMinDelayTxt);
		
		window.add(picSwitch);
		
		window.add(CAMScroll);
		window.add(CAMTxt);
		window.add(buttonFlushCAM);
		window.add(BufferScroll);
		window.add(BufferTxt);
		window.add(BufferSelect);
		window.add(buttonClearBuffers);
		
		window.setVisible(true);
		setStatus("Ready", false);
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
			
			//Setup RNG
			if(rngSelected == RNGTYPE.UNIFORM)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Uniform();
			if(rngSelected == RNGTYPE.EXP)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Exponential(0.001);
			if(rngSelected == RNGTYPE.NORMAL)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Normal();
			
			Integer FrameLength = 64;
			Integer minDelay = Integer.valueOf(frameMinDelay.getText());
			Double dataIn = 0.0;
			Double dataOut = 0.0;
			time =  Integer.valueOf( iterations.getText() );
			progressBar.setValue(0);
			progressBar.setMaximum(time);
			LocalDateTime then = LocalDateTime.now();
			Integer oldProgress = 0;
			while (true) //SIMULATION START
			{
				if (ChronoUnit.SECONDS.between(then, LocalDateTime.now()) >= time) break;
				progressBar.setValue((int) ChronoUnit.SECONDS.between(then, LocalDateTime.now()));
				//ACTUAL SWITCH
				for(Integer i = 0; i < PORTNUMBER; i++) //RX
				{
					if(mySwitch.ethernet[i].isDown()) continue;
					Integer byteCounter = 0; //How many bytes interface has received
					while(byteCounter < 5120)
					{
						/*mySwitch.ethernet[2].Rx.push(new traffic.Frame(69, 2, 1, 3, 7));
						mySwitch.ethernet[2].Rx.push(new traffic.Frame(0, 2, 1));
						mySwitch.CAM.push(new l2.MAC(2, 3), 2);*/
						if(mySwitch.ethernet[i].Rx.Idle == 0) //Generate new frame
						{
							Boolean targetHostUp = false;
							Boolean broadcast = false;
							int targetHost = -1;
							while(!targetHostUp)
							{
								targetHost = (int) (targetPortRNG.getNextInt(0, 8));
								if(targetHost > PORTNUMBER - 1)
								{
									broadcast = true;
									break;
								}
								if(mySwitch.ethernet[targetHost].isUp() && targetHost != i) targetHostUp = true;
							}
							
							setStatus("Generating new frame from interface " + i + " to interface " + Integer.toString(targetHost), false);
							Integer length = 64;
							if(!frameFixedSize) length = 64 * (int) (lengthRng.getNext() * 24 + 1);
							Frame frame = new Frame(length, i, targetHost, 1, 1);
							dataIn += length;
							if(broadcast) frame = new Frame(length, i, 1); //Broadcast
							if(!mySwitch.ethernet[i].Rx.isFull()) //Frame received
							{
								mySwitch.ethernet[i].Rx.push(frame);
								Integer Rx = Integer.valueOf(packetsRx[i].getText()) + 1;
								packetsRx[i].setText( Integer.toString(Rx) );
							}
							else //Buffer is full - frame lost
							{
								Integer Lst = Integer.valueOf(packetsLst[i].getText()) + 1;
								packetsLst[i].setText( Integer.toString(Lst) );
							}
							mySwitch.ethernet[i].Rx.Idle = (int) interfaceRNG[i].getNext() * 1 + FrameLength + minDelay;
							setStatus("Updating CAM table", false);
							if(! mySwitch.CAM.exists(frame.getSource()))
							{
								mySwitch.CAM.push(frame.getSource(), Character.getNumericValue( frame.getSource().getInterface() ));
							}
							else
							{
								mySwitch.CAM.isAlive(frame.getSource());
							}
							if(! mySwitch.CAM.exists(frame.getDestination())) //If destination MAC unknown set frame to broadcast
							{
								frame.setBroadcast();
							}
						}
						mySwitch.ethernet[i].Rx.Idle--;
						byteCounter++;
						if(mySwitch.ethernet[i].Rx.isEmpty()) picPort[i].setIcon(portON);
						else picPort[i].setIcon(portTRX);
					}
				}
				for(Integer i = 0; i < PORTNUMBER; i++) //SWITCHING
				{
					if(mySwitch.ethernet[i].isDown()) continue;
					Integer byteCounter = 0; //How many bytes interface has switched
					while(byteCounter < 5120)
					{
						//Push new frame if ready, Tx is free and Rx not empty
						if(mySwitch.ethernet[i].Tx.IdleSwitch <= 0
								&& !mySwitch.ethernet[i].Rx.isEmpty()
								&& mySwitch.ethernet[i].Tx.getCurrentSize() < mySwitch.ethernet[i].Tx.getSize())
						{
							if(mySwitch.ethernet[i].Rx.buffer[0].getBroadcast() == false)
							{
								int targetInterface = Character.getNumericValue(mySwitch.ethernet[i].Rx.buffer[0].getDestination().getInterface());
								mySwitch.ethernet[targetInterface].Tx.push( mySwitch.ethernet[i].Rx.buffer[0] );
								mySwitch.ethernet[targetInterface].Tx.IdleSwitch = mySwitch.ethernet[i].Rx.buffer[0].getLength();
								setStatus("Switching frame from interface " + mySwitch.ethernet[i].Rx.buffer[0].getSource().getInterface() + " to " + targetInterface, false);
								//Add handling time
								if(handlingMode == 1) mySwitch.ethernet[i].Tx.IdleSwitch += mySwitch.ethernet[i].Rx.buffer[0].getLength() / 10;
								if(handlingMode == 2) mySwitch.ethernet[i].Tx.IdleSwitch += mySwitch.ethernet[i].Rx.buffer[0].getLength() / 4;
								if(handlingMode == 3) mySwitch.ethernet[i].Tx.IdleSwitch += mySwitch.ethernet[i].Rx.buffer[0].getLength() / 2;
								if(handlingMode == 4) mySwitch.ethernet[i].Tx.IdleSwitch += mySwitch.ethernet[i].Rx.buffer[0].getLength();
								if(handlingMode == 5) mySwitch.ethernet[i].Tx.IdleSwitch += (int) handlingRng.getNext() * 1;
								//Drop frame from Rx
								mySwitch.ethernet[i].Rx.pop();
							}
							else
							{
								for(Integer j = 0; j < PORTNUMBER; j++) //Push broadcast to all active interfaces
								{
									if(mySwitch.ethernet[j].isDown() || i == j) continue;
									mySwitch.ethernet[j].Tx.push( mySwitch.ethernet[i].Rx.buffer[0] );
									mySwitch.ethernet[j].Tx.IdleSwitch = mySwitch.ethernet[i].Rx.buffer[0].getLength();
									//Add handling time
									if(handlingMode == 1) mySwitch.ethernet[j].Tx.IdleSwitch += mySwitch.ethernet[j].Rx.buffer[0].getLength() / 10;
									if(handlingMode == 2) mySwitch.ethernet[j].Tx.IdleSwitch += mySwitch.ethernet[j].Rx.buffer[0].getLength() / 4;
									if(handlingMode == 3) mySwitch.ethernet[j].Tx.IdleSwitch += mySwitch.ethernet[j].Rx.buffer[0].getLength() / 2;
									if(handlingMode == 4) mySwitch.ethernet[j].Tx.IdleSwitch += mySwitch.ethernet[j].Rx.buffer[0].getLength();
									if(handlingMode == 5) mySwitch.ethernet[j].Tx.IdleSwitch += (int) handlingRng.getNext() * 1;
									byteCounter++;
									
									Integer Brd = Integer.valueOf(packetsBrd[i].getText()) + 1;
									packetsBrd[i].setText( Integer.toString(Brd) );
								}
								//Drop frame from Rx
								mySwitch.ethernet[i].Rx.pop();
							}
						}
						mySwitch.ethernet[i].Tx.IdleSwitch--;
						byteCounter++;
					}
				}
				for(Integer i = 0; i < PORTNUMBER; i++) //TX
				{
					if(mySwitch.ethernet[i].isDown()) continue;
					Integer byteCounter = 0; //How many bytes interface has transmitted
					while(byteCounter < 5120)
					{
						if(mySwitch.ethernet[i].Tx.Idle <= 0
								&& !mySwitch.ethernet[i].Tx.isEmpty())
						{
							setStatus("Transmitting frame from interface " + mySwitch.ethernet[i].Tx.buffer[0].getSource().getInterface() + " to " + mySwitch.ethernet[i].Tx.buffer[0].getDestination().getInterface(), false);
							dataOut += mySwitch.ethernet[i].Tx.buffer[0].getLength();
							mySwitch.ethernet[i].Tx.Idle = mySwitch.ethernet[i].Tx.buffer[0].getLength();
							mySwitch.ethernet[i].Tx.pop();
							picPort[i].setIcon(portTRX);
							Integer Tx = Integer.valueOf(packetsTx[i].getText()) + 1;
							packetsTx[i].setText( Integer.toString(Tx) );
						}
						else picPort[i].setIcon(portON);
						mySwitch.ethernet[i].Tx.Idle--;
						byteCounter++;
					}
				}
				//Validate CAM table
				mySwitch.CAM.decrement();
				mySwitch.CAM.validate();
				//Update GUI and print status every second
				if(oldProgress != progressBar.getValue())
				{
					oldProgress = progressBar.getValue();
					CAM.setText(mySwitch.CAM.listTable());
					
					double sumLst = 0.0;
					double sumTotal = 0.0;
					for(Integer i = 0; i < PORTNUMBER; i++)
					{
						sumLst += Integer.valueOf(packetsLst[i].getText());
						sumTotal += Integer.valueOf(packetsRx[i].getText());
					}
					Double losts = (sumLst / sumTotal) * 100;
					if(losts > 100) losts = 100.0;
					packetsLstTotal.setText( "Total lost: " + Double.toString(losts) + "%");
					dataInbound.setText( "Data in: " + Double.toString(dataIn / 1024 / 1024) + " Mb");
					dataServed.setText( "Data served: " + Double.toString(dataOut / 1024 / 1024) + " Mb");
					Buffer.setText("Rx:\n"+ mySwitch.ethernet[BufferSelect.getSelectedIndex() ].Rx.getString() + "\nTx:\n" + mySwitch.ethernet[ BufferSelect.getSelectedIndex() ].Tx.getString() );
				}
			} //SIMULATION END
			isRunning = false;
			
			progressBar.setValue( progressBar.getMaximum() );
			enableGUI();
			
			System.out.print(mySwitch.listInterfaces());
			//System.out.print(mySwitch.ethernet[2].Rx.getString());
			System.out.print(mySwitch.CAM.listTable());
			setStatus("Completed", false);
			picSwitch.setIcon(switchOFF);
			
			
			CAM.setText(mySwitch.CAM.listTable());
			mySwitch.CAM.decrement();
			mySwitch.CAM.validate();
			
			double sumLst = 0.0;
			double sumTotal = 0.0;
			for(Integer i = 0; i < PORTNUMBER; i++)
			{
				sumLst += Integer.valueOf(packetsLst[i].getText());
				sumTotal += Integer.valueOf(packetsRx[i].getText());
			}
			Double losts = (sumLst / sumTotal) * 100;
			if(losts > 100) losts = 100.0;
			DataInVal += (dataIn / 1024 / 1024);
			DataOutVal += (dataOut / 1024 / 1024);
			packetsLstTotal.setText( "Total lost: " + Double.toString(losts) + "%");
			dataInbound.setText( "Data in: " + Double.toString(DataInVal) + " Mb");
			dataServed.setText( "Data served: " + Double.toString(DataOutVal) + " Mb");
			Buffer.setText("Rx:\n"+ mySwitch.ethernet[BufferSelect.getSelectedIndex() ].Rx.getString() + "\nTx:\n" + mySwitch.ethernet[ BufferSelect.getSelectedIndex() ].Tx.getString() );
			this.interrupt();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == buttonRun)
		{			
			//Data validation
			double d; //Iterations
			double d2; //Delay
			try {
		        d = Double.parseDouble( String.valueOf(iterations.getText()) );
		        d2 = Double.parseDouble( String.valueOf(frameMinDelay.getText()) );
		    } 
			catch (NumberFormatException nfe) {
				setStatus("Invalid input - not a number", true);
		        return;
		    }
			if( d <= 0 || d2 < 0)
			{
				setStatus("Invalid input - must be greater than 0", true);
				return;
			}
			if( mySwitch.getNumberOfActiveInterfaces() < 2 )
			{
				setStatus("Cannot transmit - at least two interfaces must be up", true);
				return;
			}
			
			isRunning = !isRunning;
			//Start simulator
			Task progress = new Task();
			thread = new Thread(progress);
			thread.start();
		}
		if(e.getSource() == buttonStop)
		{
			setStatus("Simulation HALTED!", false);
			thread.stop(); //UNSAFE
			picSwitch.setIcon(switchOFF);
			enableGUI();
		}
		if(e.getSource() == buttonClr)
		{
			for(Integer i = 0; i < PORTNUMBER; i++)
			{
				packetsRx[i].setText("0");
				packetsTx[i].setText("0");
				packetsLst[i].setText("0");
				packetsBrd[i].setText("0");
			}
			packetsLstTotal.setText("Total lost: 0.0%");
			dataInbound.setText("Data in: 0 Mb");
			dataServed.setText("Data served: 0 Mb");
			setStatus("Statistics cleared", false);
		}
		if(e.getSource() == buttonFlushCAM)
		{
			mySwitch.CAM.flush();
			CAM.setText(mySwitch.CAM.listTable());
			setStatus("CAM table flushed", false);
		}
		if(e.getSource() == buttonClearBuffers)
		{
			for(int i = 0; i < PORTNUMBER; i++)
			{
				mySwitch.ethernet[i].Rx.clear();
				mySwitch.ethernet[i].Tx.clear();
			}
			setStatus("Removed frames from all buffers", false);
			Buffer.setText("Rx:\n"+ mySwitch.ethernet[BufferSelect.getSelectedIndex() ].Rx.getString() + "\nTx:\n" + mySwitch.ethernet[ BufferSelect.getSelectedIndex() ].Tx.getString() );
		}
		if(e.getSource() == rngType)
		{
			if(rngType.getSelectedIndex() == 0)
			{
				rngSelected = RNGTYPE.UNIFORM;
				setStatus("Random number generator switched to uniform distribution", false);
			}
			if(rngType.getSelectedIndex() == 1)
			{
				rngSelected = RNGTYPE.EXP;
				setStatus("Random number generator switched to exponential distribution", false);
			}
			if(rngType.getSelectedIndex() == 2)
			{
				rngSelected = RNGTYPE.NORMAL;
				setStatus("Random number generator switched to normal (Gauss) distribution", false);
			}
		}
		if(e.getSource() == handling)
		{
			handlingMode = handling.getSelectedIndex();
			setStatus("Handling time set to " + handling.getSelectedItem(), false);
		}
		if(e.getSource() == frameLengthFixed)
		{
			frameFixedSize = true;
			setStatus("Frame size set to fixed 64B", false);
		}
		if(e.getSource() == frameLengthVary)
		{
			frameFixedSize = false;
			setStatus("Frame size set to varied 64B-1536B", false);
		}
		for(Integer i = 0; i < PORTNUMBER; i++)
		{
			if(e.getSource() == ethernet[i])
			{
				if(ethernet[i].isSelected())
					{
					mySwitch.ethernet[i].setState(State.UP);
					picPort[i].setIcon(portON);
					setStatus("Interface " + i + " changed status to UP", false);
					}
				else {
					mySwitch.ethernet[i].setState(State.DOWN);
					picPort[i].setIcon(portOFF);
					setStatus("Interface " + i + " changed status to DOWN", false);
				}
			}
		}
		if(e.getSource() == BufferSelect)
		{
			Buffer.setText("Rx:\n"+ mySwitch.ethernet[BufferSelect.getSelectedIndex() ].Rx.getString() + "\nTx:\n" + mySwitch.ethernet[ BufferSelect.getSelectedIndex() ].Tx.getString() );
		}
	}
	
	
	public void enableGUI() //After simulation
	{
		setStatus("Enabling user interface", false);
		buttonRun.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonClr.setEnabled(true);
		buttonFlushCAM.setEnabled(true);
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(true);
		frameMinDelay.setEnabled(true);
		rngType.setEnabled(true);
		frameLengthFixed.setEnabled(true);
		frameLengthVary.setEnabled(true);
		handling.setEnabled(true);
	}
	
	public void disableGUI() //Before simulation
	{
		setStatus("Disabling user interface", false);
		buttonRun.setEnabled(false);
		buttonStop.setEnabled(true);
		buttonClr.setEnabled(false);
		buttonFlushCAM.setEnabled(false);
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(false);
		frameMinDelay.setEnabled(false);
		rngType.setEnabled(false);
		frameLengthFixed.setEnabled(false);
		frameLengthVary.setEnabled(false);
		handling.setEnabled(false);
	}
	
	public void setStatus(String msg, Boolean isError)
	{
		if(isError) status.setForeground(Color.RED);
		else status.setForeground(Color.BLACK);
		status.setText("Status: " + msg);
		System.out.println(status.getText());
	}
}
