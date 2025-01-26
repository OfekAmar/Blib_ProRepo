package logic;

import java.io.Serializable;

/**
 * The Librarian class represents a librarian in the system. It contains
 * information such as ID, name, phone number, password, and username. This
 * class implements Serializable to allow for object serialization.
 */
public class Librarian implements Serializable {
	private static final long serialVersionUID = 4L;
	private int id;
	private String name;
	private String phone;
	private String password;
	private String userName;

	/**
	 * Constructs a new Librarian object with the specified details.
	 *
	 * @param id       the unique identifier of the librarian.
	 * @param name     the name of the librarian.
	 * @param phone    the phone number of the librarian.
	 * @param password the password of the librarian's account.
	 * @param userName the username of the librarian's account.
	 */
	public Librarian(int id, String name, String phone, String password, String userName) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.userName = userName;
	}

	/**
	 * Retrieves the unique identifier of the librarian.
	 *
	 * @return the librarian's ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Updates the unique identifier of the librarian.
	 *
	 * @param id the new ID of the librarian.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retrieves the name of the librarian.
	 *
	 * @return the librarian's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Updates the name of the librarian.
	 *
	 * @param name the new name of the librarian.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the phone number of the librarian.
	 *
	 * @return the librarian's phone number.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Updates the phone number of the librarian.
	 *
	 * @param phone the new phone number of the librarian.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Retrieves the password of the librarian's account.
	 *
	 * @return the librarian's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Updates the password of the librarian's account.
	 *
	 * @param password the new password for the librarian's account.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retrieves the username of the librarian's account.
	 *
	 * @return the librarian's username.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Updates the username of the librarian's account.
	 *
	 * @param userName the new username for the librarian's account.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns a string representation of the librarian object.
	 *
	 * @return a string containing the librarian's details.
	 */
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone + "\npassword: " + password + "\nuserName: "
				+ userName;
	}
}
