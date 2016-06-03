package carControl;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class MotorControl{

	private NXTRegulatedMotor leftMotor = Motor.C;
	private NXTRegulatedMotor rightMotor = Motor.A;

	private int topSpeed = (int) (leftMotor.getMaxSpeed() + rightMotor.getMaxSpeed()) / 2;
	private int currentSpeed;
	private int aimSpeed;
	private boolean semaphore = false;

	public MotorControl() {

	}


	public void accelerateTo(int speed) {
		if (speed < topSpeed) {
			this.aimSpeed = speed;
		} else {
			this.aimSpeed = this.topSpeed;
		}
		actMotorSpeed();
	}

	public void brakeTo(int speed) {
		if (speed < this.currentSpeed) {
			this.aimSpeed = speed;
		} else if (speed < 0) {
			this.aimSpeed = 0;
		}
		actMotorSpeed();
	}
	
	public void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}
	
	private void actMotorSpeed(){
		leftMotor.setSpeed(aimSpeed);
		rightMotor.setSpeed(aimSpeed);
	}

	private int getSpeed() {
		int tempSum = (leftMotor.getSpeed() + rightMotor.getSpeed()) / 2;
		return tempSum;
	}

	private void setSpeed(int speed) {
		if(speed > topSpeed){
			speed=topSpeed;
		}
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		actMotorSpeed();
	}

}
