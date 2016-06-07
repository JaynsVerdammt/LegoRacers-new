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
import lejos.nxt.Sound;
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
	
	
	private int straightLeftDirection = -1;
	private long dTime = 0;
	private long cATime = 0;
	private long cETime = 0;
	private long cDTime = 0;
	private boolean stillInCorner = false;
	
	
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
	public void accelerateTo(int speed) {
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
	
	public void steerLeft(int degree) {
		Motor.B.rotateTo(-degree);
	}
	
	public void steerRight(int degree) {
		Motor.B.rotateTo(degree);
	}
	
	public void steerLeftFor(int degree) {
		Motor.B.rotate(-degree);
	}
	
	public void steerRightFor(int degree) {
		Motor.B.rotate(degree);
	}
	
	public void steerStraight() {
		Motor.B.rotateTo(0);
	}
	
	public void stop() {
		Motor.A.stop();
		Motor.C.stop();
	}
	
	public void stopMotors() {
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
		
		// Tracks f¸r Lara
		/*
		sections.add(new TrackStraight());
		sections.add(new TrackCorner(TrackCorner.RIGHT, 40));
		sections.add(new TrackStraight());
		sections.add(new TrackCorner(TrackCorner.RIGHT, 30));
		sections.add(new TrackStraight());
		sections.add(new TrackCorner(TrackCorner.RIGHT, 50));
		sections.add(new TrackStraight());
		sections.add(new TrackCorner(TrackCorner.LEFT, 30));
		sections.add(new TrackStraight());
		sections.add(new TrackCorner(TrackCorner.RIGHT, 40));
		sections.add(new TrackStraight());
		*/
		
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
		//corner_start = RGBControl.GREEN;
		//corner_end = RGBControl.GREEN;
		accelerateTo(default_speed);
		
		while (true) {
			try {
				colorLeft = RGBLeft.getColor();
				colorRight = RGBRight.getColor();
				
				// correction nach der Kurve
				if (stillInCorner && (System.currentTimeMillis() - cATime) > 400) {
					steerStraight();
					stillInCorner = false;
					LCD.drawString("Correct", 0, 4);
				}
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
		LCD.drawString("Fertig", 0, 0);
		sleep(2000);
		LCD.clearDisplay();
		for(int i=3;i<sections.size();i++) {
			TrackData t = sections.get(i);
			if (t instanceof TrackCorner) {
				TrackCorner c = (TrackCorner)t;
				LCD.drawString(c.getDirection() + " - " + String.valueOf(c.getSteeringRate()), 0, i-3);
			}
			else {
				LCD.drawString("Gerade", 0, i-3);
			}
		}
	}
	
	private void gerade(int colorLeft, int colorRight) throws UnknownStateException {
		if (colorLeft == RGBControl.WHITE && colorRight != corner_start) {// && (System.currentTimeMillis() - dTime > 400)) {
			// Kurve nicht mehr korrigieren
			stillInCorner = false;
			//Sound.twoBeeps();
			correctToRightStraight();
			straightLeftDirection = TrackCorner.LEFT;
			//accelerateTo(default_speed);
		}
		else if (colorRight == RGBControl.WHITE && colorLeft != corner_start) {// && (System.currentTimeMillis() - dTime > 400)) {
			// Kurve nicht mehr korrigieren
			stillInCorner = false;
			//Sound.twoBeeps();
			correctToLeftStraight();
			straightLeftDirection = TrackCorner.RIGHT;
		}
		// beides weiﬂ
		else if (  (colorLeft == RGBControl.WHITE && colorRight == RGBControl.WHITE)
				|| (colorLeft == corner_start && colorRight == RGBControl.WHITE)
				|| (colorRight == corner_start && colorLeft == RGBControl.WHITE)) {
			// Kurve nicht mehr korrigieren
			stillInCorner = false;
			stop();
			sleep(3000);
			accelerateTo(-default_speed);
			LCD.drawString("ACHtung Schleife", 0, 6);
			while(RGBLeft.getColor() != RGBControl.BLACK || RGBRight.getColor() != RGBControl.BLACK) {}
			if (straightLeftDirection == TrackCorner.LEFT) {
				steerLeftFor(20);
			}
			else {
				steerRightFor(20);
			}
			sleep(300);
			stop();
			//Sound.beepSequence();
			sleep(3000);
			steerStraight();
			accelerateTo(default_speed);
			/*if (straightLeftDirection == TrackCorner.LEFT) {
				steerRightFor(20);
			}
			else {
				steerLeftFor(20);
			}*/
			
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
			// Kurve nicht mehr korrigieren
			stillInCorner = false;
			endGerade();
			abschnitt = KURVE;
			startKurve();
		}
		// Ende erreicht
		else if (colorLeft == RGBControl.RED || colorRight == RGBControl.RED) {
			// Kurve nicht mehr korrigieren
			stillInCorner = false;
			stop();
			abschnitt = AFTER_C_LAP;
			LCD.drawString("ENDEE", 0, 5);
		}
		// beides schwarz, normal auf Strecke
		else {
			if (!stillInCorner) {
				steerStraight();
			}
		}
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
		else if (colorLeft == corner_end && currentKurve.getDirection() == TrackCorner.RIGHT) {
			endKurve();
			abschnitt = GERADE;
			startGerade();
		}
		else if (colorRight == corner_end && currentKurve.getDirection() == TrackCorner.LEFT) {
			endKurve();
			abschnitt = GERADE;
			startGerade();
		}
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
		//Sound.buzz();
		cATime = System.currentTimeMillis();
		LCD.drawString("Kurve", 0, 7);
		currentKurve = new TrackCorner();
		currentKurve.setDirection(calcDirection());
		//driveForward(currentKurve.getDelay); // turns
		if (currentKurve.getDirection() == TrackCorner.LEFT) {
			steerLeft(currentKurve.getSteeringRate());
			LCD.drawString("LEFT ", 0, 6);
			LCD.drawString(String.valueOf(currentKurve.getSteeringRate()), 0, 2);
		}
		else {
			steerRight(currentKurve.getSteeringRate());
			LCD.drawString("RIGHT", 0, 6);
			LCD.drawString(String.valueOf(currentKurve.getSteeringRate()), 0, 2);
		}
		//accelerateTo(default_speed);
		inSection = true;
	}
	
	private void endKurve() { // LEFT oder RIGHT
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
		//sleep(400);
		//steerStraight();
		cATime = System.currentTimeMillis();
		stillInCorner = true;
		sections.add(currentKurve);
		currentKurve = null;
		inSection = false;
		//Sound.buzz();
		//Sound.buzz();
	}
	
	private int calcDirection() {
		int direction = -1;
		while(true) {
			if (direction < 0) {
				if (System.currentTimeMillis() - cDTime < 300) {
					// Innere weiﬂe Fl‰che der Kurve erwischt, da das ‰uﬂere noch nicht
					// erreicht sein kann
					if (RGBLeft.getColor() == RGBControl.WHITE) {
						stop();
						steerLeft(20);
						sleep(200);
						accelerateTo(-default_speed);
						sleep(300);
						stop();
						steerStraight();
						sleep(200);
						accelerateTo(default_speed);
					}
					else if (RGBRight.getColor() == RGBControl.WHITE) {
						stop();
						steerRight(20);
						sleep(200);
						accelerateTo(-default_speed);
						sleep(300);
						stop();
						steerStraight();
						sleep(200);
						accelerateTo(default_speed);
					}
				}
				else {
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
			}
			else {
				cETime = System.currentTimeMillis();
				stop();
				sleep(200);
				accelerateTo(-default_speed);
				sleep(((int)(cETime-cATime)/2));
				// solange keiner von beiden gr¸n ist, r¸ckw‰rts fahren
				//while (RGBRight.getColor() != corner_start && RGBLeft.getColor() != corner_start) {}
				stop();
				sleep(200);
				accelerateTo(default_speed);
				//while (RGBRight.getColor() != corner_start && RGBLeft.getColor() != corner_start) {}				
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
		sleep(400);
		accelerateTo(-default_speed);
		// solange einer von beiden nicht schwarz ist, zur¸ckfahren
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
		// solange einer von beiden nicht schwarz ist, zur¸ckfahren
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
	private void correctToLeftStraight() {
		steerLeftFor(40);
		dTime = System.currentTimeMillis();
		LCD.drawString("Correction to left", 0, 5);
	}
	
	private void correctToRightStraight() {
		steerRightFor(40);
		dTime = System.currentTimeMillis();
		LCD.drawString("Correction to right", 0, 5);
	}

}
