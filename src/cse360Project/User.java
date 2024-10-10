package cse360Project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/*******
 * <p> User Class </p>
 * 
 * <p> Description: A class representing a user with various attributes and roles. </p>
 * 
 * <p> Copyright: CSE 360 Team © 2024 </p>
 * 
 * @author CSE 360 Team
 * 
 * @version 1.00 2024-10-09 Phase one
 * 
 */

enum Topic {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

enum Role {
    ADMIN,
    STUDENT,
    INSTRUCTOR
}

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
	private String username;
    private byte[] password;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private List<Role> roles;
    private List<Topic> topics;
    private boolean hasOneTimePassword;
    private LocalDateTime oneTimePasswordExpires;

    /** 
     * Constructor to initialize a User with a username and hashed password.
     * 
     * @param username The username of the user.
     * @param hashedPassword The hashed password of the user.
     */
    public User(String username, byte[] hashedPassword) {
        this.username = username;
        this.password = hashedPassword;
        this.hasOneTimePassword = false;
        this.roles = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.topics.add(Topic.INTERMEDIATE);
    }

    /**
     * Custom serialization method to write the object.
     * 
     * @param oos The ObjectOutputStream to write the object to.
     * @throws IOException If an I/O error occurs.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(Base64.getEncoder().encodeToString(password));
    }

    /**
     * Custom deserialization method to read the object.
     * 
     * @param ois The ObjectInputStream to read the object from.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        String base64Hash = (String) ois.readObject();
        password = Base64.getDecoder().decode(base64Hash);
    }

    /**
     * Checks if the user has fully registered by checking if the first name, last name, and email are not null.
     * 
     * @return true if the user has fully registered, false otherwise.
     */
    public boolean isFullyRegistered() {
        return firstName != null && lastName != null && email != null;
    }

    // Getters and setters for the User class attributes
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] hashedPassword) {
        this.password = hashedPassword;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
    	return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    public boolean getHasOneTimePassword() {
        return hasOneTimePassword;
    }

    public void setHasOneTimePassword(boolean oneTimePassword) {
    	hasOneTimePassword = oneTimePassword;
    }

    public LocalDateTime getOneTimePasswordExpires() {
        return oneTimePasswordExpires;
    }

    public void setOneTimePasswordExpires(LocalDateTime oneTimePasswordExpires) {
        this.oneTimePasswordExpires = oneTimePasswordExpires;
    }
}