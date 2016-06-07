package controller;


import java.util.LinkedList;
import java.util.NoSuchElementException;

import carControl.MotorControl;
import carControl.SteeringControl;
import data.TrackCorner;
import data.TrackData;
import data.TrackStraight;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import sensors.RGBControl;
import sensors.TouchControl;
import sensors.UltrasonicControl;
import controller.TrackControl;

public class MainControl {
	public SteeringControl steeringControl;
	public MotorControl motorControl;
	public TrackControl trackControl;
	public RGBControl rgbControlLeft;
	public RGBControl rgbControlRight;
	public TouchControl touchControl;
	public UltrasonicControl ultrasonicControl;
	
	
	// DEBUG
	public IntroductionLap l = new IntroductionLap(motorControl, steeringControl, rgbControlLeft, rgbControlRight);
	//
	

	private int default_speed = 200; // default speed for car in RACE
	private int straight_default_speed = 100000;
	private boolean inRace = false;
	//TrackCorner currentTrackCorner;
	//TrackStraight currentTrackStraight;

	int left = 0;
	int right = 1;

	public MainControl() {
		initialize();
	}

	public void initialize() {
		steeringControl = new SteeringControl(false);
		motorControl = new MotorControl();
		//trackControl = new TrackControl();
		rgbControlLeft = new RGBControl(this, SensorPort.S4);
		rgbControlRight = new RGBControl(this, SensorPort.S1);
		touchControl = new TouchControl(this);
		ultrasonicControl = new UltrasonicControl(this);

		//steeringControl.start();
		//rgbControlLeft.start();
		//rgbControlRight.start();
		//touchControl.start();
		//ultrasonicControl.start();

	}
	
	public void initTrackControl(LinkedList<TrackData> sections){
		this.trackControl = new TrackControl(sections);
	}
	
//	public void startIntroductionLap() {
//		IntroductionLap l = new IntroductionLap(motorControl, steeringControl, rgbControlLeft, rgbControlRight);
//		try {
//			l.startLap();
//		}
//		catch (Exception e) {
//			LCD.drawString("Exception: " + e.getMessage(), 0, 2);
//		}
//	}
	
	public void startRacer() throws IllegalStartPositionException {
		LCD.drawString("Racer racing", 0, 1);		

		// If one of the two color sensors isn't black -> car is not in the right position
		if ((rgbControlLeft.getColor() != RGBControl.BLACK ) || (rgbControlRight.getColor() != RGBControl.BLACK)) {
			throw new IllegalStartPositionException("IllExcp StartPosition");
		}
		
		// start		
		l.accelerateTo(default_speed);
		LCD.drawString("accelerated", 0, 1);
		
		
		while (true) {
			try {
				//nächster Abschnitt gefunden
				if ((rgbControlLeft.getColor() == RGBControl.GREEN ) || (rgbControlRight.getColor() == RGBControl.GREEN)) {
					LCD.drawString("ERkannt", 0, 7);
					nextSection();
				}
				//Ziellinie überquert
				else if ((rgbControlLeft.getColor() == RGBControl.RED ) || (rgbControlRight.getColor() == RGBControl.RED)) {
					if(!inRace){
						inRace = true;
						nextSection();
					}
					else{
						l.stopMotors();
						inRace = false;
					}					
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cornerDetected(TrackCorner currentTrackCorner, int correction) {
		//currentTrackCorner = TrackControl.getCurrentTrackCorner();
		LCD.drawString("Kurve:" + String.valueOf(currentTrackCorner.getDirection()), 0, 4);
		if (currentTrackCorner.getDirection() == left) {
			l.steerLeft(currentTrackCorner.getSteeringRate() + correction);
		} else if (currentTrackCorner.getDirection() == right) {
			l.steerRight(currentTrackCorner.getSteeringRate() + correction);
		} else {
			l.stopMotors();
			LCD.clear();
			LCD.drawString("FAILURE", 0, 0);
			LCD.drawString("Wrong Corner Direction", 0, 1);
			LCD.drawString("Return Value: " + currentTrackCorner.getDirection(), 0, 2);
		}
	}
	
	public void straightDetected(TrackStraight currentTrackStraight,int correction) {
		//currentTrackStraight = TrackControl.getCurrentTrackStraight();
		l.steerStraight();
		l.accelerateTo(default_speed);
		
		if(correction != 0){
			correctDirection(correction);
		}
		
	}
	
	public void correctDirection(int correction){
		
	}
	
	public void nextSection(){
		try{
			TrackData currentElement = trackControl.getNextTrackSection();
			if(currentElement instanceof TrackCorner){
				//cast ok, weil typ gecheckt -> cornerDetected erwartet TrackCorner und correction
				cornerDetected((TrackCorner)currentElement, 0);
			}
			else if(currentElement instanceof TrackStraight){
				//cast ok, weil typ gecheckt
				straightDetected((TrackStraight)currentElement, 0);
			}
			else{
				//unbekanntesObject in Liste
			}			
		}
		catch(NoSuchElementException e){
			//TrackListe ist leer aber Track ist nicht zu Ende?
		}
		catch(Exception e){
			//unbekannter Fehler
		}
	}
	

}
