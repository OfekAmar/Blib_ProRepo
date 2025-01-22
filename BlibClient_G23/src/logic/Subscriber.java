package logic;

public class Subscriber {
	private int id;
	private String name;
	private String phone;
	private String status;
	private String email;
	private String password;

	public Subscriber(int id, String name, String phone, String status, String email, String password) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.status = status;
		this.email = email;
		this.password = password;
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
	
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone + "\nstatus: " + status + "\nemail: "+email+"\npassword: "+password;
	}

}
