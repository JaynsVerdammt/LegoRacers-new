package controller;

import java.util.Iterator;
import java.util.LinkedList;

import data.TrackCorner;
import data.TrackData;
import data.TrackStraight;

public class TrackControl {

	private LinkedList<TrackData> sections;// = new LinkedList<TrackData>();
	private Iterator<TrackData> sectionsIterator;
	private TrackData currentTrackData;
	
	public TrackControl(LinkedList<TrackData> sections){
		this.sections = sections;
		this.sectionsIterator = this.sections.listIterator();		
	}

	public TrackData[] calibrateTrack() {
		// TODO Auto-generated method stub
		return null;
	}

	//-----
	//current sinnlos?
	public static TrackCorner getCurrentTrackData() {
		// TODO Auto-generated method stub
		return null;
	}

	public static TrackCorner getCurrentTrackCorner() {
		// TODO Auto-generated method stub
		return null;
	}

	public static TrackStraight getCurrentTrackStraight() {
		// TODO Auto-generated method stub
		return null;
	}
	//-----
	
	public TrackData getNextTrackSection(){
		//safe currentTrackData for further purpose, not used yet and no idea for what, but it's there
		this.currentTrackData = sectionsIterator.next();		
		return currentTrackData;
	}


}
