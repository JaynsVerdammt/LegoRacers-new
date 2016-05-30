package controller;


import carControl.MotorControl;
import carControl.SteeringControl;
import data.TrackCorner;
import data.TrackStraight;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import sensors.RGBControl;
import sensors.TouchControl;
import sensors.UltrasonicControl;
import controller.TrackControl;

public class MainControl {
	SteeringControl steeringControl;
	MotorControl motorControl;
	TrackControl trackControl;
	RGBControl rgbControlLeft;
	RGBControl rgbControlRight;
	TouchControl touchControl;
	UltrasonicControl ultrasonicControl;

	TrackCorner currentTrackCorner;
	TrackStraight currentTrackStraight;

	int left = 0;
	int right = 1;

	public MainControl() {
		initialize();
	}

	public void initialize() {
		steeringControl = new SteeringControl(false);
		motorControl = new MotorControl();
		trackControl = new TrackControl();
		rgbControlLeft = new RGBControl(this, SensorPort.S4);
		rgbControlRight = new RGBControl(this, SensorPort.S1);
		touchControl = new TouchControl(this);
		ultrasonicControl = new UltrasonicControl(this);

		steeringControl.start();
		rgbControlLeft.start();
		rgbControlRight.start();
		touchControl.start();
		ultrasonicControl.start();

	}

	public void cornerDetected(int correction) {
		currentTrackCorner = TrackControl.getCurrentTrackCorner();

		if (currentTrackCorner.getDirection() == left) {
			steeringControl.steerLeft(currentTrackCorner.getSteeringRate() + correction);
		} else if (currentTrackCorner.getDirection() == right) {
			steeringControl.steerRight(currentTrackCorner.getSteeringRate() + correction);
		} else {
			motorControl.stopMotors();
			LCD.clear();
			LCD.drawString("FAILURE", 0, 0);
			LCD.drawString("Wrong Corner DirectioN", 0, 1);
			LCD.drawString("Return Value: " + currentTrackCorner.getDirection(), 0, 2);
		}
	}
	
	public void straightDetected(int correction) {
		currentTrackStraight = TrackControl.getCurrentTrackStraight();
		steeringControl.steerStraight();
		motorControl.accelerateTo(100000);
		
		if(correction != 0){
			correctDirection(correction);
		}
		
	}
	
	public void correctDirection(int correction){
		
	}
	

}
