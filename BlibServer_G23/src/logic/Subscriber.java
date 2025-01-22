package logic;

import java.io.Serializable;

public class Subscriber implements Serializable {
	private static final long serialVersionUID=3L;
	private int id;
	private String name;
	private String phone;
	private String status;
	private String email;
	private String password;



	private String userName;

	public Subscriber(int id, String name, String phone, String status, String email, String password,String userName) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.status = status;
		this.email = email;
		this.password = password;
		this.userName=userName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getStatus() {
		return status;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
	
	public String getUserName() {
		return userName;
	}


	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone + "\nstatus: " + status + "\nemail: "+email+"\npassword: "+password+ "\nuserName: "+userName;
	}

}
