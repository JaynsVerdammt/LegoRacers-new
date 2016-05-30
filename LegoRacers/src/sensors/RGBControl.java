package sensors;

import controller.MainControl;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.Color;

public class RGBControl extends Thread{
	
	public static final int BLACK = Color.BLACK;
	public static final int WHITE = Color.WHITE;
	public static final int RED = Color.RED;
	public static final int GREEN = Color.GREEN;
	
	private ColorSensor colorSensor;
	
	public RGBControl(MainControl mainControl, SensorPort sensorPort) {
		this.colorSensor = new ColorSensor(sensorPort);
	}
	
	public RGBControl(SensorPort sensorPort) {
		this.colorSensor = new ColorSensor(sensorPort);
	}
	
	public int getColor() {
		return this.colorSensor.getColorID();
	}
	
	public void calibrateHight(){
		this.colorSensor.calibrateHigh();
	}
	
	public void calibrateLow(){
		this.colorSensor.calibrateLow();
	}

}
