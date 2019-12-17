package mini.server.network.services;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import mini.server.VO.ChatVO;
import mini.server.VO.PlanVO;
import mini.server.VO.RoomVO;
import mini.server.VO.UserVO;
import mini.server.database.TravelTogetherDAO;
import mini.server.network.staticValues.ConnectionStaticValues;
import mini.server.network.staticValues.NetworkProtocolHeads;

public class RequestProcessService implements IRequestProcessService
{
	TravelTogetherDAO travelTogetherDAO = TravelTogetherDAO.getTravelTogetherDAO();

	@Override
	public boolean checkDuplId(String userId) 
	{
		String temp = travelTogetherDAO.selectUserId(userId);
		if(temp!=null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean signUp(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		UserVO user = new UserVO();
		user.setUserId(st.nextToken());
		user.setUserPassword(st.nextToken());
		user.setUserName(st.nextToken());
		user.setUserGender(st.nextToken());
		user.setUserPhone(st.nextToken());
		
		return travelTogetherDAO.insertUser(user);
	}

	@Override
	public String getFriendList(String protocol) 
	{
		List<UserVO> list = travelTogetherDAO.selectFriendList(protocol.substring(5).trim());
		if(list==null)
			return null;
		StringBuffer sb = new StringBuffer();
		for(UserVO user : list)
		{
			sb.append(":");
			sb.append(user.toString());
		}
		return sb.toString();
	}

	// 1003:'아이디':'비밀번호'
	// 1054:1(1:로그인성공, 2:비번오류, 3:아이디없음):'내이아디':'내이름':'내성별':'내폰번호':'동의플랜'
	@Override
	public String loginCheck(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String id = st.nextToken();
		UserVO user = travelTogetherDAO.selectUser(id);
		if(user == null)
			return ":3";
		if(!user.getUserPassword().equals(st.nextToken()))
			return ":2";
		else
		{
			travelTogetherDAO.updateUserState("T", id);
			StringBuffer sb = new StringBuffer();
			sb.append(":1:");
			sb.append(user.getUserId());
			sb.append(":");
			sb.append(user.getUserName());
			sb.append(":");
			sb.append(user.getUserGender());
			sb.append(":");
			sb.append(user.getUserPhone().trim());
			sb.append(":");
			String agree = user.getUserIpNow();
			if(agree == null || agree.equals(""))
				sb.append("nope");
			else
				sb.append(agree);
			return sb.toString();
		}
	}

//	날 추가한 친구들 셋
	@Override
	public Set<String> getSomeoneAddmeStringSet(String userId) 
	{
		return travelTogetherDAO.selectFriendWhoAddMe(userId);
	}
	
//	로그아웃 시키기
	@Override
	public void logout(String userId) {
		travelTogetherDAO.updateUserState("F", userId);
	}
	
//	친구추가
	// 3002:'내아이디':'친구아이디'
	// 3051:T(친구추가성공여부):'친구아이디':'친구이름':'T(친구상태)'
	@Override
	public String addFriend(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String myid = st.nextToken();
		String friendId = st.nextToken();
		UserVO friend = travelTogetherDAO.selectUser(friendId);
		StringBuffer sb = new StringBuffer();
		if(friend == null)
		{
			sb.append(":F");
			return sb.toString();
		}
		else
		{
			travelTogetherDAO.insertFriendId(myid, friendId);
			sb.append(":T:");
			sb.append(friend.getUserId());
			sb.append(":");
			sb.append(friend.getUserName());
			sb.append(":");
			sb.append(friend.getUserState());
			return sb.toString();
		}
	}

//	친구 삭제
	@Override
	public boolean deleteFriend(String myId, String protocol) 
	{
		// 3003:'친구아이디'
		// 3055:true
		return travelTogetherDAO.deleteFriendId(myId, protocol.substring(5).trim());
	}

//	방목록 요청 처리해서 문자열로 반환
	// 4001:'내아이디'
	// 4054:'room_num':'room_owner_id':'room_name':'room_place':'start_date':'end_date':'room_member_num'
	@Override
	public String getRoomList(String protocol) 
	{
		List<RoomVO> list = travelTogetherDAO.selectRoomList(protocol.substring(5).trim());
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		for(RoomVO room : list)
		{
			sb.append(room.getRoomNum());
			sb.append(":");
			sb.append(room.getRoomOwnerId());
			sb.append(":");
			sb.append(room.getRoomName());
			sb.append(":");
			sb.append(room.getRoomPlace());
			sb.append(":");
			sb.append(room.getRoomStartDate());
			sb.append(":");
			sb.append(room.getRoomEndDate());
			sb.append(":");
			sb.append(room.getRoomMemberNum());
			sb.append(":");
		}
		
		return sb.toString();
	}

//	방 추가 요청 처리
	// 4002:'내아이디':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE'
	// 4051:'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM'
	@Override
	public String getAddRoomResult(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String myId = st.nextToken();
		RoomVO insertRoom = new RoomVO();
		insertRoom.setRoomOwnerId(myId);
		insertRoom.setRoomName(st.nextToken());
		insertRoom.setRoomPlace(st.nextToken());
		insertRoom.setRoomStartDate(st.nextToken());
		insertRoom.setRoomEndDate(st.nextToken());
		
		
		RoomVO resultRoom = travelTogetherDAO.insertRoom(insertRoom);
		
		if(resultRoom == null)
			return null;
		
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		sb.append(resultRoom.getRoomNum());
		sb.append(":");
		sb.append(resultRoom.getRoomOwnerId());
		sb.append(":");
		sb.append(resultRoom.getRoomName());
		sb.append(":");
		sb.append(resultRoom.getRoomPlace());
		sb.append(":");
		sb.append(resultRoom.getRoomStartDate());
		sb.append(":");
		sb.append(resultRoom.getRoomEndDate());
		sb.append(":");
		sb.append(resultRoom.getRoomMemberNum());
		
		return sb.toString();
	}

//	방나가기 오청 처리
	// 4003:'내아이디':'방번호'
	// 4053:true
	@Override
	public String getOutRoom(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String myId = st.nextToken();
		String thisRoom = st.nextToken();
		
		String result = travelTogetherDAO.getOutRoom(myId, Integer.parseInt(thisRoom.trim()));
		if(result ==null || result.equals("잘못된접근"))
		{
			return "1";
		}
		else if(result.equals("아직사람남음"))
		{
			return "2";
		}
		else if(result.equals("이제사람없음"))
		{
			return "3";
		}
		else
		{
			return "1";
		}
	}

	
//	방 들어갈떄 방 멤버 조회해서 가져오기
	// 5001:'방번호'
	// 5051:'멤버1아이디':'멤버1이름':'멤버1상태':'멤버2아이디':'멤버2이름':'멤버2상태'....
	@Override
	public String getRoomMemberList(String protocol) 
	{
		int RoomNum = Integer.parseInt(protocol.substring(5).trim());
		List<UserVO> memberList = travelTogetherDAO.getRoomMemberList(RoomNum);
		
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		for(UserVO user : memberList)
		{
			sb.append(user.getUserId());
			sb.append(":");
			sb.append(user.getUserName());
			sb.append(":");
			sb.append(user.getUserState());
			sb.append(":");
		}
		return sb.toString();
	}

//	방에 멤버 추가하는 요청 (추가 요청을 받아서 해당 아이디로 초대 메세지 보냄)
	// 5003:'내아이디':'방번호':'여행지':'친구아이디' -> 4052
	// 4052:'초대한사람':'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM' -> 5004
	// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num'
	// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
	@Override
	public String getInviteMessage(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String myId = st.nextToken();
		int roomNum = Integer.parseInt(st.nextToken());
		st.nextToken();
		String friendId = st.nextToken();
		UserVO friend = travelTogetherDAO.selectUser(friendId);
		if(friend == null)
		{
			return null;
		}
		
		RoomVO room = travelTogetherDAO.selectSingleRoom(roomNum);
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		sb.append(myId);
		sb.append(":");
		sb.append(room.getRoomNum());
		sb.append(":");
		sb.append(room.getRoomOwnerId());
		sb.append(":");
		sb.append(room.getRoomName());
		sb.append(":");
		sb.append(room.getRoomPlace());
		sb.append(":");
		sb.append(room.getRoomStartDate());
		sb.append(":");
		sb.append(room.getRoomEndDate());
		sb.append(":");
		sb.append(room.getRoomMemberNum());
		return sb.toString();
	}

	
//	방 초대 답장이 서버로 왔을때 처리
	// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num'
	// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
	@Override
	public String replyRoomInvite(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String tf = st.nextToken();
		String invitedId = st.nextToken();
		String inviteId = st.nextToken();
		int roomNum = Integer.parseInt(st.nextToken().trim());
		
		StringBuffer sb = new StringBuffer();
		sb.append(":");
//		초대에 응했을떄
		if(tf.equalsIgnoreCase("T"))
		{
			UserVO friend = travelTogetherDAO.selectUser(invitedId);
			boolean temp = travelTogetherDAO.insertRoomMember(roomNum, invitedId);
			if(temp == false)
			{
				sb.append("2");
				return sb.toString();
			}
			
			sb.append("1");
			sb.append(":");
			sb.append(friend.getUserId());
			sb.append(":");
			sb.append(friend.getUserName());
			sb.append(":");
			sb.append(friend.getUserState());
		}
//		초대를 거절 했을때
		else
		{
			sb.append("3");
		}
		return sb.toString();
	}

	
//	채팅 왔을때 처리
	// 5002:'내아이디':'방번호':'메세지' -> 5052
	// 5052:'방번호':'채팅한사람':'채팅내용'
	@Override
	public String chatProcess(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		String chatId = st.nextToken();
		int roomNum = Integer.parseInt(st.nextToken());
		String message = st.nextToken();
		
		travelTogetherDAO.insertChat(roomNum, chatId, message);
		
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		sb.append(roomNum);
		sb.append(":");
		sb.append(chatId);
		sb.append(":");
		sb.append(message);
		return sb.toString();
	}
	
	
//	방번호 입력하면 해당 방의 구성원들 ID(SET) 불러오기
	@Override
	public Set<String> getRoomMemberSet(int roomNum) 
	{
		List<UserVO> list = travelTogetherDAO.getRoomMemberList(roomNum);
		Set<String> set = new HashSet<>();
		for(UserVO user : list)
		{
			set.add(user.getUserId());
		}
		return set;
	}
	
	
//	방 입장시 이전 채팅들 불러오기
	// 5005:'채팅방번호'
	// 5054:'방번호1':'채팅시간1':'채팅한사람1':'채팅내용1':'채팅시간2':'채팅한사람2':'채팅내용2'....
	@Override
	public String getChatList(String protocol) 
	{
		int roomNum = Integer.parseInt(protocol.substring(5).trim());
		List<ChatVO> list = travelTogetherDAO.selectChatList(roomNum);
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		sb.append(roomNum);
		for(ChatVO chat : list)
		{
			sb.append(":");
			sb.append(chat.getChatTime());
			sb.append(":");
			sb.append(chat.getChatMemberId());
			sb.append(":");
			sb.append(chat.getChatMessage());
		}
		return sb.toString();
	}

//	콤보박스 리스트 달라하면 던져주기
	// 6005:
	// 6053:'구분1':'구분2':'구분3'....
	@Override
	public String getComboBoxList() 
	{
		List<String> list = travelTogetherDAO.selectPurposeNameList();
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		for(String s : list)
		{
			sb.append(s);
			sb.append(":");
		}
		return sb.toString();
	}

//	새로운 플랜 들어오면 DB저장하고 뿌려주기
	// 6003:'작성자':'방번호':'제목':'구분':'예상지출':'좌표':'기타':'링크':'날짜':'이미지명' -> 6051
	// 6051:'플랜번호':'제목':'플랜방번호':'작성자':'구분':'예상지출':'좌표':'기타':'링크'
	@Override
	public String getAddPlanResponse(String protocol) 
	{
		PlanVO insertPlan = new PlanVO();
		StringTokenizer st = new StringTokenizer(protocol,":");
		st.nextToken();
		insertPlan.setPlanUserId(st.nextToken());
		insertPlan.setPlanRoomNum(Integer.parseInt(st.nextToken()));
		insertPlan.setPlanName(st.nextToken());
		insertPlan.setPlanPurposeName(st.nextToken());
		insertPlan.setPlanMoney(Integer.parseInt(st.nextToken()));
		insertPlan.setPlanLoc(st.nextToken());
		insertPlan.setPlanOther(st.nextToken());
		insertPlan.setPlanLink(st.nextToken());
		insertPlan.setPlanDate(st.nextToken());
		insertPlan.setPlanImgLoc("X:/miniproject1/server/imgs/"+st.nextToken());
		
		PlanVO plan = travelTogetherDAO.insertPlanAndSelectPlan(insertPlan);
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		sb.append(plan.getPlanNum());
		sb.append(":");
		sb.append(plan.getPlanName());
		sb.append(":");
		sb.append(plan.getPlanRoomNum());
		sb.append(":");
		sb.append(plan.getPlanUserId());
		sb.append(":");
		sb.append(plan.getPlanPurposeName());
		sb.append(":");
		sb.append(plan.getPlanMoney());
		sb.append(":");
		sb.append(plan.getPlanLoc());
		sb.append(":");
		sb.append(plan.getPlanOther());
		sb.append(":");
		sb.append(plan.getPlanLink());
		
		return sb.toString();
	}

//	특정 방에 있는 모든 플랜들 주기
	//* 6002:'방번호'	-> 6054
	//* 6054:'플렌번호':'플랜명':'플랜방번호':'작성자':'구분':'예상지출':'기타':'링크':'좌표':'플랜상태':'플랜날짜':'플랜:시간':'대표여부':'동의자수'
	@Override
	public String getAllPlanList(int roomNum) 
	{
		List<PlanVO> list = travelTogetherDAO.SelectAllPlan(roomNum);
		StringBuffer sb = new StringBuffer();
		for(PlanVO plan : list)
		{
			sb.append(":");
			sb.append(plan.getPlanNum());
			sb.append(":");
			sb.append(plan.getPlanName());
			sb.append(":");
			sb.append(plan.getPlanRoomNum());
			sb.append(":");
			sb.append(plan.getPlanUserId());
			sb.append(":");
			sb.append(plan.getPlanPurposeName());
			sb.append(":");
			sb.append(plan.getPlanMoney());
			sb.append(":");
			sb.append(plan.getPlanOther());
			sb.append(":");
			sb.append(plan.getPlanLink());
			sb.append(":");
			sb.append(plan.getPlanLoc());
			sb.append(":");
			sb.append(plan.getPlanState());
			sb.append(":");
			sb.append(plan.getPlanDate());
			sb.append(":");
			sb.append(plan.getPlanTime());
			sb.append(":");
			sb.append(plan.getPlanRep());
			sb.append(":");
			sb.append(plan.getPlanAgreeNum());
		}
		
		return sb.toString();
	}

//	플랜 삭제요청 들어왔을떄 삭제하고 뿌려주기
	// 6004:'플렌번호':'방번호'	-> 6052
	// 6052:'플랜번호'
	@Override
	public String deleteSinglePlan(int planNum) 
	{
		if(travelTogetherDAO.deleteSinglePlan(planNum))
			return ":"+planNum;
		else
			return ":-1";
	}

//	플랜 투표 요청 처리
	//* 6102:'플렌번호':'플랜:시간':'방번호':'플랜날짜' -> 6151
	//* 6151:'플랜번호':'플랜:시간':'플랜날짜'
	@Override
	public String updatePlanToVote(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		int planNum = Integer.parseInt(st.nextToken());
		String planTime = st.nextToken()+":"+st.nextToken();
		st.nextToken();
		String planDate = st.nextToken();
		if(travelTogetherDAO.updatePlanToVote(planNum, planTime, planDate))
		{
			StringBuffer sb = new StringBuffer();
			sb.append(":");
			sb.append(planNum);
			sb.append(":");
			sb.append(planTime);
			sb.append(":");
			sb.append(planDate);
			return sb.toString();
		}
		else
		{
			return ":xxxxx";
		}
	}

//	투표플랜 동의 요청 처리하고 뿌려주기
		//* 6103:'방번호':'플랜번호':'동의유저ID':'방인원수' -> 6152
	//* 6152:1(1.모두동의, 2.아직남음):'플랜번호':'동의유저ID'
	@Override
	public String agreePlan(String protocol) 
	{
		StringTokenizer st = new StringTokenizer(protocol, ":");
		st.nextToken();
		st.nextToken();
		int planNum = Integer.parseInt(st.nextToken().trim());
		String userId =  st.nextToken();
		int roomMemberNum = Integer.parseInt(st.nextToken());
		int resultState = travelTogetherDAO.updateAgreeNum(planNum, userId, roomMemberNum);
		
		StringBuffer sb = new StringBuffer();
		sb.append(":");
		if(resultState == 1)
		{
			sb.append(1);
		}
		else if(resultState == 2)
		{
			sb.append(2);
		}
		else
		{
			return ":xxxxxxx";
		}
		
		sb.append(":");
		sb.append(planNum);
		sb.append(":");
		sb.append(userId);
		return sb.toString();
	}
	
//	이미지 파일을 달라는 요청이 온다...
	//* 6001:'플랜번호'
	@Override
	public void sendImgToClient(String protocol) 
	{
		try {
			int planNum = Integer.parseInt(protocol.split(":")[1].trim());
			String imgLoc = travelTogetherDAO.selectPlanImgLoc(planNum);
			File file = new File(imgLoc);
			
			SocketChannel socketFileChannel = SocketChannel.open();
			socketFileChannel.configureBlocking(true);
			socketFileChannel.connect(new InetSocketAddress(ConnectionStaticValues.SERVER_INET_ADDRESS, ConnectionStaticValues.C_FILE_SERVER_PORT));
			System.out.println("[netservice] 파일서버 소켓 연결성공");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			
			
			String fileName=file.getName();
			System.out.println(fileName);
			
			ByteBuffer byteBuffer1 = null;
			Charset charset = Charset.forName("UTF-8");
			byteBuffer1 = charset.encode(fileName);
			socketFileChannel.write(byteBuffer1);
			
			FileChannel fileChannel = null;
			if(imgLoc.equals("nope"))
			{
				socketFileChannel.close();
				return;
			}
			else
				fileChannel = FileChannel.open(file.toPath());
			ByteBuffer byteBuffer = ByteBuffer.allocate(10000000);
			
			int bytesRead = fileChannel.read(byteBuffer);
			
			while(bytesRead != -1)
			{
				byteBuffer.flip();
				socketFileChannel.write(byteBuffer);
				byteBuffer.compact();
				bytesRead = fileChannel.read(byteBuffer);
			}
			
			fileChannel.close();
			socketFileChannel.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
}
