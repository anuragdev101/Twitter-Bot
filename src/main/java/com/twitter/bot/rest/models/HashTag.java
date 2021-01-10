package com.twitter.bot.rest.models;

public class HashTag {
	
	private String[] hashTagText;
	private double[] locations;
	private String language;
	private int time;
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String[] getHashTagText() {
		return hashTagText;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setHashTagText(String hashTagText) {
		this.hashTagText = hashTagText.split(";");
	}
	
	public double[] getLocations() {
		return locations;
	}
	
	public void setLocations(String locations) {
		String[] locArr = locations.split(";");
		this.locations= new double[locArr.length];
		for(int i =0;i<locArr.length;i++) {
			this.locations[i]=Double.valueOf(locArr[i]);
		}
	}
	
}
