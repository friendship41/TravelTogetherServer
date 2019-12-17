package mini.server.VO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlanVO 
{
	private final IntegerProperty planNum;
	private final StringProperty planName;
	private final IntegerProperty planRoomNum;
	private final StringProperty planUserId;
	private final IntegerProperty planPurposeNum;
	private final IntegerProperty planMoney;
	private final StringProperty planOther;
	private final StringProperty planLink;
	private final StringProperty planImgLoc;
	private final StringProperty planState;
	private final StringProperty planDate;
	private final StringProperty planTime;
	private final StringProperty planRep;
	private final IntegerProperty planAgreeNum;
	
	private final StringProperty planPurposeName;
	private final IntegerProperty planDistance;
	private final StringProperty planLoc;
	
	
	public PlanVO() {
		this(-1, null, -1, null, -1, -1, null, null, null, null, null, null, null, -1, null, -1, null);
	}
	public PlanVO(int planNum, String planName, int planRoomNum, String planUserId, int planPurposeNum, int planMoney, String planOther, String planLink, String planImgLoc, String planState, String planDate, String planTime, String planRep, int planAgreeNum, String planPurposeName, int planDistance, String planLoc) 
	{
		this.planNum = new SimpleIntegerProperty(planNum);
		this.planName = new SimpleStringProperty(planName);
		this.planRoomNum = new SimpleIntegerProperty(planRoomNum);
		this.planUserId = new SimpleStringProperty(planUserId);
		this.planPurposeNum = new SimpleIntegerProperty(planPurposeNum);
		this.planMoney = new SimpleIntegerProperty(planMoney);
		this.planOther = new SimpleStringProperty(planOther);
		this.planLink = new SimpleStringProperty(planLink);
		this.planImgLoc = new SimpleStringProperty(planImgLoc);
		this.planState = new SimpleStringProperty(planState);
		this.planDate = new SimpleStringProperty(planDate);
		this.planTime = new SimpleStringProperty(planTime);
		this.planRep = new SimpleStringProperty(planRep);
		this.planAgreeNum = new SimpleIntegerProperty(planAgreeNum);
		this.planPurposeName = new SimpleStringProperty(planPurposeName);
		this.planDistance = new SimpleIntegerProperty(planDistance);
		this.planLoc = new SimpleStringProperty(planLoc);
	}
	
	public IntegerProperty getPlanNumProperty() {
		return planNum;
	}
	public StringProperty getPlanNameProperty() {
		return planName;
	}
	public IntegerProperty getPlanRoomNumProperty() {
		return planRoomNum;
	}
	public StringProperty getPlanUserIdProperty() {
		return planUserId;
	}
	public IntegerProperty getPlanPurposeNumProperty() {
		return planPurposeNum;
	}
	public IntegerProperty getPlanMoneyProperty() {
		return planMoney;
	}
	public StringProperty getPlanOtherProperty() {
		return planOther;
	}
	public StringProperty getPlanLinkProperty() {
		return planLink;
	}
	public StringProperty getPlanImgLocProperty() {
		return planImgLoc;
	}
	public StringProperty getPlanStateProperty() {
		return planState;
	}
	public StringProperty getPlanDateProperty() {
		return planDate;
	}
	public StringProperty getPlanTimeProperty() {
		return planTime;
	}
	public StringProperty getPlanRepProperty() {
		return planRep;
	}
	public IntegerProperty getPlanAgreeNumProperty() {
		return planAgreeNum;
	}
	public StringProperty getPlanPurposeNameProperty() {
		return planPurposeName;
	}
	public IntegerProperty getPlanDistanceProperty() {
		return planDistance;
	}
	public StringProperty getPlanLocProperty() {
		return planLoc;
	}
	
	
	
	public int getPlanNum() {
		return planNum.get();
	}
	public String getPlanName() {
		return planName.get();
	}
	public int getPlanRoomNum() {
		return planRoomNum.get();
	}
	public String getPlanUserId() {
		return planUserId.get();
	}
	public int getPlanPurposeNum() {
		return planPurposeNum.get();
	}
	public int getPlanMoney() {
		return planMoney.get();
	}
	public String getPlanOther() {
		return planOther.get();
	}
	public String getPlanLink() {
		return planLink.get();
	}
	public String getPlanImgLoc() {
		return planImgLoc.get();
	}
	public String getPlanState() {
		return planState.get();
	}
	public String getPlanDate() {
		return planDate.get();
	}
	public String getPlanTime() {
		return planTime.get();
	}
	public String getPlanRep() {
		return planRep.get();
	}
	public int getPlanAgreeNum() {
		return planAgreeNum.get();
	}
	public String getPlanPurposeName() {
		return planPurposeName.get();
	}
	public int getPlanDistance() {
		return planDistance.get();
	}
	public String getPlanLoc() {
		return planLoc.get();
	}
//	
	
	
	
	public void setPlanNum(int planNum) {
		this.planNum.set(planNum);
	}
	public void setPlanName(String planName) {
		this.planName.set(planName);
	}
	public void setPlanRoomNum(int planRoomNum) {
		this.planRoomNum.set(planRoomNum);
	}
	public void setPlanUserId(String planUserId) {
		this.planUserId.set(planUserId);
	}
	public void setPlanPurposeNum(int planPurposeNum) {
		this.planPurposeNum.set(planPurposeNum);
	}
	public void setPlanMoney(int planMoney) {
		this.planMoney.set(planMoney);
	}
	public void setPlanOther(String planOther) {
		this.planOther.set(planOther);
	}
	public void setPlanLink(String planLink) {
		this.planLink.set(planLink);
	}
	public void setPlanImgLoc(String planImgLoc) {
		this.planImgLoc.set(planImgLoc);
	}
	public void setPlanState(String planState) {
		this.planState.set(planState);
	}
	public void setPlanDate(String planDate) {
		this.planDate.set(planDate);
	}
	public void setPlanTime(String planTime) {
		this.planTime.set(planTime);
	}
	public void setPlanRep(String planRep) {
		this.planRep.set(planRep);
	}
	public void setPlanAgreeNum(int planAgreeNum) {
		this.planAgreeNum.set(planAgreeNum);
	}
	public void setPlanPurposeName(String planPurposeName) {
		this.planPurposeName.set(planPurposeName);
	}
	public void setPlanDistance(int planDistance) {
		this.planDistance.set(planDistance);
	}
	public void setPlanLoc(String planLoc) {
		this.planLoc.set(planLoc);
	}
	
	
	
}
