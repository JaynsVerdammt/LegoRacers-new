package carControl;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class MotorControl extends Thread {

	private NXTRegulatedMotor leftMotor = Motor.C;
	private NXTRegulatedMotor rightMotor = Motor.A;

	private int topSpeed = (int) (leftMotor.getMaxSpeed() + rightMotor.getMaxSpeed()) / 2;
	private int currentSpeed;
	private int aimSpeed;
	private boolean semaphore = false;

	public MotorControl() {

	}

	public void run() {
		while (!semaphore) {
			currentSpeed = this.getSpeed();
			if (currentSpeed != aimSpeed) {
				int tempDif = aimSpeed - currentSpeed;
				tempDif = Math.round(tempDif / 2);
				setSpeed(currentSpeed + tempDif);
			}
		}

	}

	public void accelerateTo(int speed) {
		semaphore = true;
		if (speed < topSpeed) {
			this.aimSpeed = speed;
		} else {
			this.aimSpeed = this.topSpeed;
		}
		semaphore = false;
	}

	public void brakeTo(int speed) {
		semaphore = true;
		if (speed < this.currentSpeed) {
			this.aimSpeed = speed;
		} else if (speed < 0) {
			this.aimSpeed = 0;
		}
		semaphore = false;
	}
	
	public void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}

	private int getSpeed() {
		int tempSum = (leftMotor.getSpeed() + rightMotor.getSpeed()) / 2;
		return tempSum;
	}

	private void setSpeed(int speed) {
		if(speed > topSpeed)speed=topSpeed;
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
	}

}
