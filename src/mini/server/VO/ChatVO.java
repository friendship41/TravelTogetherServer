package mini.server.VO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatVO 
{
	private final IntegerProperty chatNum;
	private final IntegerProperty chatRoomNum;
	private final StringProperty chatMemberId;
	private final StringProperty chatMessage;
	private final StringProperty chatTime;	// SYSDATE
	
	public ChatVO() {
		this(0,0,null,null,null);
	}
	public ChatVO(int chatNum, int chatRoomNum, String chatMemberId, String chatMessage, String chatTime) 
	{
		this.chatNum = new SimpleIntegerProperty(chatNum);
		this.chatRoomNum = new SimpleIntegerProperty(chatRoomNum);
		this.chatMemberId = new SimpleStringProperty(chatMemberId);
		this.chatMessage = new SimpleStringProperty(chatMessage);
		this.chatTime = new SimpleStringProperty(chatTime);
	}
	
	
	
	public IntegerProperty getChatNumProperty() {
		return chatNum;
	}
	public IntegerProperty getChatRoomNumProperty() {
		return chatRoomNum;
	}
	public StringProperty getChatMemberIdProperty() {
		return chatMemberId;
	}
	public StringProperty getChatMessageProperty() {
		return chatMessage;
	}
	public StringProperty getChatTimeProperty() {
		return chatTime;
	}
	
	
	
	public int getChatNum() {
		return chatNum.get();
	}
	public int getChatRoomNum() {
		return chatRoomNum.get();
	}
	public String getChatMemberId() {
		return chatMemberId.get();
	}
	public String getChatMessage() {
		return chatMessage.get();
	}
	public String getChatTime() {
		return chatTime.get();
	}
	
	
	
	public void setChatNum(int chatNum) {
		this.chatNum.set(chatNum);
	}
	public void setChatRoomNum(int chatRoomNum) {
		this.chatRoomNum.set(chatRoomNum);
	}
	public void setChatMemberId(String chatMemberId) {
		this.chatMemberId.set(chatMemberId);
	}
	public void setChatMessage(String chatMessage) {
		this.chatMessage.set(chatMessage);
	}
	public void setChatTime(String chatTime) {
		this.chatTime.set(chatTime);
	}
	
}
