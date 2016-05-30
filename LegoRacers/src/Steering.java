import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

public class Steering{

	private int neutral;
	private int leftMax = 2000;
	private int rightMax = 2000;
	private int maxUnlimitedAngel = 60;
	private boolean limited;
	private int correction = 0;

	private SensorPort leftTouchSensor = SensorPort.S4;
	private SensorPort rightTouchSensor = SensorPort.S1;

	public Steering(boolean limited) {
		this.limited = limited;
	}

	public void calibrateSteering() {
		if (limited) {
			calibrateSteeringLimited();
		} else {
			calibrateSteeringUnlimited();
		}
	}

	public void run() {
		TouchSensor leftTouchSensor = new TouchSensor(SensorPort.S4);
		TouchSensor rightTouchSensor = new TouchSensor(SensorPort.S1);
		boolean contact = false;
		int angleBeforeImpact;

		if (leftTouchSensor.isPressed()) {
			angleBeforeImpact = Motor.B.getPosition();

			while (leftTouchSensor.isPressed()) {
				turnLeft(10);
				sleep(300);
			}
			Motor.B.rotateTo(angleBeforeImpact);
		}
		if (rightTouchSensor.isPressed()) {
			angleBeforeImpact = Motor.B.getPosition();

			while (leftTouchSensor.isPressed()) {
				turnRight(10);
				sleep(300);
			}
			Motor.B.rotateTo(angleBeforeImpact);
		}

	}

	private void calibrateSteeringUnlimited() {
		Motor.B.resetTachoCount();
		leftMax = -maxUnlimitedAngel + correction;
		rightMax = maxUnlimitedAngel + correction;
	}

	public void calibrateSteeringLimited() {

		Motor.B.resetTachoCount();
		Motor.B.backward();
		while (Motor.B.isMoving()) {
			refreshLCD();
			leftMax = Motor.B.getTachoCount();
		}
		Motor.B.stop();
		Motor.B.forward();
		while (Motor.B.isMoving()) {
			refreshLCD();
			rightMax = Motor.B.getTachoCount();
			// sleep(250);
		}
		refreshLCD();
		Motor.B.stop();
		Motor.B.rotateTo(neutral);
		sleep(500);
		Motor.B.stop();
		neutral = rightMax - ((Math.abs(leftMax) + Math.abs(rightMax)) / 2) + 2;
		LCD.clear();
		LCD.drawInt(neutral, 1, 1);
	}

	public void setNeutral() {
		Motor.B.rotateTo(neutral);
	}

	public void turnLeft(int degrees) {
		int newDegree = Motor.B.getTachoCount() - degrees;
		if (newDegree < leftMax) {
			newDegree = leftMax;
		}
		Motor.B.rotateTo(newDegree);
	}

	public void turnRight(int degrees) {
		int newDegree = Motor.B.getTachoCount() + degrees;
		if (newDegree > rightMax) {
			newDegree = rightMax;
		}
		Motor.B.rotateTo(newDegree);
	}

	public void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void refreshLCD() {
		LCD.clear();
		LCD.drawString("Calibrate Steering", 0, 1);
		LCD.drawString("CurrentTachoCount", 0, 2);
		LCD.drawInt(Motor.B.getTachoCount(), 0, 3);
		LCD.drawString("Current rightMax", 0, 4);
		LCD.drawInt(rightMax, 0, 5);
		LCD.drawString("Current leftMax", 0, 6);
		LCD.drawInt(leftMax, 0, 7);
	}

}
