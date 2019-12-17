package mini.server.VO;

import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserVO 
{
	private final StringProperty userId;
	private final StringProperty userPassword;
	private final StringProperty userName;
	private final StringProperty userGender;
	private final StringProperty userPhone;
	private final StringProperty userState;
	private final StringProperty userIpNow;
	
	private Set<String> agreeSet;
	
	public UserVO() {
		this(null,null,null,null,null,null,null,null);
	}
	public UserVO(String userId, String userPassword, String userName,
			String userGender, String userPhone, String userState, String userIpNow, Set<String> agreeSet) {
		this.userId = new SimpleStringProperty(userId);
		this.userPassword = new SimpleStringProperty(userPassword);
		this.userName = new SimpleStringProperty(userName);
		this.userGender = new SimpleStringProperty(userGender);
		this.userPhone = new SimpleStringProperty(userPhone);
		this.userState = new SimpleStringProperty(userState);
		this.userIpNow = new SimpleStringProperty(userIpNow);
		this.setAgreeSet(agreeSet);
	}
	
	
	public StringProperty getUserIdProperty() {
		return userId;
	}
	public StringProperty getUserPasswordProperty() {
		return userPassword;
	}
	public StringProperty getUserNameProperty() {
		return userName;
	}
	public StringProperty getUserGenderProperty() {
		return userGender;
	}
	public StringProperty getUserPhoneProperty() {
		return userPhone;
	}
	public StringProperty getUserStateProperty() {
		return userState;
	}
	public StringProperty getUserIpNowProperty() {
		return userIpNow;
	}
	
	
	
	public String getUserId() {
		return userId.get();
	}
	public String getUserPassword() {
		return userPassword.get();
	}
	public String getUserName() {
		return userName.get();
	}
	public String getUserGender() {
		return userGender.get();
	}
	public String getUserPhone() {
		return userPhone.get();
	}
	public String getUserState() {
		return userState.get();
	}
	public String getUserIpNow() {
		return userIpNow.get();
	}
	
	
	
	public void setUserId(String userId)
	{
		this.userId.set(userId);
	}
	public void setUserPassword(String userPassword)
	{
		this.userPassword.set(userPassword);
	}
	public void setUserName(String userName)
	{
		this.userName.set(userName);
	}
	public void setUserGender(String userGender)
	{
		this.userGender.set(userGender);
	}
	public void setUserPhone(String userPhone)
	{
		this.userPhone.set(userPhone);
	}
	public void setUserState(String userState)
	{
		this.userState.set(userState);
	}
	public void setUserIpNow(String userIpNow)
	{
		this.userIpNow.set(userIpNow);
	}
	
	@Override
	public String toString() {
		return ":"+userId.get()+":"+userName.get()+":"+userState.get();
	}
	
	public Set<String> getAgreeSet() {
		return agreeSet;
	}
	public void setAgreeSet(Set<String> agreeSet) {
		this.agreeSet = agreeSet;
	}
	
	
}
