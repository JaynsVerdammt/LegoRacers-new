package controller;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.LCPBTResponder;

public class startUp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LCPBTResponder lcpThread = new LCPBTResponder();
		lcpThread.setDaemon(true);
		lcpThread.start();
		
		MainControl m = new MainControl();
		m.initialize();
		LCD.drawString("MainControl initialized", 0, 0);
		Button.waitForAnyPress();
		m.startIntroductionLap();
	}

}
