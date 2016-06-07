package data;

public class TrackCorner extends TrackData{
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	//Richtung der Kurve (recht oder links)
	private int cornerDirection = 0;
	//Länge der Kurve in ms
	private int cornerDelay = 0;
	// Grad der Kurve
	private int steeringRate = 45;
	
	public TrackCorner() {
		
	}
	
	public TrackCorner(int dir, int steer) {
		this.cornerDirection = dir;
		this.steeringRate = steer;
	}
	
	public int getDirection() {
		// TODO Auto-generated method stub
		return this.cornerDirection;
	}

	public int getSteeringRate() {
		// TODO Auto-generated method stub
		return this.steeringRate;
	}
	
	public int getDelay() {
		return this.cornerDelay;
	}
	
	public void setDirection(int direction) {
		this.cornerDirection = direction;
	}
	
	public void setSteeringRate(int newRate) {
		this.steeringRate = newRate;
	}
	
	public void setDelay(int delay) {
		this.cornerDelay=delay;
	}
}

