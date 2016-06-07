package controller;

import java.util.LinkedList;

import data.TrackData;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.LCPBTResponder;

public class startUp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sound.setVolume(100);
		MainControl m = new MainControl();
		m.initialize();
		LCD.drawString("MainControl initialized", 0, 0);
		Sound.buzz();
		Button.waitForAnyPress();
		//m.startIntroductionLap();
		
		IntroductionLap intrLap = new IntroductionLap(m.motorControl, m.steeringControl, m.rgbControlLeft, m.rgbControlRight);
		try {
			intrLap.startLap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LCD.drawString("Error: IntroductionLap", 0, 0);
			LCD.drawString(e.getMessage(), 0, 1);
		}
		
		LCD.drawString("Press start now", 0, 1);
		Button.waitForAnyPress();
		m.initTrackControl(intrLap.sections);
		try {
			m.startRacer();
		} catch (IllegalStartPositionException e) {
			// TODO Auto-generated catch block
			LCD.drawString("Error: RaceLap", 0, 0);
			LCD.drawString(e.getMessage(), 0, 1);
		}
	}

}
