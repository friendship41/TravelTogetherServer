package mini.server.VO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RoomVO 
{
	private final IntegerProperty roomNum;
	private final StringProperty roomOwnerId;
	private final StringProperty roomName;
	private final StringProperty roomPlace;
	private final StringProperty roomStartDate;
	private final StringProperty roomEndDate;
	private final IntegerProperty roomMemberNum;

	private final StringProperty roomMemberId;
	
	public RoomVO() 
	{
		this(0, null, null, null, null, null, 0, null);
	}
	public RoomVO(int roomNum, String roomOwnerId, String roomName, String roomPlace, String roomStartDate, String roomEndDate ,int roomMemberNum, String roomMemberId)
	{
		this.roomNum = new SimpleIntegerProperty(roomNum);
		this.roomOwnerId = new SimpleStringProperty(roomOwnerId);
		this.roomName = new SimpleStringProperty(roomName);
		this.roomPlace = new SimpleStringProperty(roomPlace);
		this.roomStartDate = new SimpleStringProperty(roomStartDate);
		this.roomEndDate = new SimpleStringProperty(roomEndDate);
		this.roomMemberNum = new SimpleIntegerProperty(roomMemberNum);
		this.roomMemberId = new SimpleStringProperty(roomMemberId);
	}
	
	
	public IntegerProperty getRoomNumProperty() {
		return roomNum;
	}
	public StringProperty getRoomOwnerIdProperty() {
		return roomOwnerId;
	}
	public StringProperty getRoomNameProperty() {
		return roomName;
	}
	public StringProperty getRoomPlaceProperty() {
		return roomPlace;
	}
	public StringProperty getRoomStartDateProperty() {
		return roomStartDate;
	}
	public StringProperty getRoomEndDateProperty() {
		return roomEndDate;
	}
	public IntegerProperty getRoomMemberNumProperty() {
		return roomMemberNum;
	}
	public StringProperty getRoomMemberIdProperty() {
		return roomMemberId;
	}
	
	
	
	public int getRoomNum() {
		return roomNum.get();
	}
	public String getRoomOwnerId() {
		return roomOwnerId.get();
	}
	public String getRoomName() {
		return roomName.get();
	}
	public String getRoomPlace() {
		return roomPlace.get();
	}
	public String getRoomStartDate() {
		return roomStartDate.get();
	}
	public String getRoomEndDate() {
		return roomEndDate.get();
	}
	public int getRoomMemberNum() {
		return roomMemberNum.get();
	}
	public String getRoomMemberId() {
		return roomMemberId.get();
	}
	
	
	
	public void setRoomNum(int roomNum) {
		this.roomNum.set(roomNum);
	}
	public void setRoomOwnerId(String roomOwnerId) {
		this.roomOwnerId.set(roomOwnerId);
	}
	public void setRoomName(String roomName) {
		this.roomName.set(roomName);
	}
	public void setRoomPlace(String roomPlace) {
		this.roomPlace.set(roomPlace);
	}
	public void setRoomStartDate(String roomStartDate) {
		this.roomStartDate.set(roomStartDate);
	}
	public void setRoomEndDate(String roomEndDate) {
		this.roomEndDate.set(roomEndDate);
	}
	public void setRoomMemberNum(int roomMemberNum) {
		this.roomMemberNum.set(roomMemberNum);
	}
	public void setRoomMemberId(String roomMemberId) {
		this.roomMemberId.set(roomMemberId);
	}
	
	@Override
	public String toString() {
		return "RoomVO [roomNum=" + roomNum + ", roomOwnerId=" + roomOwnerId + ", roomName=" + roomName + ", roomPlace="
				+ roomPlace + ", roomStartDate=" + roomStartDate + ", roomEndDate=" + roomEndDate + ", roomMemberNum="
				+ roomMemberNum + ", roomMemberId=" + roomMemberId + "]";
	}
	
	
	
}