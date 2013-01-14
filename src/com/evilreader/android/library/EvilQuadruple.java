package com.evilreader.android.library;

public class EvilQuadruple {
	private String _EvilAuthor;
	private String _EvilTitle;
	private String _EvilPath;
	private String _EvilId;
	
	public EvilQuadruple(String pEvilAuthor, String pEvilTitle,
			String pEvilPath, String pEvilId) {
		this._EvilAuthor = pEvilAuthor;
		this._EvilTitle = pEvilTitle;
		this._EvilPath = pEvilPath;
		this._EvilId = pEvilId;
	}
	
	public String getEvilAuthor() {
		return this._EvilAuthor;
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
