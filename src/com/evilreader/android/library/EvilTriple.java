package com.evilreader.android.library;

public class EvilTriple {
	private String _EvilTitle;
	private String _EvilPath;
	private String _EvilId;
	
	public EvilTriple(String pEvilTitle, String pEvilPath, String pEvilId) {
		this._EvilTitle = pEvilTitle;
		this._EvilPath = pEvilPath;
		this._EvilId = pEvilId;
	}
	
	public String getEvilTitle() {
		return this._EvilTitle;
	}
	
	public String getEvilPath() {
		return this._EvilPath;
	}
	
	public String getEvilId() {
		return this._EvilId;
	}

}
