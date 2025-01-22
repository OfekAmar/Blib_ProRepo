package logic;

import java.io.Serializable;

public class Librarian implements Serializable{
	private static final long serialVersionUID=4L;
	private int id;
	private String name;
	private String phone;
	private String password;
	private String userName;
	
	public Librarian(int id, String name, String phone, String password, String userName) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone +"\npassword: "+password+ "\nuserName: "+userName;
	}
}
