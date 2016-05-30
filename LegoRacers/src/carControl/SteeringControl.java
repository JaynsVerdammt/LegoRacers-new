package carControl;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;

public class SteeringControl extends Thread {

	private int neutral;
	private int leftMax = 2000;
	private int rightMax = 2000;
	private int maxUnlimitedAngel = 60;
	private boolean limited;
	private int correction = 0;
	private int newDegree;
	
	private NXTRegulatedMotor steeringMotor = Motor.B;

	public SteeringControl(boolean limited) {
		this.limited = limited;
	}

	public void run() {
		calibrateSteering();
		while(true){
		steeringMotor.rotateTo(newDegree);	
		}
	}
	
	public void steerRight(int degrees) {
		newDegree = Motor.B.getTachoCount() + degrees;
		if (newDegree > rightMax) {
			newDegree = rightMax;
		}
	}
	
	public void steerLeft(int degrees) {
		newDegree = Motor.B.getTachoCount() - degrees;
		if (newDegree < leftMax) {
			newDegree = leftMax;
		}
	}
	
	public void steerStraight(){
		newDegree = neutral+correction;
	}

	public void calibrateSteering() {
		if (limited) {
			calibrateSteeringLimited();
		} else {
			calibrateSteeringUnlimited();
		}
	}
	
	private void calibrateSteeringUnlimited() {
		Motor.B.resetTachoCount();
		leftMax = -maxUnlimitedAngel + correction;
		rightMax = maxUnlimitedAngel + correction;
	}
	
	public int getCurrentSteering(){
		return neutral-Motor.B.getPosition();
	}
	
	public void calibrateSteeringLimited() {

		Motor.B.resetTachoCount();
		Motor.B.backward();
		while (Motor.B.isMoving()) {
			leftMax = Motor.B.getTachoCount();
		}
		Motor.B.stop();
		Motor.B.forward();
		while (Motor.B.isMoving()) {
			rightMax = Motor.B.getTachoCount();
			// sleep(250);
		}
		
		Motor.B.stop();
		Motor.B.rotateTo(neutral);
		sleep(500);
		Motor.B.stop();
		neutral = rightMax - ((Math.abs(leftMax) + Math.abs(rightMax)) / 2) + 2;
		LCD.clear();
		LCD.drawInt(neutral, 1, 1);
		steerStraight();
	}

	public void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
