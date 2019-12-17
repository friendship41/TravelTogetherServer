package mini.server.network.services;

import java.util.Set;

public interface IRequestProcessService 
{
	public boolean checkDuplId(String userId);
	public boolean signUp(String protocol);
	public String loginCheck(String protocol);
	public String getFriendList(String protocol);
	public Set<String> getSomeoneAddmeStringSet(String userId);
	public void logout(String userId);
	public String addFriend(String protocol);
	public boolean deleteFriend(String myId, String protocol);
	public String getRoomList(String protocol);
	public String getAddRoomResult(String protocol);
	public String getOutRoom(String protocol);
	public String getRoomMemberList(String protocol);
	public String getInviteMessage(String protocol);
	public String replyRoomInvite(String protocol);
	public String chatProcess(String protocol);
	public Set<String> getRoomMemberSet(int roomNum);
	public String getChatList(String protocol);
	public String getComboBoxList();
	public String getAddPlanResponse(String protocol);
	public String getAllPlanList(int roomNum);
	public String deleteSinglePlan(int planNum);
	public String updatePlanToVote(String protocol);
	public String agreePlan(String protocol);
	public void sendImgToClient(String protocol);
}
