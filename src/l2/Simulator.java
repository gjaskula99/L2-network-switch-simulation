package l2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.Vector;

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
import RNG.Constant;
import plot.XYLineChart;

public class Simulator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//Global variables
	static Simulator main;
	Integer time = -1;
	Boolean isRunning = false;
	Thread thread;
	static Integer BUFFERSIZE = 10;
	static final Integer PORTNUMBER = 8;
	static Integer INTERFACESPEED = 6400;
	
	enum RNGTYPE {UNIFORM, EXP, NORMAL, CONST};
	RNGTYPE rngSelected = RNGTYPE.EXP;
	Exponential handlingRng = new Exponential(0.1);
	Uniform lengthRng = new Uniform();
	
	double TotalLost = 0.0;
	double DataInVal = 0.0;
	double DataOutVal = 0.0;
	Integer handlingMode = 1;
	Boolean frameFixedSize = true;
	Integer broadcastTreshold = 5;
	Integer MACTimeToLive = 10;
	Boolean MACFlooding = false;
	Boolean CutThrough = true;
	Boolean forceBroadcastPush = false;
	
	//GUI
	JFrame window = new JFrame();
	JLabel status = new JLabel();
	JLabel copy = new JLabel();
	
	JButton buttonRun = new JButton("RUN");
	JButton buttonStop = new JButton("HALT");
	JButton buttonClr = new JButton("Clear stats");
	JLabel iterationsTxt = new JLabel();
	JTextField iterations = new JTextField();
	JLabel speedTxt = new JLabel("eth speed (bytes)");
	JTextField speed = new JTextField("6400");
	String[] rngTypeString = {"Uniform", "Exponential", "Normal", "Constant"};
	JComboBox<String> rngType = new JComboBox<String>(rngTypeString);
	JLabel rngTypeTxt = new JLabel();
	String[] handlingString = {"Instant", "10% of frame length", "25% of frame length", "50% of frame length" , "100% of frame length" , "Exp(0.001)", "Exp(0.01)", "Exp(0.1)"};
	JTextField rngParam1 = new JTextField();
	JTextField rngParam2 = new JTextField();
	JLabel rngParamsTxt = new JLabel();
	
	JProgressBar progressBar = new JProgressBar();
	
	JCheckBox[] ethernet = new JCheckBox[8];
	JTextField bufferSize = new JTextField();
	JLabel bufferSizeTxt = new JLabel();
	ButtonGroup switchingMode = new ButtonGroup();
	JLabel switchingModeTxt = new JLabel();
	JRadioButton switchingMode_SF = new JRadioButton();
	JRadioButton switchingMode_CT = new JRadioButton();
	JCheckBox forcePush = new JCheckBox();
	ButtonGroup frameLength = new ButtonGroup();
	JLabel frameLengthTxt = new JLabel();
	JRadioButton frameLengthFixed = new JRadioButton();
	JRadioButton frameLengthVary = new JRadioButton();
	JCheckBox flooding = new JCheckBox();
	JTextField frameMinDelay = new JTextField();
	JLabel frameMinDelayTxt = new JLabel();
	JComboBox<String> handling = new JComboBox<String>(handlingString);
	JLabel handlingTxt = new JLabel();
	JTextField MAC_TTL = new JTextField();
	JLabel MAC_TTLTxt = new JLabel();
	JTextField MAC_Size = new JTextField();
	JLabel MAC_SizeTxt = new JLabel();
	JTextField broadcast = new JTextField();
	JLabel broadcastTxt = new JLabel();
	
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
	JLabel packetsBrdTxt = new JLabel("Brdcst Tx:");
	JLabel packetsLstTotal = new JLabel("Total lost: 0.0%");
	JLabel dataInbound = new JLabel("Data in: 0 Mb");
	JLabel dataServed = new JLabel("Data out: 0 Mb");
	
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
	
	//Plotting
	String plotTypes[] = {"CAM TTL", "Broadcast %", "Received frames", "Transmitted frames", "Lost %",
			"Transmitted broadcast frames", "Traffic intensity", "Data in (Mb)", "Data out (Mb)"};
	JComboBox plotTypeX = new JComboBox(plotTypes);
	JComboBox plotTypeY = new JComboBox(plotTypes);
	JLabel plotTypeTxt = new JLabel();
	JButton plotMake = new JButton();
	JButton plotClear = new JButton();
	//Plot data
	Vector<Double> plotData_CAMTTL = new Vector<Double>();
	Vector<Double> plotData_Broadcast = new Vector<Double>();
	Vector<Double> plotData_Received = new Vector<Double>();
	Vector<Double> plotData_Transmitted = new Vector<Double>();
	Vector<Double> plotData_Losts = new Vector<Double>();
	Vector<Double> plotData_BroadcastTx = new Vector<Double>();
	Vector<Double> plotData_Intensity = new Vector<Double>();
	Vector<Double> plotData_TrafficIn = new Vector<Double>();
	Vector<Double> plotData_TrafficOut = new Vector<Double>();
	
	//Logic
	Switch mySwitch = new Switch(BUFFERSIZE);
	Random[] interfaceRNG = new Random[PORTNUMBER];
	Double generatorParam1 = 0.0;
	Double generatorParam2 = 0.0;
	
	Uniform targetPortRNG = new Uniform();
	Uniform broadcastRNG = new Uniform();
	
	//Constructior
	Simulator()
	{
		//Setup GUI
		setStatus("Creating GUI", false);
		
		window.setBounds(EXIT_ON_CLOSE, ABORT, 1300, 800);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setTitle("L2 network switch simulator");
		window.setLayout(null);
		
		status.setBounds(20, 10, 980, 40);
		status.setFont(new Font("Serif", Font.PLAIN, 24));;
		copy.setBounds(5, 740, 1180, 30);
		copy.setFont(new Font("Serif", Font.PLAIN, 10));
		copy.setText("©Grzegorz Jaskuła, Adam Rektor 2023. Supervised by prof. Maciej Stasiak, Phd. Slawomir Hanczewski. Poznan University of Technology. This software is for educational purposes only and is provided as it is.");
		
		iterationsTxt.setBounds(1050, 30, 140, 30);
		iterationsTxt.setText("Run for seconds");
		iterations.setBounds(1050, 60, 100, 30);
		iterations.setText(Integer.toString(0));
		speedTxt.setBounds(1160, 30, 120, 30);
		speed.setBounds(1160, 60, 100, 30);
		buttonRun.setBounds(1050, 90, 100, 30);
		buttonRun.addActionListener(this);
		buttonStop.setBounds(1050, 120, 100, 30);
		buttonStop.setForeground(Color.RED);
		buttonStop.setEnabled(false);
		buttonStop.addActionListener(this);
		buttonClr.setBounds(30, 500, 170, 30);
		buttonClr.setEnabled(true);
		buttonClr.addActionListener(this);
		
		plotTypeTxt.setBounds(220, 500, 90, 30);
        plotTypeTxt.setText("Generate chart");
        plotTypeX.setBounds(310, 500, 140, 30);
        plotTypeX.setSelectedIndex(6);
        plotTypeY.setBounds(460, 500, 140, 30);
        plotTypeY.setSelectedIndex(4);
        plotMake.setBounds(620, 500, 150, 30);
        plotMake.setText("Create chart");
        plotMake.addActionListener(this);
        plotClear.setBounds(780, 500, 150, 30);
        plotClear.setText("Clear charts data");
        plotClear.addActionListener(this);
		
		rngType.setBounds(1020, 170, 170, 20);
		rngType.setSelectedIndex(1);
		rngType.addActionListener(this);
		rngTypeTxt.setBounds(1020, 150, 170, 20);
		rngTypeTxt.setText("Traffic generator type");
		
		rngParam1.setBounds(1020, 220, 100, 20);
		rngParam1.setText("Lambda");
		rngParam1.addActionListener(this);
		rngParam2.setBounds(1150, 220, 100, 20);
		rngParam2.setEnabled(false);
		rngParam2.setText("N/A");
		rngParam2.addActionListener(this);
		rngParamsTxt.setBounds(1020, 200, 200, 20);
		rngParamsTxt.setText("RNG parameters");
		
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
		bufferSize.setBounds(1020, 250, 100, 30);
		bufferSize.setText(Integer.toString(BUFFERSIZE));
		bufferSize.setEnabled(false);
		bufferSizeTxt.setBounds(950, 250, 90, 30);
		bufferSizeTxt.setText("Buffer size");
		
		switchingMode.add(switchingMode_SF);
		switchingMode.add(switchingMode_CT);
		switchingModeTxt.setBounds(950, 300, 150, 20);
		switchingModeTxt.setText("Switching mode");
		switchingMode_SF.setBounds(950, 330, 150, 20);
		switchingMode_SF.setText("Store and forward");
		switchingMode_SF.addActionListener(this);
		switchingMode_CT.setBounds(950, 350, 150, 20);
		switchingMode_CT.setText("Cut through");
		switchingMode_CT.addActionListener(this);
		switchingMode_CT.setSelected(true);
		
		broadcast.setBounds(1100, 330, 50, 20);
		broadcast.setText(Integer.toString(broadcastTreshold));
		broadcastTxt.setBounds(1100, 300, 150, 20);
		broadcastTxt.setText("% of broadcast traffic");
		
		forcePush.setBounds(950, 370, 250, 20);
		forcePush.setText("Force broadcast pushing");
		forcePush.addActionListener(this);
		
		frameLength.add(frameLengthFixed);
		frameLength.add(frameLengthVary);
		frameLengthTxt.setBounds(950, 400, 140, 20);
		frameLengthTxt.setText("Frame length");
		frameLengthFixed.setBounds(950, 430, 150, 20);
		frameLengthFixed.setText("Fixed 64B");
		frameLengthFixed.setSelected(true);
		frameLengthFixed.addActionListener(this);
		frameLengthVary.setBounds(950, 450, 150, 20);;
		frameLengthVary.setText("64B - 1536B");
		frameLengthVary.addActionListener(this);
		
		flooding.setBounds(950, 480, 140, 20);
		flooding.setText("MAC flooding");
		flooding.addActionListener(this);
		
		handling.setBounds(1100, 430, 150, 20);
		handling.setSelectedIndex(0);
		handling.addActionListener(this);
		handlingTxt.setBounds(1100, 400, 150, 20);
		handlingTxt.setText("Frame handle time");
		
		frameMinDelay.setBounds(950, 520, 50, 20);
		frameMinDelay.setText(String.valueOf(0));
		frameMinDelayTxt.setBounds(950, 500, 200, 20);
		frameMinDelayTxt.setText("Minimal delay between frames");
		
		MAC_TTL.setBounds(1150, 520, 50, 20);
		MAC_TTL.setText(Integer.toString(MACTimeToLive));
		MAC_TTLTxt.setBounds(1150, 500, 200, 20);
		MAC_TTLTxt.setText("CAM table entry TTL");
		MAC_Size.setBounds(1150, 560, 50, 20);
		MAC_Size.setText("32");
		MAC_Size.setEnabled(false);
		MAC_SizeTxt.setBounds(1150, 540, 200, 20);
		MAC_SizeTxt.setText("CAM table size");
		
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
		window.add(plotTypeX);
		window.add(plotTypeY);
		window.add(plotTypeTxt);
		window.add(plotMake);
		window.add(plotClear);
		window.add(iterationsTxt);
		window.add(iterations);
		window.add(speedTxt);
		window.add(speed);
		window.add(status);
		window.add(rngType);
		window.add(rngTypeTxt);
		window.add(rngParam1);
		window.add(rngParam2);
		window.add(rngParamsTxt);
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
		window.add(broadcast);
		window.add(broadcastTxt);
		window.add(forcePush);
		window.add(frameLengthTxt);
		window.add(frameLengthFixed);
		window.add(frameLengthVary);
		window.add(flooding);
		window.add(handling);
		window.add(handlingTxt);
		window.add(frameMinDelay);
		window.add(frameMinDelayTxt);
		window.add(MAC_TTL);
		window.add(MAC_TTLTxt);
		window.add(MAC_Size);
		window.add(MAC_SizeTxt);
		
		window.add(picSwitch);
		
		window.add(CAMScroll);
		window.add(CAMTxt);
		window.add(buttonFlushCAM);
		window.add(BufferScroll);
		window.add(BufferTxt);
		window.add(BufferSelect);
		window.add(buttonClearBuffers);
		
		//Read config
	    System.out.println("Working Directory = " + System.getProperty("user.dir"));
	    Properties prop = new Properties();
	    String fileName = "Switch.config";
	    System.out.println("Reading config file...");
	    try (FileInputStream fis = new FileInputStream(fileName)) {
	        prop.load(fis);
	        System.out.println("config loaded - " + fileName + "\n");
	        
	        //Buffer size
	        System.out.println("Switch.bufferSize : " + prop.getProperty("Switch.bufferSize"));
	        BUFFERSIZE = Integer.valueOf(prop.getProperty("Switch.bufferSize"));
	        bufferSize.setText(prop.getProperty("Switch.bufferSize"));
	        //CAM size
	        System.out.println("CAM.size : " + prop.getProperty("CAM.size"));
	        MAC_Size.setText(prop.getProperty("CAM.size"));
	        mySwitch = new Switch(BUFFERSIZE, 1, Integer.valueOf(prop.getProperty("CAM.size")));
	        //CAM TTL
	        System.out.println("CAM.TTL : " + prop.getProperty("CAM.TTL"));
	        MACTimeToLive = Integer.valueOf(prop.getProperty("CAM.TTL"));
	        MAC_TTL.setText(prop.getProperty("CAM.TTL"));
	        //MAC flooding
	        System.out.println("CAM.flood : " + prop.getProperty("CAM.flood"));
	        MACFlooding = Boolean.valueOf(prop.getProperty("CAM.flood"));
	        flooding.setSelected(MACFlooding);
	        //Broadcast
	        System.out.println("Switch.broadcast : " + prop.getProperty("Switch.broadcast"));
	        broadcast.setText(prop.getProperty("Switch.broadcast"));
	        broadcastTreshold = Integer.valueOf(prop.getProperty("Switch.broadcast"));
	        //Broadcast force push
	        System.out.println("Switch.forcePush : " + prop.getProperty("Switch.forcePush"));
	        forceBroadcastPush = Boolean.valueOf(prop.getProperty("Switch.forcePush"));
	        forcePush.setSelected(forceBroadcastPush);
	        //Interface states
	        for(int i = 0; i < PORTNUMBER; i++)
	        {
		        System.out.println("Switch.interface" + i + " : " + prop.getProperty("Switch.interface" + i));
		        if( Boolean.valueOf(prop.getProperty("Switch.interface" + i) ))
		        {
		        	mySwitch.ethernet[i].setState(State.UP);
		        	ethernet[i].setSelected(true);
		        	picPort[i].setIcon(portON);
		        }
	        }
	        //Speed
	        System.out.println("Switch.speed : " + prop.getProperty("Switch.speed"));
	        INTERFACESPEED = Integer.valueOf(prop.getProperty("Switch.speed"));
	        speed.setText(prop.getProperty("Switch.speed"));
	        //Switching mode
	        System.out.println("Switch.modeCF : " + prop.getProperty("Switch.modeCF"));
	        CutThrough = Boolean.valueOf(prop.getProperty("Switch.modeCF"));
	        if(CutThrough) switchingMode_CT.setSelected(true);
	        else switchingMode_SF.setSelected(true);
	        //Frame size
	        System.out.println("Switch.fixedFrame : " + prop.getProperty("Switch.fixedFrame"));
	        frameFixedSize = Boolean.valueOf(prop.getProperty("Switch.fixedFrame"));
	        if(frameFixedSize) frameLengthFixed.setSelected(true);
	        else frameLengthVary.setSelected(true);
	        //Handling mode
	        System.out.println("Switch.handling : " + prop.getProperty("Switch.handling"));
	        handlingMode = Integer.valueOf(prop.getProperty("Switch.delay"));
	        handling.setSelectedIndex(Integer.valueOf(prop.getProperty("Switch.handling")));
	        //Minimal delay
	        System.out.println("Switch.delay : " + prop.getProperty("Switch.delay"));
	        frameMinDelay.setText(prop.getProperty("Switch.delay"));
	        //Running time
	        System.out.println("Simulator.iterations : " + prop.getProperty("Simulator.iterations"));
	        iterations.setText( prop.getProperty("Simulator.iterations") );
	        //Traffic RNG
	        System.out.println("TrafficRNG.type : " + prop.getProperty("TrafficRNG.type"));
	        rngType.setSelectedIndex( Integer.valueOf(prop.getProperty("TrafficRNG.type")) );
	        System.out.println("TrafficRNG.param1 : " + prop.getProperty("TrafficRNG.param1"));
	        generatorParam1 =  Double.valueOf(prop.getProperty("TrafficRNG.param1"));
	        rngParam1.setText( prop.getProperty("TrafficRNG.param1") );
	        System.out.println("TrafficRNG.param2 : " + prop.getProperty("TrafficRNG.param2"));
	        generatorParam2 =  Double.valueOf(prop.getProperty("TrafficRNG.param2"));
	        rngParam2.setText( prop.getProperty("TrafficRNG.param2") );
	        
		    System.out.println("Configuration loaded\n");
		    assert BUFFERSIZE > 0;
		    assert Integer.valueOf(MAC_Size.getText()) > 0;
	    } catch (Exception ex) {
	    	System.out.println("ERROR - " + ex.getLocalizedMessage() + "\nProceeding with default values\n");
	    }
	    
	    window.setIconImage(portON.getImage());
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
			else if(rngSelected == RNGTYPE.EXP)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Exponential(generatorParam1);
			else if(rngSelected == RNGTYPE.NORMAL)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Normal(generatorParam1, generatorParam2);
			else if(rngSelected == RNGTYPE.CONST)
				for(Integer i = 0; i < PORTNUMBER; i++) interfaceRNG[i] = new Constant(generatorParam1);
			
			Uniform floodRNG = new Uniform();
			
			if(!CutThrough)
			{
				for(int i = 0; i < PORTNUMBER; i++) mySwitch.ethernet[0].Rx.Idle = 1;
			}
			
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
				//MAC flooding
				if(MACFlooding)
				{
					int numOfFrames = floodRNG.getNextInt(0, BUFFERSIZE);
					for(int i = 0; i < numOfFrames; i++)
					{
						if(mySwitch.ethernet[0].Rx.isFull()) break;
						Frame floodFrame = new Frame("213769ABCD" + String.valueOf(floodRNG.getNextInt(0, 9)+String.valueOf(floodRNG.getNextInt(0, 9)) ));
						//System.out.println(floodFrame.getSource().getString());
						mySwitch.ethernet[0].Rx.push(floodFrame);
						Integer Lst = Integer.valueOf(packetsLst[0].getText()) + 1;
						packetsLst[0].setText( Integer.toString(Lst) );
					}
				}
				//Looping through interfaces
				for(Integer i = 0; i < PORTNUMBER; i++)
				{
					if(mySwitch.ethernet[i].isDown()) continue;
					Frame frame = new Frame();
					Integer SF_idle = 0;
					Integer byteCounter = 0; //How many bytes interface has received
					//RECEIVING
					//System.out.println("RECEIVING");
					while(byteCounter < INTERFACESPEED)
					{
						/*mySwitch.ethernet[2].Rx.push(new traffic.Frame(69, 2, 1, 3, 7));
						mySwitch.ethernet[2].Rx.push(new traffic.Frame(0, 2, 1));
						mySwitch.CAM.push(new l2.MAC(2, 3), 2);*/
						if(CutThrough && mySwitch.ethernet[i].Rx.Idle == 0) //Generate new frame in cut-through
						{
							Boolean targetHostUp = false;
							Boolean broadcast = false;
							int targetHost = -1;
							if(broadcastRNG.getNextInt(0, 99) < broadcastTreshold) broadcast = true;
							else while(!targetHostUp)
							{
								targetHost = (targetPortRNG.getNextInt(0, 7));
 								if(mySwitch.ethernet[targetHost].isUp() && targetHost != i) targetHostUp = true;
							}
							
							setStatus("Generating new frame from interface " + i + " to interface " + Integer.toString(targetHost), false);
							Integer length = 64;
							if(!frameFixedSize) length = 64 * (int) (lengthRng.getNext() * 24 + 1);
							frame = new Frame(length, i, targetHost, 1, 1);
							dataIn += length;
							if(broadcast) frame = new Frame(length, i, 1); //Broadcast
							if(!mySwitch.ethernet[i].Rx.isFull()) //Frame received
							{
								if(CutThrough)
								{
									mySwitch.ethernet[i].Rx.push(frame);
									Integer Rx = Integer.valueOf(packetsRx[i].getText()) + 1;
									packetsRx[i].setText( Integer.toString(Rx) );
								}
							}
							else //Buffer is full - frame lost
							{
								Integer Lst = Integer.valueOf(packetsLst[i].getText()) + 1;
								packetsLst[i].setText( Integer.toString(Lst) );
							}
							mySwitch.ethernet[i].Rx.Idle = Math.abs( (int) interfaceRNG[i].getNext() + FrameLength + minDelay);
						}
						if(!CutThrough) //Generate new frame in store&forward
						{
							if(mySwitch.ethernet[i].Rx.Idle == 0) SF_idle = 64;
							if(SF_idle == 0)
							{
							Boolean targetHostUp = false;
							Boolean broadcast = false;
							int targetHost = -1;
							if(broadcastRNG.getNextInt(0, 99) < broadcastTreshold) broadcast = true;
							else while(!targetHostUp)
							{
								targetHost = (targetPortRNG.getNextInt(0, 7));
								if(mySwitch.ethernet[targetHost].isUp() && targetHost != i) targetHostUp = true;
							}
							
							setStatus("Generating new frame from interface " + i + " to interface " + Integer.toString(targetHost), false);
							Integer length = 64;
							frame = new Frame(length, i, targetHost, 1, 1);
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
							mySwitch.ethernet[i].Rx.Idle = Math.abs( (int) interfaceRNG[i].getNext() + minDelay);
							}
							SF_idle--;
						}
						mySwitch.ethernet[i].Rx.Idle--;
						byteCounter++;
						if(mySwitch.ethernet[i].Rx.isEmpty()) picPort[i].setIcon(portON);
						else picPort[i].setIcon(portTRX);
					}
					//SWITCHING
					//System.out.println("SWITCHING");
					byteCounter = 0;
					while(byteCounter < INTERFACESPEED)
					{
						//Push new frame if ready, Tx is free and Rx not empty
						if(mySwitch.ethernet[i].Tx.IdleSwitch <= 0
								&& !mySwitch.ethernet[i].Rx.isEmpty()
								//&& mySwitch.ethernet[i].Tx.getCurrentSize() < mySwitch.ethernet[i].Tx.getSize()
								)
						{
							//Check if target interface engress is full. If so end the loop.
							//Does not apply to broadcast. If any of interfaces will be full the broadcast frame will not be pushed to it but will be removed from Rx.
							//15 (F) is equal to broadcast.
							boolean BREAK = false; //Used for forceBroadcastPush
							int target = Character.getNumericValue( mySwitch.ethernet[i].Rx.buffer[0].getDestination().getInterface() );
							if(target != 15) if(mySwitch.ethernet[ target ].Tx.isFull()) break;
							else if(!forceBroadcastPush)
							{
								for(int ii = 0; ii < PORTNUMBER; ii++) //Loop through all interfaces to check if not full
								{
									if(ii == i) continue; //Skip source interface
									if(mySwitch.ethernet[i].isDown()) continue;
									if(mySwitch.ethernet[i].Tx.isFull())
									{
										BREAK = true;
										break;
									}
								}
							}
							if(BREAK) break; //Abort if frame is broadcast and cannot be fully pushed
							
							Frame f = mySwitch.ethernet[i].Rx.pop();
							//CAM table
							setStatus("Updating CAM table", false); //Found in memory
							if(! mySwitch.CAM.exists(f.getSource()))
							{
								if(! mySwitch.CAM.push(f.getSource(), Character.getNumericValue( f.getSource().getInterface() )) )
								{
									setStatus("CAM table is full!", false);
								}
							}
							else //Not Found in memory
							{
								mySwitch.CAM.isAlive(f.getSource());
							}
							if(! mySwitch.CAM.exists(f.getDestination())) //If destination MAC unknown set frame to broadcast
							{
								f.setBroadcast();
							}
							//Actual switching
							if(f.getBroadcast() == false)
							{
								int targetInterface = Character.getNumericValue(f.getDestination().getInterface());
								mySwitch.ethernet[targetInterface].Tx.push( f );
								mySwitch.ethernet[targetInterface].Tx.IdleSwitch = f.getLength();
								setStatus("Switching frame from interface " + f.getSource().getInterface() + " to " + targetInterface, false);
								//Add handling time
								if(handlingMode == 1) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 10;
								else if(handlingMode == 2) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 4;
								else if(handlingMode == 3) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 2;
								else if(handlingMode == 4) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength();
								else if(handlingMode >= 5) mySwitch.ethernet[i].Tx.IdleSwitch += (int) handlingRng.getNext();
							}
							else
							{
								for(Integer j = 0; j < PORTNUMBER; j++) //Push broadcast to all active interfaces
								{
									if(mySwitch.ethernet[j].isDown() || i == j) continue;
									mySwitch.ethernet[j].Tx.push( f );
									mySwitch.ethernet[j].Tx.IdleSwitch = f.getLength();
									//Add handling time
									if(handlingMode == 1) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 10;
									else if(handlingMode == 2) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 4;
									else if(handlingMode == 3) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength() / 2;
									else if(handlingMode == 4) mySwitch.ethernet[i].Tx.IdleSwitch += f.getLength();
									else if(handlingMode >= 5) mySwitch.ethernet[i].Tx.IdleSwitch += (int) handlingRng.getNext();
									byteCounter++;
								}
								Integer Brd = Integer.valueOf(packetsBrd[i].getText()) + 1;
								packetsBrd[i].setText( Integer.toString(Brd) );
							}
						}
						mySwitch.ethernet[i].Tx.IdleSwitch--;
						byteCounter++;
					}
					//TRANSMITTING
					//System.out.println("TRANSMITTING");
					byteCounter = 0;
					while(byteCounter < INTERFACESPEED)
					{
						if(mySwitch.ethernet[i].Tx.Idle <= 0
								&& !mySwitch.ethernet[i].Tx.isEmpty())
						{
							Frame f = mySwitch.ethernet[i].Tx.pop();
							setStatus("Transmitting frame from interface " + f.getSource().getInterface() + " to " + f.getDestination().getInterface(), false);
							dataOut += f.getLength();
							mySwitch.ethernet[i].Tx.Idle = f.getLength();
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
					CAMTxt.setText("CAM TABLE (" + mySwitch.CAM.getSize() + " entries)");
					CAM.setText(mySwitch.CAM.listTable());
					
					double sumLst = 0.0;
					double sumTotal = 0.0;
					for(Integer i = 0; i < PORTNUMBER; i++)
					{
						sumLst += Integer.valueOf(packetsLst[i].getText());
						sumTotal += Integer.valueOf(packetsRx[i].getText());
					}
					Double losts = (sumLst / (sumTotal + sumLst)) * 100;
					packetsLstTotal.setText( "Total lost: " + Double.toString(Math.round(losts*10000)/10000.0d) + "%");
					dataInbound.setText( "Data in: " + Double.toString(Math.round(dataIn / 1024 / 1024 *10000)/10000.0d) + " Mb");
					dataServed.setText( "Data out: " + Double.toString(Math.round(dataOut / 1024 / 1024 *10000)/10000.0d) + " Mb");
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
			CAMTxt.setText("CAM TABLE (" + mySwitch.CAM.getSize() + " entries)");
			
			double sumLst = 0.0;
			double sumTx = 0.0;
			double sumTotal = 0.0;
			double sumBr = 0.0;
			for(Integer i = 0; i < PORTNUMBER; i++)
			{
				sumLst += Integer.valueOf(packetsLst[i].getText());
				sumTx += Integer.valueOf(packetsTx[i].getText());
				sumTotal += Integer.valueOf(packetsRx[i].getText());
				sumBr += Integer.valueOf(packetsBrd[i].getText());
			}
			//System.out.println(sumLst + " " + sumTotal + "\n");
			Double losts = (sumLst / (sumTotal + sumLst)) * 100;
			if(losts > 100.0) losts = 100.0;
			DataInVal += Math.round( (dataIn / 1024 / 1024) *10000)/10000.0d;
			DataOutVal += Math.round( (dataOut / 1024 / 1024) *10000)/10000.0d;
			packetsLstTotal.setText( "Total lost: " + Double.toString(Math.round(losts*10000)/10000.0d) + "%");
			dataInbound.setText( "Data in: " + Double.toString(DataInVal) + " Mb");
			dataServed.setText( "Data out: " + Double.toString(DataOutVal) + " Mb");
			Buffer.setText("Rx:\n"+ mySwitch.ethernet[BufferSelect.getSelectedIndex() ].Rx.getString() + "\nTx:\n" + mySwitch.ethernet[ BufferSelect.getSelectedIndex() ].Tx.getString() );
			
			plotData_CAMTTL.addElement((double) mySwitch.CAM.defaultValidity);
			plotData_Broadcast.addElement((double) broadcastTreshold);
			plotData_Received.addElement(sumTotal);
			plotData_Transmitted.addElement(sumTx);
			plotData_Losts.addElement(losts);
			plotData_BroadcastTx.addElement(sumBr);
			plotData_Intensity.addElement(generatorParam1);
			plotData_TrafficIn.addElement(DataInVal);
			plotData_TrafficOut.addElement(DataOutVal);
			
			//this.interrupt();
			return;
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
		        broadcastTreshold = Integer.parseInt( broadcast.getText() );
		        MACTimeToLive = Integer.parseInt(MAC_TTL.getText());
		        INTERFACESPEED = Integer.parseInt(speed.getText());
		        generatorParam1 = Double.parseDouble(rngParam1.getText());
		        if(rngSelected != RNGTYPE.EXP)
		        {
		        	generatorParam2 = Double.parseDouble(rngParam2.getText());
		        }
		    } 
			catch (NumberFormatException nfe) {
				setStatus("Invalid input - not a number", true);
		        return;
		    }
			if( d <= 0 || d2 < 0 || MACTimeToLive < 0 || INTERFACESPEED < 0)
			{
				setStatus("Invalid input - must be greater than 0", true);
				return;
			}
			if(broadcastTreshold > 100 || broadcastTreshold < 0)
			{
				setStatus("Invalid input - broadcast treshold must be % (0-100)", true);
				return;
			}
			if( mySwitch.getNumberOfActiveInterfaces() < 2 )
			{
				setStatus("Cannot transmit - at least two interfaces must be up", true);
				return;
			}
			
			mySwitch.CAM.setDefaultValidity(MACTimeToLive);
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
			DataInVal = 0.0;
			DataOutVal = 0.0;
			packetsLstTotal.setText("Total lost: 0.0%");
			dataInbound.setText("Data in: 0 Mb");
			dataServed.setText("Data out: 0 Mb");
			setStatus("Statistics cleared", false);
		}
		if(e.getSource() == plotClear)
		{
			plotData_CAMTTL.clear();
			plotData_Broadcast.clear();
			plotData_Received.clear();
			plotData_Transmitted.clear();
			plotData_Losts.clear();
			plotData_Intensity.clear();
			plotData_TrafficIn.clear();
			plotData_TrafficOut.clear();
			setStatus("Chart data has been cleared", false);
		}
		if(e.getSource() == buttonFlushCAM)
		{
			mySwitch.CAM.flush();
			CAMTxt.setText("CAM TABLE");
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
				rngParam1.setText("Min");
				rngParam2.setEnabled(true);
				rngParam2.setText("Max");
			}
			if(rngType.getSelectedIndex() == 1)
			{
				rngSelected = RNGTYPE.EXP;
				setStatus("Random number generator switched to exponential distribution", false);
				rngParam1.setText("Lambda");
				rngParam2.setEnabled(false);
				rngParam2.setText("N/A");
			}
			if(rngType.getSelectedIndex() == 2)
			{
				rngSelected = RNGTYPE.NORMAL;
				setStatus("Random number generator switched to normal (Gauss) distribution", false);
				rngParam1.setText("Mean");
				rngParam2.setEnabled(true);
				rngParam2.setText("Deviation");
			}
			if(rngType.getSelectedIndex() == 3)
			{
				rngSelected = RNGTYPE.CONST;
				setStatus("Random number generator switched to constant value", false);
				rngParam1.setText("Mean");
				rngParam2.setEnabled(false);		
				rngParam2.setText("N/A");
			}
		}
		if(e.getSource() == handling)
		{
			handlingMode = handling.getSelectedIndex();
			if(handlingMode == 5) handlingRng = new Exponential(0.001);
			else if(handlingMode == 6) handlingRng = new Exponential(0.01);
			else if(handlingMode == 7) handlingRng = new Exponential(0.1);
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
		if(e.getSource() == flooding)
		{
			MACFlooding = flooding.isSelected();
			if(MACFlooding)
			{
				mySwitch.ethernet[0].setState(State.UP);
	        	ethernet[0].setSelected(true);
	        	picPort[0].setIcon(portON);
				setStatus("eth0 will flood network with false MACs", false);
			}
			else
			{
				setStatus("eth0 will act normally", false);
			}
		}
		if(e.getSource() == switchingMode_CT)
		{
			CutThrough = true;
			setStatus("Switching mode set to cut-trough", false);
			frameLengthVary.setEnabled(true);
			for(int i = 0; i > PORTNUMBER; i++)
			{
				mySwitch.ethernet[i].Rx.Idle = 0;
				mySwitch.ethernet[i].Tx.IdleSwitch = 0;
				mySwitch.ethernet[i].Tx.Idle = 0;
			}
		}
		if(e.getSource() == switchingMode_SF)
		{
			CutThrough = false;
			setStatus("Switching mode set to store&forward", false);
			frameFixedSize = true;
			frameLengthFixed.setSelected(true);
			frameLengthVary.setEnabled(false);
			for(int i = 0; i > PORTNUMBER; i++)
			{
				mySwitch.ethernet[i].Rx.Idle = 0;
				mySwitch.ethernet[i].Tx.IdleSwitch = 0;
				mySwitch.ethernet[i].Tx.Idle = 0;
			}
		}
		if(e.getSource() == forcePush)
		{
			if(forceBroadcastPush)
			{
				forceBroadcastPush = false;
				setStatus("Broadcast frames will be pushed only if all interfaces engress is not full", false);
			}
			else
			{
				forceBroadcastPush = true;
				setStatus("Broadcast frames will be pushed even if interfaces engress is full", false);
			}
		}
		if(e.getSource() == plotMake)
		{
			if(plotData_Transmitted.isEmpty())
			{
				setStatus("Nothing to plot (yet)", true);
				return;
			}
			Vector<Double> DataX;
			Vector<Double> DataY;
			switch(plotTypeX.getSelectedIndex())
			{
				case 0 : DataX = plotData_CAMTTL;
						 break;
				case 1 : DataX = plotData_Broadcast;
				 		 break;
				case 2 : DataX = plotData_Received;
						 break;
				case 3 : DataX = plotData_Transmitted;
				 		 break;
				case 4 : DataX = plotData_Losts;
				 		 break;
				case 5 : DataX = plotData_BroadcastTx;
				 		 break;
				case 6 : DataX = plotData_Intensity;
						 break;
				case 7 : DataX = plotData_TrafficIn;
				 		 break;
				case 8 : DataX = plotData_TrafficOut;
				 		 break;
				default: return;
			}
			switch(plotTypeY.getSelectedIndex())
			{
				case 0 : DataY = plotData_CAMTTL;
						 break;
				case 1 : DataY = plotData_Broadcast;
				 		 break;
				case 2 : DataY = plotData_Received;
						 break;
				case 3 : DataY = plotData_Transmitted;
				 		 break;
				case 4 : DataY = plotData_Losts;
				 		 break;
				case 5 : DataY = plotData_BroadcastTx;
				 		 break;
				case 6 : DataY = plotData_Intensity;
						 break;
				case 7 : DataY = plotData_TrafficIn;
				 		 break;
				case 8 : DataY = plotData_TrafficOut;
				 		 break;
				default: return;
			}
			new plot.XYLineChart(DataX, DataY,
					plotTypeY.getSelectedItem().toString() + " = f(" + plotTypeX.getSelectedItem().toString() +")",
					plotTypeX.getSelectedItem().toString(), plotTypeY.getSelectedItem().toString() );
			
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
		iterations.setEnabled(true);
		buttonStop.setEnabled(false);
		buttonClr.setEnabled(true);
		buttonFlushCAM.setEnabled(true);
		buttonClearBuffers.setEnabled(true);
		speed.setEnabled(true);
		plotTypeX.setEnabled(true);
		plotTypeY.setEnabled(true);
		plotMake.setEnabled(true);
		plotClear.setEnabled(true);
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(true);
		frameMinDelay.setEnabled(true);
		rngType.setEnabled(true);
		frameLengthFixed.setEnabled(true);
		if(CutThrough) frameLengthVary.setEnabled(true);
		handling.setEnabled(true);
		broadcast.setEnabled(true);
		forcePush.setEnabled(true);
		MAC_TTL.setEnabled(true);
		flooding.setEnabled(true);
		rngParam1.setEnabled(true);
		if(rngSelected != RNGTYPE.EXP) rngParam2.setEnabled(true);
		switchingMode_CT.setEnabled(true);
		switchingMode_SF.setEnabled(true);
	}
	
	public void disableGUI() //Before simulation
	{
		setStatus("Disabling user interface", false);
		buttonRun.setEnabled(false);
		iterations.setEnabled(false);
		buttonStop.setEnabled(true);
		buttonClr.setEnabled(false);
		buttonFlushCAM.setEnabled(false);
		buttonClearBuffers.setEnabled(false);
		speed.setEnabled(false);
		plotTypeX.setEnabled(false);
		plotTypeY.setEnabled(false);
		plotMake.setEnabled(false);
		plotClear.setEnabled(false);
		for(Integer i = 0; i < PORTNUMBER; i++) ethernet[i].setEnabled(false);
		frameMinDelay.setEnabled(false);
		rngType.setEnabled(false);
		frameLengthFixed.setEnabled(false);
		frameLengthVary.setEnabled(false);
		handling.setEnabled(false);
		broadcast.setEnabled(false);
		forcePush.setEnabled(false);
		MAC_TTL.setEnabled(false);
		flooding.setEnabled(false);
		rngParam1.setEnabled(false);
		rngParam2.setEnabled(false);
		switchingMode_CT.setEnabled(false);
		switchingMode_SF.setEnabled(false);
	}
	
	public void setStatus(String msg, Boolean isError)
	{
		if(isError) status.setForeground(Color.RED);
		else status.setForeground(Color.BLACK);
		status.setText("Status: " + msg);
		System.out.println(status.getText());
	}
}
