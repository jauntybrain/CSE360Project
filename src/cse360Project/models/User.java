package cse360Project.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/*******
 * <p>
 * User Class
 * </p>
 * 
 * <p>
 * Description: A class representing a user with various attributes and roles.
 * </p>
 * 
 * <p>
 * Copyright: CSE 360 Team Th02 © 2024
 * </p>
 * 
 * @author CSE 360 Team Th02
 * 
 * @version 1.01 2024-10-09 Phase two
 *          1.00 2024-10-09 Phase one
 * 
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uuid;
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
     * @param uuid 			 The unique identifier of the user.
     * @param username       The username of the user.
     * @param hashedPassword The hashed password of the user.
     */
    public User(String uuid, String username, byte[] hashedPassword) {
        this.uuid = uuid != null ? uuid : UUID.randomUUID().toString();
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
     * @throws IOException            If an I/O error occurs.
     * @throws ClassNotFoundException If the class of a serialized object cannot be
     *                                found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        String base64Hash = (String) ois.readObject();
        password = Base64.getDecoder().decode(base64Hash);
    }

    /**
     * Checks if the user has fully registered by checking if the first name, last
     * name, and email are not null.
     * 
     * @return true if the user has fully registered, false otherwise.
     */
    public boolean isFullyRegistered() {
        return firstName != null && lastName != null && email != null;
    }

    /**
     * Gets the UUID of the user.
     * 
     * @return the UUID.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the username of the user.
     * 
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the hashed password of the user.
     * 
     * @return the hashed password.
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * Sets the hashed password of the user.
     * 
     * @param hashedPassword the hashed password to set.
     */
    public void setPassword(byte[] hashedPassword) {
        this.password = hashedPassword;
    }

    /**
     * Gets the email of the user.
     * 
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * 
     * @param email the email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the user.
     * 
     * @return the first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * 
     * @param firstName the first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middle name of the user.
     * 
     * @return the middle name.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middle name of the user.
     * 
     * @param middleName the middle name to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return the last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * 
     * @param lastName the last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the preferred name of the user.
     * 
     * @return the preferred name.
     */
    public String getPreferredName() {
        return preferredName;
    }

    /**
     * Sets the preferred name of the user.
     * 
     * @param preferredName the preferred name to set.
     */
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    /**
     * Gets the list of topics of the user.
     * 
     * @return the list of topics.
     */
    public List<Topic> getTopics() {
        return topics;
    }

    /**
     * Sets the list of topics of the user.
     * 
     * @param topics the list of topics to set.
     */
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    /**
     * Gets the list of roles of the user.
     * 
     * @return the list of roles.
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles of the user.
     * 
     * @param roles the list of roles to set.
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Gets the has one time password flag of the user.
     * 
     * @return the has one time password flag.
     */
    public boolean getHasOneTimePassword() {
        return hasOneTimePassword;
    }

    /**
     * Sets the has one time password flag of the user.
     * 
     * @param oneTimePassword the has one time password flag to set.
     */
    public void setHasOneTimePassword(boolean oneTimePassword) {
        hasOneTimePassword = oneTimePassword;
    }

    /**
     * Gets the one time password expires of the user.
     * 
     * @return the one time password expires.
     */
    public LocalDateTime getOneTimePasswordExpires() {
        return oneTimePasswordExpires;
    }

    /**
     * Sets the one time password expires of the user.
     * 
     * @param oneTimePasswordExpires the one time password expires to set.
     */
    public void setOneTimePasswordExpires(LocalDateTime oneTimePasswordExpires) {
        this.oneTimePasswordExpires = oneTimePasswordExpires;
    }
}