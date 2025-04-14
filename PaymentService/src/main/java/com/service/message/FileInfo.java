package com.service.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileInfo {
	
	private String filename;
	private String url;

	public FileInfo(String filename, String url) {
		this.filename = filename;
		this.url = url;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
}