package data;

public class TrackCorner extends TrackData{
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private int steeringRate = 45;
	
	public int getDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSteeringRate() {
		// TODO Auto-generated method stub
		return steeringRate;
	}
	
	public int getDelay() {
		return 0;
	}
	
	public void setDirection(int direction) {
		
	}
	
	public void setSteeringRate(int newRate) {
		steeringRate = newRate;
	}
	
	public void setDelay(int delay) {
		
	}
}
