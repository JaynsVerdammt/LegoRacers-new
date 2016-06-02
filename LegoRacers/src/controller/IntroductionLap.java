package controller;

import java.util.LinkedList;

import carControl.MotorControl;
import carControl.SteeringControl;
import data.TrackCorner;
import data.TrackData;
import data.TrackStraight;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import sensors.RGBControl;

public class IntroductionLap {

	private MotorControl motorControl;
	private SteeringControl steeringControl;
	private RGBControl RGBLeft;
	private RGBControl RGBRight;
	private int lastColorLeft;
	private int lastColorRight;
	
	private static final int BEFORE_C_LAP = 0;
	private static final int AFTER_C_LAP = 1;
	private static final int GERADE = 2;
	private static final int KURVE = 3;
	private int abschnitt = BEFORE_C_LAP;
	
	// indicates if the car is currently on a line
	private boolean isOnLine = false;
	// indicates if the car is currently recording a section
	private boolean inSection = false;
	// current Kurve and Gerade
	private TrackStraight currentGerade;
	private TrackCorner currentKurve;
	
	
	public LinkedList<TrackData> sections = new LinkedList<TrackData>();
	
	// Parameters
	private int default_radius_curve = 30; // in degree
	private int default_delay_before_turning = 2; // turnings of wheel
	private int default_speed = 200; // default speed for car in introduction lap
	
	/* ------------------------------------------------ */
	/* ------------------------------------------------ */
	/* DEBUG */
	/* ------------------------------------------------ */
	/* ------------------------------------------------ */
	private void accelerateTo(int speed) {
		Motor.A.setSpeed(speed);
		Motor.C.setSpeed(speed);
	}
	
	private void stop() {
		Motor.A.stop();
		Motor.C.stop();
	}
	
	public static void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/* ------------------------------------------------ */
	/* ------------------------------------------------ */
	
	public IntroductionLap(MotorControl motorControl, SteeringControl steeringControl, RGBControl left, RGBControl right) {
		this.steeringControl = steeringControl;
		this.motorControl = motorControl;
		this.RGBLeft = left;
		this.RGBRight = right;
		
		// DEBUG
		Motor.A.backward();
		Motor.C.backward();
	}
	
	public void startLap() throws IllegalStartPositionException {
		
		int colorLeft;
		int colorRight;
		
		LCD.drawString("startLap()", 0, 1);
		LCD.drawString(String.valueOf(RGBLeft.getColor()), 0, 4);
		LCD.drawString(String.valueOf(RGBRight.getColor()), 0, 5);
		LCD.drawString(String.valueOf(RGBControl.BLACK), 0, 6);
		
		// If one of the two color sensors isn't black -> car is not in the right position
		if ((RGBLeft.getColor() != RGBControl.BLACK ) || (RGBRight.getColor() != RGBControl.BLACK)) {
			throw new IllegalStartPositionException("IllExcp");
		}
		lastColorLeft = RGBLeft.getColor();
		lastColorRight = RGBRight.getColor();
		
		// start
		
		accelerateTo(default_speed);
		LCD.drawString("accelerated", 0, 1);
		
		while (true) {
			try {
				colorLeft = RGBLeft.getColor();
				colorRight = RGBRight.getColor();
				//if (colorLeft != lastColorLeft || colorRight != lastColorRight) {
					switch (abschnitt) {
						case BEFORE_C_LAP:	before_c_lap(colorLeft, colorRight);
											break;
						case AFTER_C_LAP:	after_c_lap(colorLeft, colorRight);
											break;
						case GERADE:		gerade(colorLeft, colorRight);
											break;
						case KURVE:			kurve(colorLeft, colorRight);
											break;
					}
				//}
				lastColorLeft = colorLeft;
				lastColorRight = colorRight;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Executed only after changes of the RGB-Sensors
	private void before_c_lap(int colorLeft, int colorRight) throws IllegalStartPositionException {
		if (colorLeft == RGBControl.RED || colorRight == RGBControl.RED) {
			abschnitt = GERADE;
		}
		/*
		if (!isOnLine && colorLeft != RGBControl.RED && colorRight != RGBControl.RED) {
			// unmögliche Startposition, da nicht als erstes die Start/Ziel-Linie überquert wird
			throw new IllegalStartPositionException("IllExcp");
		}
		else if (isOnLine && colorLeft == RGBControl.BLACK && colorRight == RGBControl.BLACK) {
			isOnLine = false;
			abschnitt = GERADE;
			startGerade();
		}
		else {
			isOnLine = true;
		}*/
	}
	
	private void after_c_lap(int colorLeft, int colorRight) {
		motorControl.brakeTo(0);
	}
	
	private void gerade(int colorLeft, int colorRight) throws UnknownStateException {
		
		if (colorLeft == RGBControl.WHITE || colorRight == RGBControl.WHITE) {
			stop();
			LCD.drawString("weiß", 0, 6);
			sleep(5000);
			accelerateTo(default_speed);
		}
		else if (colorLeft == RGBControl.GREEN || colorRight == RGBControl.GREEN) {
			stop();
			LCD.drawString("grün", 0, 6);
			sleep(5000);
			accelerateTo(default_speed);
		}
		/*
		
		if (isOnLine) {
			if (colorLeft == RGBControl.GREEN || colorRight == RGBControl.GREEN) {
				// do nothing
			}
			else {
				isOnLine = false;
				endGerade();
				abschnitt = KURVE;
				startKurve();
			}
		}
		else {
			if (colorLeft == RGBControl.BLACK && colorRight == RGBControl.BLACK) {
				// do nothing
			}
			else if (colorLeft == RGBControl.WHITE && colorRight == RGBControl.BLACK) {
				correctRight();
			}
			else if (colorLeft == RGBControl.BLACK && colorRight == RGBControl.WHITE) {
				correctLeft();
			}
			else if (colorLeft == RGBControl.GREEN || colorRight == RGBControl.GREEN) {
				isOnLine = true;
			}
			else if (colorLeft == RGBControl.RED || colorRight == RGBControl.RED) {
				isOnLine = true;
				abschnitt = AFTER_C_LAP;
			}
			else {
				throw new UnknownStateException();
			}
		}*/
	}
	
	private void kurve(int colorLeft, int colorRight) throws UnknownStateException {
		if (isOnLine) {
			if (colorLeft == RGBControl.GREEN || colorRight == RGBControl.GREEN) {
				// do nothing
			}
			else {
				isOnLine = false;
				endKurve();
				abschnitt = GERADE;
				startGerade();
			}
		}
		else {
			if (colorLeft == RGBControl.BLACK && colorRight == RGBControl.BLACK) {
				// do nothing
			}
			else if (colorLeft == RGBControl.WHITE && colorRight == RGBControl.BLACK) {
				if (currentKurve.getDirection() == TrackCorner.LEFT) {
					// drive backwards one turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-5); // sub 5 degree to radius
					steeringControl.steerRight(5);
					accelerateTo(default_speed);
				}
				else {
					// drive backwards three turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5); // add 5 degree to radius
					steeringControl.steerRight(5);
					accelerateTo(default_speed);
				}
			}
			else if (colorLeft == RGBControl.BLACK && colorRight == RGBControl.WHITE) {
				if (currentKurve.getDirection() == TrackCorner.RIGHT) {
					// drive backwards one turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-5); // sub 5 degree to radius
					steeringControl.steerLeft(5);
					accelerateTo(default_speed);
				}
				else {
					// drive backwards three turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5); // add 5 degree to radius
					steeringControl.steerLeft(5);
					accelerateTo(default_speed);
				}
			}
			else if (colorLeft == RGBControl.GREEN && colorRight == RGBControl.GREEN) {
				isOnLine = true;
			}
			else if (colorLeft == RGBControl.GREEN) {
				// drive backwards 3 turns
				if (currentKurve.getDirection() == TrackCorner.LEFT) {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+2);
				}
				else {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-2);
				}
				steeringControl.steerLeft(2);
				accelerateTo(default_speed);
			}
			else if (colorRight == RGBControl.GREEN) {
				// drive backwards 3 turns
				if (currentKurve.getDirection() == TrackCorner.LEFT) {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-2);
				}
				else {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+2);
				}
				steeringControl.steerRight(2);
				accelerateTo(default_speed);
			}
			else {
				throw new UnknownStateException();
			}
		}
	}
	
	private void startGerade() {
		currentGerade = new TrackStraight();
		inSection = true;
	}
	
	private void endGerade() {
		sections.add(currentGerade);
		currentGerade = null;
		inSection = false;
	}
	
	private void startKurve() {
		currentKurve = new TrackCorner();
		currentKurve.setDirection(calcDirection());
		//driveForward(currentKurve.getDelay); // turns
		if (currentKurve.getDirection() == TrackCorner.LEFT) {
			steeringControl.steerLeft(currentKurve.getSteeringRate());
		}
		else {
			steeringControl.steerLeft(currentKurve.getSteeringRate());
		}
		accelerateTo(default_speed);
		inSection = true;
	}
	
	private void endKurve() {
		steeringControl.steerStraight();
		sections.add(currentKurve);
		currentKurve = null;
		inSection = false;
	}
	
	private int calcDirection() {
		int direction = -1;
		while(true) {
			if (direction < 0) {
				if (RGBLeft.getColor() == RGBControl.WHITE) {
					direction = TrackCorner.RIGHT;
					// drive backwards
				}
				else if (RGBRight.getColor() == RGBControl.WHITE) {
					direction = TrackCorner.RIGHT;
					// drive backwards
				}
				else {
					// do nothing
				}
			}
			else {
				if (RGBLeft.getColor() == RGBControl.GREEN || RGBRight.getColor() == RGBControl.GREEN) {
					accelerateTo(default_speed);
					return direction;
				}
			}
		}
	}
	
	private void correctLeft() {
		
	}
	
	private void correctRight() {
		
	}
}
