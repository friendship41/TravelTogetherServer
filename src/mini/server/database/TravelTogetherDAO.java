package mini.server.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import mini.server.VO.ChatVO;
import mini.server.VO.PlanVO;
import mini.server.VO.RoomVO;
import mini.server.VO.UserVO;
import mini.server.network.staticValues.ConnectionStaticValues;

public class TravelTogetherDAO 
{
	private static TravelTogetherDAO travelTogetherDAO = new TravelTogetherDAO();
	Properties sqlProps;
	
	private TravelTogetherDAO() {
		try {
			sqlProps= new Properties();
			sqlProps.load(new FileInputStream("src/mini/server/database/TravelTogether.properties"));
		} catch (FileNotFoundException e) {
			System.out.println(this.getClass().getName()+"에러: TravelTogetherDAO(), fileNotFound");
		} catch (IOException e) {
			System.out.println("에러: TravelTogetherDAO(), IOException");
		}
		
//		드라이버 로드
		try {
			Class.forName(ConnectionStaticValues.ORACLEDRIVER);
			System.out.println("드라이버 검색 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 검색 실패");
		}
	}
	
	public static TravelTogetherDAO getTravelTogetherDAO() {
		return travelTogetherDAO;
	}
	
	
//	커넥션 받아오기
	private Connection getConnection()
	{
		Connection con = null;
		try {
			String url = ConnectionStaticValues.URL;
			String id = ConnectionStaticValues.ID;
			String pw = ConnectionStaticValues.PASSWORD;
			con = DriverManager.getConnection(url,id,pw);
		} catch (SQLException e) {
			System.out.println("커넥션 실패");
			disConnect(con,null,null);
		}
		return con;
	}
//	커넥션 끊기
	private void disConnect(Connection con, PreparedStatement pstmt, ResultSet rs)
	{
		try {if(rs!=null && !rs.isClosed()){rs.close();}} catch (SQLException e1) {}
		try {if(pstmt!=null && !pstmt.isClosed()){pstmt.close();}}catch (SQLException e) {}
		try {if(con!=null && !con.isClosed()){con.close();}}catch (SQLException e) {}
//		System.out.println("---------------연결 종료--------------");
	}
	
	
	
/*	복붙용 견본
	public boolean 메서드명(매개변수)****************
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty(******************쿼리문명);
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			물음표 있으면 밑에꺼 수행***************************************
			pstmt.setString(1, user.getUserId());
			pstmt.setString(2, user.getUserPassword());
			
			rs = pstmt.executeQuery();
			
			
			
		} catch (SQLException e) {
			System.out.println("err: 에러 메세지***************: "+e.getMessage());************************
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	*/
	
	
	
	
	
	
//	아이디 중복 확인을 위해  아이디 있는지 DB검색
	public String selectUserId(String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_USER_ID");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				System.out.println(rs.getString("USER_ID"));
				return rs.getString("USER_ID");
			}
			else
			{
				return null;
			}
		} catch (SQLException e) {
			System.out.println("err: selectUserId(String userId): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	로그인 하면서 유저 상태 변경
	public boolean updateUserState(String state, String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("UPDATE_USER_STATE");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, state);
			pstmt.setString(2, userId);
			
			int i = pstmt.executeUpdate();
			
			if(i == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
			
		} catch (SQLException e) {
			System.out.println("err: updateUserState(String state, String userId)");
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	유저 정보 입력
	public boolean insertUser(UserVO user)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("INSERT_USER");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, user.getUserId());
			pstmt.setString(2, user.getUserPassword());
			pstmt.setString(3, user.getUserName());
			pstmt.setString(4, user.getUserGender());
			pstmt.setString(5, user.getUserPhone());
			
			int r = pstmt.executeUpdate();
			if(r!=0)
				return true;
			else
				return false;
			
		} catch (SQLException e) {
			System.out.println("err: insertUser(UserVO user): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	유저정보 받아오기
	public UserVO selectUser(String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_SINGLE_FROM_USER_ID");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				UserVO user = new UserVO();
				user.setUserId(rs.getString("USER_ID"));
				user.setUserPassword(rs.getString("USER_PASSWORD"));
				user.setUserName(rs.getString("USER_NAME"));
				user.setUserGender(rs.getString("USER_GENDER"));
				user.setUserPhone(rs.getString("USER_PHONE"));
				user.setUserState(rs.getString("USER_STATE"));
				user.setUserIpNow(rs.getString("AGREESET"));
				return user;
			}
			else
			{
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("err: selectPassword(String userId, String password): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	친구 테이블에서 친구들 받아오기
	public List<UserVO> selectFriendList(String userId)
	{
		List<UserVO> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_FRIEND_USER_LIST");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				UserVO user = new UserVO();
				user.setUserId(rs.getString("USER_ID"));
				user.setUserPassword(rs.getString("USER_PASSWORD"));
				user.setUserName(rs.getString("USER_NAME"));
				user.setUserGender(rs.getString("USER_GENDER"));
				user.setUserPhone(rs.getString("USER_PHONE"));
				user.setUserState(rs.getString("USER_STATE"));
				list.add(user);
			}
			
			return list;
		} catch (SQLException e) {
			System.out.println("err: List<UserVO> selectFriendList(String userId): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	public Set<String> selectFriendWhoAddMe(String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_FRIEND_ADD_ME");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			Set<String> set = new HashSet<>();
			while(rs.next())
			{
				set.add(rs.getString(1));
			}
			return set;
			
		} catch (SQLException e) {
			System.out.println("err: selectFriendWhoAddMe(String userId)");
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	친구 추가
	public boolean insertFriendId(String myId, String friendId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("ADD_FRIEND");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, myId);
			pstmt.setString(2, friendId);
			
			int i = pstmt.executeUpdate();
			
			if(i==1)
			{
				return true;
			}
			else
			{
				return false;
			}
			
		} catch (SQLException e) {
			System.out.println("err: insertFriendId(String myId, String friendId): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	친구 삭제
	public boolean deleteFriendId(String myId, String friendId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("DELETE_FRIEND");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, myId);
			pstmt.setString(2, friendId);
			
			int i = pstmt.executeUpdate();
			
			if(i==1)
			{
				return true;
			}
			else
			{
				return false;
			}
			
		} catch (SQLException e) {
			System.out.println("err: deleteFriendId(String myId, String friendId)");
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	방목록 받아오기
	public List<RoomVO> selectRoomList(String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_ROOMS");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			List<RoomVO> list = new ArrayList<>();
			while(rs.next())
			{
				RoomVO room = new RoomVO();
				room.setRoomNum(rs.getInt("ROOM_NUM"));
				room.setRoomOwnerId(rs.getString("ROOM_OWNER_ID"));
				room.setRoomName(rs.getString("ROOM_NAME"));
				room.setRoomPlace(rs.getString("ROOM_PLACE"));
				room.setRoomStartDate(rs.getString("ROOM_START_DATE"));
				room.setRoomEndDate(rs.getString("ROOM_END_DATE"));
				room.setRoomMemberNum(rs.getInt("ROOM_MEMBER_NUM"));
				list.add(room);
			}
			
			return list;
			
		} catch (SQLException e) {
			System.out.println("err: selectRoomList(String userId): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	방 추가
	public RoomVO insertRoom(RoomVO room)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("INSERT_ROOM");
		String sql2 = sqlProps.getProperty("INSERT_ROOM_MEMBER_TABLE");
		String sql3 = sqlProps.getProperty("SELECT_INSERTED_ROOM");
		
		try {
			con = getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql1);
			
			pstmt.setString(1, room.getRoomOwnerId());
			pstmt.setString(2, room.getRoomName());
			pstmt.setString(3, room.getRoomPlace());
			pstmt.setString(4, room.getRoomStartDate());
			pstmt.setString(5, room.getRoomEndDate());
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, room.getRoomOwnerId());
			pstmt.setString(2, room.getRoomOwnerId());
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(sql3);
			pstmt.setString(1, room.getRoomOwnerId());
			
			rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				RoomVO resultRoom = new RoomVO();
				resultRoom.setRoomNum(rs.getInt("ROOM_NUM"));
				resultRoom.setRoomOwnerId(rs.getString("ROOM_OWNER_ID"));
				resultRoom.setRoomName(rs.getString("ROOM_NAME"));
				resultRoom.setRoomPlace(rs.getString("ROOM_PLACE"));
				resultRoom.setRoomStartDate(rs.getString("ROOM_START_DATE"));
				resultRoom.setRoomEndDate(rs.getString("ROOM_END_DATE"));
				resultRoom.setRoomMemberNum(rs.getInt("ROOM_MEMBER_NUM"));
				
				con.commit();
				
				return resultRoom;
			}
			else
			{
				System.out.println("err: insertRoom(RoomVO room): 커밋 전에 뭔가 오류가 있음");
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("err: insertRoom(RoomVO room): "+e.getMessage());
			try {con.rollback();} catch (SQLException e1) {System.out.println("err: insertRoom(RoomVO room): "+e1.getMessage());}
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	방 나가기 (퀴리4개....-_-)
	public String getOutRoom(String userId, int roomNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("SELECT_ROOM_OWNER");
		String sql2 = sqlProps.getProperty("DELETE_ROOM_MEMBER");
		String sql3 = sqlProps.getProperty("UPDATE_ROOM_OWNER");
		String sql4 = sqlProps.getProperty("UPDATE_ROOM_MEMBER_NUM");
		String sql5 = sqlProps.getProperty("DELETE_ROOM");
		
		try {
			con = getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql1);
			
			pstmt.setInt(1, roomNum);
			rs = pstmt.executeQuery();
			
			String ownerId;
			int roomMemberNum;
			if(rs.next())
			{
				ownerId = rs.getString("ROOM_OWNER_ID");
				roomMemberNum = rs.getInt("ROOM_MEMBER_NUM");
			}
			else
			{
				return "잘못된접근";
			}
			
//			삭제 진행
			pstmt = con.prepareStatement(sql2);
			pstmt.setInt(1, roomNum);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
			
//			방에 남은 인원이 1명이 아니었다면
			if(roomMemberNum!=1)
			{
//				나가는 사람이 방장이라면
				if(ownerId.equals(userId))
				{
//					새로운 방장 선임
					pstmt = con.prepareStatement(sql3);
					pstmt.setInt(1, roomNum);
					pstmt.setInt(2, roomNum);
					pstmt.executeUpdate();
				}
				
//				방 인원수 업뎃
				pstmt = con.prepareStatement(sql4);
				pstmt.setInt(1, roomNum);
				pstmt.setInt(2, roomNum);
				pstmt.executeUpdate();
				
				con.commit();
				
				return "아직사람남음";
			}
			else
			{
				pstmt = con.prepareStatement(sql5);
				pstmt.setInt(1, roomNum);
				pstmt.executeUpdate();
				
				con.commit();
				return "이제사람없음";
			}
			
		} catch (SQLException e) {
			System.out.println("err: getOutRoom(String userId, int roomNum): "+e.getMessage());
			try {con.rollback();} catch (SQLException e1) {System.out.println("err: getOutRoom(String userId, int roomNum): "+e1.getMessage());}
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	방 구성원 조회
	public List<UserVO> getRoomMemberList(int roomNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_ROOM_MEMBER_LIST");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, roomNum);
			rs = pstmt.executeQuery();
			
			List<UserVO> list = new ArrayList<>();
			while(rs.next())
			{
				UserVO user = new UserVO();
				user.setUserId(rs.getString("USER_ID"));
				user.setUserPassword(rs.getString("USER_PASSWORD"));
				user.setUserName(rs.getString("USER_NAME"));
				user.setUserGender(rs.getString("USER_GENDER"));
				user.setUserPhone(rs.getString("USER_PHONE"));
				user.setUserState(rs.getString("USER_STATE"));
				list.add(user);
			}
			
			return list;
			
		} catch (SQLException e) {
			System.out.println("err: getRoomMemberList(int roomNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	

//	방 하나 받기
	public RoomVO selectSingleRoom(int roomNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_ROOM");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, roomNum);
			
			rs = pstmt.executeQuery();
			
			RoomVO room = new RoomVO();
			if(rs.next())
			{
				room.setRoomNum(rs.getInt("ROOM_NUM"));
				room.setRoomOwnerId(rs.getString("ROOM_OWNER_ID"));
				room.setRoomName(rs.getString("ROOM_NAME"));
				room.setRoomPlace(rs.getString("ROOM_PLACE"));
				room.setRoomStartDate(rs.getString("ROOM_START_DATE"));
				room.setRoomEndDate(rs.getString("ROOM_END_DATE"));
				room.setRoomMemberNum(rs.getInt("ROOM_MEMBER_NUM"));
				return room;
			}
			else
			{
				return null;
			}
			
			
		} catch (SQLException e) {
			System.out.println("err: selectSingleRoom(int roomNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	방에 멤버 추가
	public boolean insertRoomMember(int roomNum, String userId)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("INSERT_ROOM_MEMBER");
		String sql2 = sqlProps.getProperty("UPDATE_ROOM_MEMBER_NUM");
		try {
			con = getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql1);
			
			pstmt.setInt(1, roomNum);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
			
//			방 인원수 업뎃
			pstmt = con.prepareStatement(sql2);
			pstmt.setInt(1, roomNum);
			pstmt.setInt(2, roomNum);
			int r = pstmt.executeUpdate();
			
			con.commit();
			
			if(r == 1)
				return true;
			else
				return false;
			
		} catch (SQLException e) {
			System.out.println("err: insertRoomMember(int roomNum, String userId): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	채팅 DB에 추가
	public void insertChat(int roomNum, String chatId, String chatMessage)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("INSERT_CHAT");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, roomNum);
			pstmt.setString(2, chatId);
			pstmt.setString(3, chatMessage);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("err: insertChat(int roomNum, String chatId, String chatMessage): "+e.getMessage());
			disConnect(con,pstmt,rs);
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	채팅방의 채팅리스트 불러오기
	public List<ChatVO> selectChatList(int roomNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_CHAT_LIST");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, roomNum);
			
			rs = pstmt.executeQuery();
			
			List<ChatVO> list = new ArrayList<>();
			while(rs.next())
			{
				ChatVO chat = new ChatVO();
				chat.setChatNum(rs.getInt("CHAT_NUM"));
				chat.setChatRoomNum(rs.getInt("ROOM_NUM"));
				chat.setChatMemberId(rs.getString("MEMBER_ID"));
				chat.setChatMessage(rs.getString("CHAT_MESSAGE"));
				chat.setChatTime(rs.getString("CHAT_TIME"));
				list.add(chat);
			}
			return list;
			
			
		} catch (SQLException e) {
			System.out.println("err: selectChatList(int roomNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	구분 목록 보내주기
	public List<String> selectPurposeNameList()
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_ALL_PURPOSE_NAME_LISE");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			List<String> list = new ArrayList<>();
			while(rs.next())
			{
				list.add(rs.getString("PURPOSE_NAME"));
			}
			return list;
			
		} catch (SQLException e) {
			System.out.println("err: selectPurposeNameList(): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	새로운 플랜을 더해주고 해당 플랜정보를 돌려주기
	public PlanVO insertPlanAndSelectPlan(PlanVO plan)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("INSERT_PLAN");
		String sql2 = sqlProps.getProperty("SELECT_PLAN_NOW");
		try {
			con = getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql1);

			pstmt.setInt(1, plan.getPlanRoomNum());
			pstmt.setString(2, plan.getPlanUserId());
			pstmt.setString(3, plan.getPlanPurposeName());
			pstmt.setInt(4, plan.getPlanMoney());
			pstmt.setString(5, plan.getPlanOther());
			pstmt.setString(6, plan.getPlanLink());
			
			pstmt.setString(7, plan.getPlanImgLoc());
			
			pstmt.setString(8, plan.getPlanDate());
			pstmt.setString(9, plan.getPlanName());
			pstmt.setString(10, plan.getPlanLoc());
			
			
			pstmt.executeUpdate();
			
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, plan.getPlanUserId());
			
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				PlanVO planResult = new PlanVO();
				
				planResult.setPlanNum(rs.getInt("PLAN_NUM"));
				planResult.setPlanRoomNum(rs.getInt("ROOM_NUM"));
				planResult.setPlanUserId(rs.getString("USER_ID"));
				planResult.setPlanPurposeNum(rs.getInt("PLAN_PURPOSE_NUM"));
				planResult.setPlanMoney(rs.getInt("PLAN_MONEY"));
				planResult.setPlanOther(rs.getString("PLAN_OTHER"));
				planResult.setPlanLink(rs.getString("PLAN_LINK"));
				planResult.setPlanImgLoc(rs.getString("PLAN_IMG_LOC"));
				planResult.setPlanState(rs.getString("PLAN_STATE"));
				planResult.setPlanDate(rs.getString("PLAN_DATE"));
				planResult.setPlanTime(rs.getString("PLAN_TIME"));
				planResult.setPlanRep(rs.getString("PLAN_REP"));
				planResult.setPlanAgreeNum(rs.getInt("PLAN_AGREE_NUM"));
				planResult.setPlanName(rs.getString("PLAN_NAME"));
				planResult.setPlanLoc(rs.getString("PLAN_LOC"));
				planResult.setPlanPurposeName(rs.getString("PURPOSE_NAME"));
				con.commit();
				return planResult;
			}
			else
			{
				con.rollback();
				return null;
			}
			
		} catch (SQLException e) {
			System.out.println("err: insertPlanAndSelectPlan(PlanVO plan): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	플랜들 전부 가져오기
	public List<PlanVO> SelectAllPlan(int roomNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_ALL_PLAN");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, roomNum);
			
			rs = pstmt.executeQuery();
			
			List<PlanVO> list = new ArrayList<>();
			while(rs.next())
			{
				PlanVO planResult = new PlanVO();
				
				planResult.setPlanNum(rs.getInt("PLAN_NUM"));
				planResult.setPlanRoomNum(rs.getInt("ROOM_NUM"));
				planResult.setPlanUserId(rs.getString("USER_ID"));
				planResult.setPlanPurposeNum(rs.getInt("PLAN_PURPOSE_NUM"));
				planResult.setPlanMoney(rs.getInt("PLAN_MONEY"));
				planResult.setPlanOther(rs.getString("PLAN_OTHER"));
				planResult.setPlanLink(rs.getString("PLAN_LINK"));
				planResult.setPlanImgLoc(rs.getString("PLAN_IMG_LOC"));
				planResult.setPlanState(rs.getString("PLAN_STATE"));
				planResult.setPlanDate(rs.getString("PLAN_DATE"));
				planResult.setPlanTime(rs.getString("PLAN_TIME"));
				planResult.setPlanRep(rs.getString("PLAN_REP"));
				planResult.setPlanAgreeNum(rs.getInt("PLAN_AGREE_NUM"));
				planResult.setPlanName(rs.getString("PLAN_NAME"));
				planResult.setPlanLoc(rs.getString("PLAN_LOC"));
				planResult.setPlanPurposeName(rs.getString("PURPOSE_NAME"));
				
				list.add(planResult);
			}
			
			return list;
			
		} catch (SQLException e) {
			System.out.println("err: SelectAllPlan(int roomNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	플랜번호 받아서 삭제
	public boolean deleteSinglePlan(int planNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("DELETE_SINGLE_PLAN");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, planNum);
			
			int r = pstmt.executeUpdate();
			
			if(r == 1)
				return true;
			else
				return false;
			
			
		} catch (SQLException e) {
			System.out.println("err: deleteSinglePlan(int planNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	플랜 투표로 업데이트
	public boolean updatePlanToVote(int planNum, String planTime, String planDate)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("UPDATE_PLAN_VOTE");
		String sql2 = sqlProps.getProperty("SELECT_AGREESET");
		String sql3 = sqlProps.getProperty("UPDATE_AGREESET");
		try {
			con = getConnection();
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(sql1);
			
			pstmt.setString(1, planTime);
			StringBuffer sbtemp = new StringBuffer(planDate);
			sbtemp.deleteCharAt(7);
			sbtemp.deleteCharAt(4);
			pstmt.setString(2, sbtemp.toString());
			pstmt.setInt(3, planNum);
			
			pstmt.executeUpdate();
			
			
			pstmt = con.prepareStatement(sql2);
			pstmt.setInt(1, planNum);
			rs = pstmt.executeQuery();
			rs.next();
			String Agrees = rs.getString("AGREESET");
			if(Agrees == null || Agrees.equals(""))
			{
				Agrees = ""+planNum;
			}
			else
			{
				Agrees += "/"+planNum;
			}
			
			
			pstmt = con.prepareStatement(sql3);
			pstmt.setString(1, Agrees);
			pstmt.setInt(2, planNum);
			
			int r = pstmt.executeUpdate();
			
			
			if(r == 1)
			{
				con.commit();
				return true;
			}
			else
			{
				con.rollback();
				return false;
			}
			
		} catch (SQLException e) {
			System.out.println("err: updatePlanToVote(int planNum): "+e.getMessage());
			try {con.rollback();} catch (SQLException e1) {}
			disConnect(con,pstmt,rs);
			return false;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
//	투표 동의상태 확인하고 동의 또는 최종처리
	public int updateAgreeNum(int planNum, String userId, int roomMemberNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql1 = sqlProps.getProperty("SELECT_AGREE_NUM");
		String sql2 = sqlProps.getProperty("SELECT_AGREESET_USER_ID");
		String sql3 = sqlProps.getProperty("UPDATE_AGREESET_USER_ID");
		String sql4 = sqlProps.getProperty("UPDATE_AGREE_NUM");
		String sql5 = sqlProps.getProperty("UPDATE_AGREE_NUM_FIN");
		int resultNum = -1;
		
		try {
			con = getConnection();
			con.setAutoCommit(false);
			
			
//			현재 동의 인원 수 가져오기
			pstmt = con.prepareStatement(sql1);
			pstmt.setInt(1, planNum);
			rs = pstmt.executeQuery();
			rs.next();
			int agreeNum = rs.getInt("PLAN_AGREE_NUM");
			agreeNum++;
			
//			요청 아이디의 동의 목록 가져와서 현재플랜 더하기 처리
			pstmt = con.prepareStatement(sql2);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			rs.next();
			String agreeSet = rs.getString("AGREESET");
			if(agreeSet == null || agreeSet.equals(""))
			{
				agreeSet = ""+planNum;
			}
			else
			{
				agreeSet += "/"+planNum;
			}
			
//			요청 아이디의 동의목록 갱신
			pstmt = con.prepareStatement(sql3);
			pstmt.setString(1, agreeSet);
			pstmt.setString(2, userId);
			pstmt.executeUpdate();
			
			
//			동의 숫자+1 해도 방 인원보다 적을떄
			if(agreeNum < roomMemberNum)
			{
				pstmt = con.prepareStatement(sql4);
				resultNum = 2;
			}
//			동의숫자+1 하면 방인원수와 같거나 클때...?
			else
			{
				pstmt = con.prepareStatement(sql5);
				resultNum = 1;
			}
			pstmt.setInt(1, planNum);
			int r = pstmt.executeUpdate();
			
			if(r == 1)
			{
				con.commit();
				return resultNum;
			}
			else
			{
				con.rollback();
				return -1;
			}
			
		} catch (SQLException e) {
			System.out.println("err: updateAgreeNum(int planNum, String userId, int roomMemberNum): "+e.getMessage());
			try {con.rollback();} catch (SQLException e1) {}
			disConnect(con,pstmt,rs);
			return -1;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
//	이미지 경로 받아오기
	public String selectPlanImgLoc(int planNum)
	{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = sqlProps.getProperty("SELECT_PLAN_IMG_LOC");
		try {
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, planNum);
			
			rs = pstmt.executeQuery();
			rs.next();
			String imgLoc = rs.getString("PLAN_IMG_LOC");
			return imgLoc;
			
			
		} catch (SQLException e) {
			System.out.println("err: selectPlanImgLoc(int planNum): "+e.getMessage());
			disConnect(con,pstmt,rs);
			return null;
		}
		finally
		{
			disConnect(con,pstmt,rs);
		}
	}
	
	
	
	
}
