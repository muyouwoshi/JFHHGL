package com.juxin.predestinate.ui.user.check.self.info;

public class UserPersonInfo {
	private String title;
	private String name;
	private String value;
	private String contact;

	public UserPersonInfo(String title, String name) {
		this.title = title;
		this.name = name;
	}

	public UserPersonInfo(String title, String name, String value) {
		this.title = title;
		this.name = name;
		this.value = value;
	}

	public UserPersonInfo(String title, String name, String value, String contact) {
		this.title = title;
		this.name = name;
		this.value = value;
		this.contact = contact;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}
