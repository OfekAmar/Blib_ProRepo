package logic;

import java.io.Serializable;

/**
 * Represents a subscriber in the library system.
 */
public class Subscriber implements Serializable {
	private static final long serialVersionUID = 3L;
	private int id;
	private String name;
	private String phone;
	private String status;
	private String email;
	private String password;

	private String userName;

	/**
	 * Constructs a Subscriber object with the specified details.
	 * 
	 * @param id       the unique identifier of the subscriber
	 * @param name     the name of the subscriber
	 * @param phone    the phone number of the subscriber
	 * @param status   the status of the subscriber (e.g., active, frozen)
	 * @param email    the email address of the subscriber
	 * @param password the password of the subscriber
	 * @param userName the username of the subscriber
	 */
	public Subscriber(int id, String name, String phone, String status, String email, String password,
			String userName) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.status = status;
		this.email = email;
		this.password = password;
		this.userName = userName;
	}

	/**
	 * Returns the unique identifier of the subscriber.
	 * 
	 * @return the subscriber's ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of the subscriber.
	 * 
	 * @return the subscriber's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the phone number of the subscriber.
	 * 
	 * @return the subscriber's phone number
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Returns the status of the subscriber.
	 * 
	 * @return the subscriber's status (e.g., active, frozen)
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns the email address of the subscriber.
	 * 
	 * @return the subscriber's email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the password of the subscriber.
	 * 
	 * @return the subscriber's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the username of the subscriber.
	 * 
	 * @return the subscriber's username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the unique identifier of the subscriber.
	 * 
	 * @param id the new ID for the subscriber
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the name of the subscriber.
	 * 
	 * @param name the new name for the subscriber
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the phone number of the subscriber.
	 * 
	 * @param phone the new phone number for the subscriber
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Sets the status of the subscriber.
	 * 
	 * @param status the new status for the subscriber
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets the email address of the subscriber.
	 * 
	 * @param email the new email address for the subscriber
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the password of the subscriber.
	 * 
	 * @param password the new password for the subscriber
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the username of the subscriber.
	 * 
	 * @param userName the new username for the subscriber
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns a string representation of the subscriber.
	 * 
	 * @return a string containing the subscriber details
	 */
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone + "\nstatus: " + status + "\nemail: " + email
				+ "\npassword: " + password + "\nuserName: " + userName;
	}

}
