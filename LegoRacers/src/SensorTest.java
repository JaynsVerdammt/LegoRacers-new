import lejos.nxt.Button;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import sensors.RGBControl;

public class SensorTest {
	public RGBControl rgbControlLeft;
	public RGBControl rgbControlRight;
	
	public SensorTest(){
		this.rgbControlLeft = new RGBControl(SensorPort.S1);
		this.rgbControlRight = new RGBControl(SensorPort.S2);
	}
	
	public String getColorValueLeft(){
		int colorLeft = rgbControlLeft.getColor();
		return new String("Left: " + String.valueOf(colorLeft));
	}
	
	public String getColorValueRight(){
		int colorRight = rgbControlRight.getColor();
		return new String("Right: " + String.valueOf(colorRight));
	}
	
	public static void main(String[] args) {
		SensorTest sensorTesting = new SensorTest();
		LCD.drawString("ColorTest", 1, 1);
		while (Button.waitForAnyPress(1) == 0) {
			LCD.drawString(sensorTesting.getColorValueLeft(), 0, 2);
			LCD.drawString(sensorTesting.getColorValueRight(), 0, 3);
		}
	}
}
