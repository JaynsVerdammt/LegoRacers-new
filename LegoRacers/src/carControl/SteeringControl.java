package carControl;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.RegulatedMotor;

public class SteeringControl{

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
	
	private void actSteering(){
		steeringMotor.rotateTo(newDegree, true);
	}
	
	public void correctLeft(){
		correction -= 1;
	}
	
	public void correctRight(){
		correction += 1;
	}
	
	
	
	public void steerRight(int degrees) {
		newDegree = steeringMotor.getTachoCount() + degrees;
		if (newDegree > rightMax) {
			newDegree = rightMax;
		}
		actSteering();
	}
	
	public void steerLeft(int degrees) {
		newDegree = steeringMotor.getTachoCount() - degrees;
		if (newDegree < leftMax) {
			newDegree = leftMax;
		}
		actSteering();
	}
	
	public void steerStraight(){
		newDegree = neutral+correction;
		actSteering();
	}

	public void calibrateSteering() {
		if (limited) {
			calibrateSteeringLimited();
		} else {
			calibrateSteeringUnlimited();
		}
	}
	
	private void calibrateSteeringUnlimited() {
		steeringMotor.resetTachoCount();
		leftMax = -maxUnlimitedAngel + correction;
		rightMax = maxUnlimitedAngel + correction;
	}
	
	public int getCurrentSteering(){
		return neutral-steeringMotor.getPosition();
	}
	
	public void calibrateSteeringLimited() {

		steeringMotor.resetTachoCount();
		steeringMotor.backward();
		while (steeringMotor.isMoving()) {
			leftMax = steeringMotor.getTachoCount();
		}
		steeringMotor.stop();
		steeringMotor.forward();
		while (steeringMotor.isMoving()) {
			rightMax = steeringMotor.getTachoCount();
			// sleep(250);
		}
		
		steeringMotor.stop();
		steeringMotor.rotateTo(neutral);
		sleep(500);
		steeringMotor.stop();
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
