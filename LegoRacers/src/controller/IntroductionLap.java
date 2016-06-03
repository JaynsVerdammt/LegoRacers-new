package controller;

import java.util.LinkedList;

import carControl.MotorControl;
import carControl.SteeringControl;
import data.TrackCorner;
import data.TrackData;
import data.TrackStraight;
import lejos.nxt.Button;
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
	
	private int corner_start = -1;
	private int corner_end = -1;
	
	private static final int BEFORE_C_LAP = 0;
	private static final int AFTER_C_LAP = 1;
	private static final int GERADE = 2;
	private static final int KURVE = 3;
	private int abschnitt = BEFORE_C_LAP;
	//private int abschnitt = GERADE;
	
	// indicates if the car is currently on a line
	private boolean isOnLine = false;
	// indicates if the car is currently recording a section
	private boolean inSection = false;
	// current Kurve and Gerade
	private TrackStraight currentGerade;
	private TrackCorner currentKurve;
	
	
	public LinkedList<TrackData> sections = new LinkedList<TrackData>();
	
	// Parameters
	private int default_radius_curve = 40; // in degree
	private int default_delay_before_turning = 2; // turnings of wheel
	private int default_speed = 150; // default speed for car in introduction lap
	
	/* ------------------------------------------------ */
	/* ------------------------------------------------ */
	/* DEBUG */
	/* ------------------------------------------------ */
	/* ------------------------------------------------ */
	private void accelerateTo(int speed) {
		if (speed < 0) {
			Motor.A.forward();
			Motor.C.forward();
			Motor.A.setSpeed(-speed);
			Motor.C.setSpeed(-speed);
		}
		else {
			Motor.A.backward();
			Motor.C.backward();
			Motor.A.setSpeed(speed);
			Motor.C.setSpeed(speed);
		}
	}
	
	private void steerLeft(int degree) {
		Motor.B.rotateTo(degree);
	}
	
	private void steerRight(int degree) {
		Motor.B.rotateTo(-degree);
	}
	
	private void steerLeftFor(int degree) {
		Motor.B.rotate(-degree);
	}
	
	private void steerRightFor(int degree) {
		Motor.B.rotate(degree);
	}
	
	private void steerStraight() {
		Motor.B.rotateTo(0);
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
		
		// If one of the two color sensors isn't black -> car is not in the right position
		if ((RGBLeft.getColor() != RGBControl.BLACK ) || (RGBRight.getColor() != RGBControl.BLACK)) {
			throw new IllegalStartPositionException("IllExcp");
		}
		lastColorLeft = RGBLeft.getColor();
		lastColorRight = RGBRight.getColor();
		
		// start
		corner_start = RGBControl.GREEN;
		corner_end = RGBControl.GREEN;
		accelerateTo(default_speed);
		
		while (true) {
			try {
				colorLeft = RGBLeft.getColor();
				colorRight = RGBRight.getColor();
				if (colorLeft != lastColorLeft || colorRight != lastColorRight) {
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
				}
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
		if (!isOnLine && (colorLeft == RGBControl.RED || colorRight == RGBControl.RED)) {
			isOnLine = true;
		}
		else if (isOnLine && colorLeft != RGBControl.RED && colorRight != RGBControl.RED) {
			abschnitt = GERADE;
			startGerade();
		}
	}
	
	private void after_c_lap(int colorLeft, int colorRight) {
		//motorControl.brakeTo(0);
		stop();
		LCD.drawString("Fertig", 0, 6);
		sleep(10000);
	}
	
	private void gerade(int colorLeft, int colorRight) throws UnknownStateException {
		if (colorLeft == RGBControl.WHITE) {
			//stop();
			correctToRight();
			//accelerateTo(default_speed);
		}
		else if (colorRight == RGBControl.WHITE) {
			correctToLeft();
		}
		// farbe der kurvenlinien noch nicht initialisiert
		else if (corner_start < 0) {
			if (colorLeft == RGBControl.GREEN || colorRight == RGBControl.GREEN) {
				corner_start = RGBControl.GREEN;
				corner_end = RGBControl.BLUE;
				LCD.drawString("START GREEN", 0, 1);
				LCD.drawString("END BLUE", 0, 2);
				endGerade();
				abschnitt = KURVE;
				startKurve();
			}
			else if (colorLeft == RGBControl.BLUE || colorRight == RGBControl.BLUE) {
				corner_start = RGBControl.BLUE;
				corner_end = RGBControl.GREEN;
				LCD.drawString("START BLUE", 0, 1);
				LCD.drawString("END GREEN", 0, 2);
				endGerade();
				abschnitt = KURVE;
				startKurve();
			}
		}
		// Kurve startet
		else if (colorLeft == corner_start || colorRight == corner_start) {
			endGerade();
			abschnitt = KURVE;
			startKurve();
		}
		// Ende erreicht
		else if (colorLeft == RGBControl.RED || colorRight == RGBControl.RED) {
			stop();
			abschnitt = AFTER_C_LAP;
			LCD.drawString("ENDEE", 0, 5);
		}
		// beides schwarz, normal auf Strecke
		else {
			// do nothing
		}
		
		/*
		
		if (isOnLine) {
			if (colorLeft == corner_start || colorRight == corner_start) {
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
			else if (colorLeft == corner_start || colorRight == corner_start) {
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
		if (colorLeft == RGBControl.WHITE) {
			//stop();
			correctToRight();
			//accelerateTo(default_speed);
		}
		else if (colorRight == RGBControl.WHITE) {
			correctToLeft();
		}
		// Kurve endet
		/*else if (colorLeft == corner_start || colorRight == corner_start) {
			LCD.drawString("finished", 0, 4);
			endKurve();
			abschnitt = GERADE;
			startGerade();
		}*/
		else if (colorLeft == corner_end) {
			endKurve(TrackCorner.LEFT);
			abschnitt = GERADE;
			startGerade();
		}
		else if (colorRight == corner_end) {
			endKurve(TrackCorner.RIGHT);
			abschnitt = GERADE;
			startGerade();
		}
		
		
		/*if (isOnLine) {
			if (colorLeft == corner_start || colorRight == corner_start) {
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
					steerRight(5);
					accelerateTo(default_speed);
				}
				else {
					// drive backwards three turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5); // add 5 degree to radius
					steerRight(5);
					accelerateTo(default_speed);
				}
			}
			else if (colorLeft == RGBControl.BLACK && colorRight == RGBControl.WHITE) {
				if (currentKurve.getDirection() == TrackCorner.RIGHT) {
					// drive backwards one turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-5); // sub 5 degree to radius
					steerLeft(5);
					accelerateTo(default_speed);
				}
				else {
					// drive backwards three turning
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5); // add 5 degree to radius
					steerLeft(5);
					accelerateTo(default_speed);
				}
			}
			else if (colorLeft == corner_start && colorRight == corner_start) {
				isOnLine = true;
			}
			else if (colorLeft == corner_start) {
				// drive backwards 3 turns
				if (currentKurve.getDirection() == TrackCorner.LEFT) {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+2);
				}
				else {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-2);
				}
				steerLeft(2);
				accelerateTo(default_speed);
			}
			else if (colorRight == corner_start) {
				// drive backwards 3 turns
				if (currentKurve.getDirection() == TrackCorner.LEFT) {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()-2);
				}
				else {
					currentKurve.setSteeringRate(currentKurve.getSteeringRate()+2);
				}
				steerRight(2);
				accelerateTo(default_speed);
			}
			else {
				throw new UnknownStateException();
			}
		}*/
	}
	
	private void startGerade() {
		LCD.drawString("Gerad", 0, 7);
		currentGerade = new TrackStraight();
		inSection = true;
	}
	
	private void endGerade() {
		sections.add(currentGerade);
		currentGerade = null;
		inSection = false;
	}
	
	private void startKurve() {
		LCD.drawString("Kurve", 0, 7);
		currentKurve = new TrackCorner();
		currentKurve.setDirection(calcDirection());
		//driveForward(currentKurve.getDelay); // turns
		if (currentKurve.getDirection() == TrackCorner.LEFT) {
			steerLeft(currentKurve.getSteeringRate());
		}
		else {
			steerRight(currentKurve.getSteeringRate());
		}
		accelerateTo(default_speed);
		inSection = true;
	}
	
	private void endKurve(int markedSensor) { // LEFT oder RIGHT
		/*long timeStart = System.currentTimeMillis();
		long stopTime;
		if (markedSensor == TrackCorner.LEFT) {
			while (RGBRight.getColor() != corner_end) {}
			stopTime = System.currentTimeMillis();
		}
		else {
			while (RGBLeft.getColor() != corner_end) {}
			stopTime = System.currentTimeMillis();
		}
		*/
		
		
		sleep(400);
		steerStraight();
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
					direction = TrackCorner.LEFT;
					// drive backwards
				}
				else {
					// do nothing
				}
			}
			else {
				stop();
				sleep(500);
				accelerateTo(-default_speed);
				// solange keiner von beiden grün ist, rückwärts fahren
				while (RGBRight.getColor() != corner_start && RGBLeft.getColor() != corner_start) {
					
				}
				stop();
				sleep(500);
				accelerateTo(default_speed);
				while (RGBRight.getColor() != corner_start && RGBLeft.getColor() != corner_start) {
					
				}				
				return direction;
				/*
				if (RGBLeft.getColor() == corner_start || RGBRight.getColor() == corner_start) {
					accelerateTo(default_speed);
					return direction;
				}*/
			}
		}
	}
	
	private void correctToLeft() {
		stop();
		sleep(500);
		accelerateTo(-default_speed);
		// solange einer von beiden nicht schwarz ist, zurückfahren
		while (RGBRight.getColor() != RGBControl.BLACK || RGBLeft.getColor() != RGBControl.BLACK) {
			
		}
		sleep(400);
		stop();
		accelerateTo(default_speed);
		steerLeftFor(20);
		if (abschnitt == KURVE) {
			if (currentKurve.getDirection() == TrackCorner.LEFT) {
				currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5);
			}
			else {
				currentKurve.setSteeringRate(currentKurve.getSteeringRate()-5);
			}
		}
	}
	
	private void correctToRight() {
		stop();
		sleep(500);
		accelerateTo(-default_speed);
		// solange einer von beiden nicht schwarz ist, zurückfahren
		while (RGBRight.getColor() != RGBControl.BLACK || RGBLeft.getColor() != RGBControl.BLACK) {
			
		}
		sleep(400);
		stop();
		accelerateTo(default_speed);
		steerRightFor(20);
		if (abschnitt == KURVE) {
			if (currentKurve.getDirection() == TrackCorner.LEFT) {
				currentKurve.setSteeringRate(currentKurve.getSteeringRate()-5);
			}
			else {
				currentKurve.setSteeringRate(currentKurve.getSteeringRate()+5);
			}
		}
	}
}
