import lejos.nxt.*;

public class MotorTest {


	public static void ultraSonicTest(){
		UltrasonicSensor ultrasonicSensor1 = new UltrasonicSensor(SensorPort.S3);
		LCD.clear();
		LCD.drawString("UltrasonicSensorTest", 0, 0); 
		//ultrasonicSensor1.
		while(Button.waitForAnyPress(1)==0){
			 LCD.drawString(Float.toString(ultrasonicSensor1.getRange()), 0, 2);
		 }
		
	}
	
	public static void buttonPressTest(){
		 TouchSensor touchSensor1 = new TouchSensor(SensorPort.S2);
		 
		 while(Button.waitForAnyPress(1)==0){
			 if(touchSensor1.isPressed()){
				 LCD.drawString("Button Pressed   ",0,0);
			 }else{
				 LCD.drawString("Button not Pressed",0,0);
			 }
			}
	}

	public static void colorSensorTest() {
		ColorSensor colorSens1 = new ColorSensor(SensorPort.S1);
		LCD.drawString("ColorTest", 1, 1);
		while (Button.waitForAnyPress(1) == 0) {
			LCD.drawString(("Blau: " + colorSens1.getColor().getBlue()), 1, 2);
			LCD.drawString(("Rot: " + colorSens1.getColor().getRed()), 1, 3);
			LCD.drawString(("Grün: " + colorSens1.getColor().getGreen()), 1, 4);
		}
		LCD.clear();
	}

	public static void testMotorStart() {
		LCD.drawString("MotorTest", 1, 1);
		Motor.A.backward();
		Motor.C.backward();
		Motor.A.setSpeed(200);
		Motor.C.setSpeed(200);
		sleep(2000);
		Motor.A.setSpeed(400);
		Motor.C.setSpeed(400);
		sleep(2000);
		Motor.A.setSpeed(2000);
		Motor.C.setSpeed(2000);
		sleep(200);

		// for(int i = 0; i < 11000; i += 250){
		// Motor.A.setSpeed(i);
		// Motor.C.setSpeed(i);
		// try {
		// Thread.sleep(300);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

	}

	public static void testMotorEnde() {
		Motor.A.setSpeed(0);
		Motor.C.setSpeed(0);
	}

	public static void steeringRoutine(Steering steering) {
		//sleep(2000);
		LCD.drawString("steeringRoutine", 0, 0);
		Motor.A.backward();
		Motor.C.backward();
		Motor.A.setSpeed(4000);
		Motor.C.setSpeed(4000);
		sleep(2000);
		Motor.A.setSpeed(2000);
		Motor.C.setSpeed(2000);
		steering.turnLeft(40);
		sleep(300);
		steering.setNeutral();
		sleep(400);
		Motor.A.setSpeed(200);
		Motor.C.setSpeed(200);
		steering.turnRight(60);
		sleep(3000);
		Motor.A.setSpeed(0);
		Motor.B.setSpeed(0);
	}

	public static void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
