package logic;

import java.io.Serializable;
/**
 * The Librarian class represents a librarian in the system.
 * It contains information such as ID, name, phone number, password, and username.
 * This class implements Serializable to allow for object serialization.
 */

public class Librarian implements Serializable{
	 /**
     * Serial version UID for ensuring compatibility during serialization.
     */
	private static final long serialVersionUID=4L;
	private int id; //The unique identifier of the librarian
	private String name; //The name of the librarian
	private String phone; //The phone number of the librarian.
	private String password; //The password of the librarian's account.
	private String userName; //The username of the librarian's account.
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
	   /**
     * Returns a string representation of the librarian object.
     *
     * @return a string containing the librarian's details.
     */
	@Override
	public String toString() {
		return "\nid: " + id + "\nname: " + name + "\nphone: " + phone +"\npassword: "+password+ "\nuserName: "+userName;
	}
}
