package mini.server.network.staticValues;

public interface NetworkProtocolHeads 
{
//	public static final int _PROTOCOL = "";
//	public static final int _RECEIVE_PROTOCOL = "";
	
//	1000번대 로그인 관련==============================================================================================================
	public static final int CHECK_ID_PROTOCOL = 1001;							// 1001:'아이디'
	public static final int SIGNUP_PROTOCOL = 1002;								// 1002:'아이디':'비밀번호':'이름':'성별':'폰번호'
	public static final int LOGIN_PROTOCOL = 1003;								// 1003:'아이디':'비밀번호'
	public static final int LOGOUT_PROTOCOL = 1004;
	
	public static final int ID_DUPLICATE_CHECK_RECEIVE_PROTOCOL = 1051;			// 1051:true
	public static final int SIGNUP_RESULT_RECEIVE_PROTOCOL = 1052;				// 1052:true
//	public static final int CHECK_PASSWORD_RESULT_RECEIVE_PROTOCOL = 1053;
	public static final int LOGIN_RESULT_RECEIVE_PROTOCOL = 1054;				// 1054:1(1:로그인성공, 2:비번오류, 3:아이디없음):'내이아디':'내이름':'내성별':'내폰번호':'동의플랜'
	
	
//	2000번대 쪽지 관련===============================================================================================================
	public static final int SEND_MESSAGE_PROTOCOL = 2001;						// 2001:'아이디from':'아이디to':'메세지내용'
	
	public static final int MESSAGE_RECEIVE_PROTOCOL = 2051;					// 2051:'아이디from':'아이디to':'메세지내용'
	
//	3000번대 친구 관리 관련============================================================================================================
	public static final int GET_FRIEND_LIST_PROTOCOL = 3001;					// 3001:'내아이디'
	public static final int ADD_FRIEND_PROTOCOL = 3002;							// 3002:'내아이디':'친구아이디'
	public static final int DELETE_FRIEND_PROTOCOL = 3003;						// 3003:'친구아이디'
	
	public static final int ADD_FRIEND_RESULT_RECEIVE_PROTOCOL = 3051;			// 3051:T(친구추가성공여부):'친구아이디':'친구이름':'T(친구상태)'
	public static final int FRIEND_LOGIN_RECEIVE_PROTOCOL = 3052;				// 3052:'친구아이디'
	public static final int FRIEND_LOGOUT_RECEIVE_PROTOCOL = 3053;				// 3053:'친구아이디'
	public static final int FRIEND_LIST_RECEIVE_PROTOCOL = 3054;				// 3054:'친구1아이디':'친구1이름':'친구1상태':'친구2아이디':'친구2이름':'친구2상태':....
	public static final int DELETE_FRIEND_RESULT_RECEIVE_PROTOCOL = 3055;		// 3055:true
	
//	4000번대 방 관리 관련=============================================================================================================
	public static final int GET_ROOM_LIST_PROTOCOL = 4001;						// 4001:'내아이디'
	public static final int ADD_ROOM_PROTOCOL = 4002;							// 4002:'내아이디':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE'
	public static final int GET_OUT_ROOM_PROTOCOL = 4003;						// 4003:'내아이디':'방번호'
	
	public static final int ADD_ROOM_RESULT_RECEIVE_PROTOCOL = 4051;			// 4051:'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM'
	public static final int ROOM_INVITE_RECEIVE_PROTOCOL = 4052;				// 4052:'초대한사람':'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM' -> 5004
	public static final int GET_OUT_ROOM_RECEIVE_PROTOCOL = 4053;				// 4053:true
	public static final int ROOM_LIST_RECEIVE_PROTOCOL = 4054;					// 4054:'room_num':'room_owner_id':'room_name':'room_place':'start_date':'end_date':'room_member_num'
//	public static final int ROOM_MEMBER_CHANGE_PROTOCOL = 4055;					
	
//	5000번대 채팅방 관련=============================================================================================================
	public static final int GET_ROOM_MEMBER_LIST_PROTOCOL = 5001;				// 5001:'방번호'
	public static final int SEND_CHAT_PROTOCOL = 5002;							// 5002:'내아이디':'방번호':'메세지' -> 5052
	public static final int ADD_ROOM_MEMBER_PROTOCOL = 5003;					// 5003:'내아이디':'방번호':'여행지':'친구아이디' -> 4052
	public static final int REPLY_ROOM_INVITE_PROTOCOL = 5004;					// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num' -> 5053
	public static final int GET_CHAT_LIST_PROTOCOL = 5005;						// 5005:'채팅방번호'
	
	public static final int ROOM_MEMBER_LIST_RECEIVE_PROTOCOL = 5051;			// 5051:'멤버1아이디':'멤버1이름':'멤버1상태':'멤버2아이디':'멤버2이름':'멤버2상태'....
	public static final int CHAT_RECEIVE_RECEIVE_PROTOCOL = 5052; 				// 5052:'방번호':'채팅한사람':'채팅내용'
	public static final int ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL = 5053;		// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
	public static final int CHAT_LIST_RECEIVE_PROTOCOL = 5054;					// 5054:'방번호':'채팅시간1':'채팅한사람1':'채팅내용1':'채팅시간2':'채팅한사람2':'채팅내용2'....
	
//	6000번대 계획 관련 (6000 : 후보지 관련, 6100 : 투표란 관련, 6200 : 확정 리스트 관련)===========================================================
	public static final int GET_PLAN_IMG_PROTOCOL = 6001;						//* 6001:'플랜번호'
	public static final int GET_ALL_PLAN_LIST_PROTOCOL = 6002;					// 6002:'방번호'
	public static final int ADD_CANDIDATE_PLAN_PROTOCOL = 6003;					// 6003:'작성자':'방번호':'제목':'구분':'예상지출':'좌표':'기타':'링크':'날짜':'이미지명' -> 6051
	public static final int DELETE_CANDIDATE_PLAN_PROTOCOL = 6004;				// 6004:'플렌번호'
	public static final int GET_COMBOBOX_PURPOSE_LIST_PROTOCOL = 6005;			// 6005:
	
	public static final int ADD_PLAN_TO_VOTE_PROTOCOL = 6102;					// 6102:'플렌번호':'플랜:시간':'방번호':'플랜날짜' -> 6151
	public static final int AGREE_PLAN_PROTOCOL = 6103;							// 6103:'방번호':'플랜번호':'동의유저ID':'방인원수' -> 6152
	
	
	
	
	public static final int NEW_PLAN_RECEIVE_PROTOCOL = 6051;					// 6051:'플랜번호':'제목':'플랜방번호':'작성자':'구분':'예상지출':'좌표':'기타':'링크'
	public static final int DELETE_CANDIDATE_PLAN_RECEIVE_PROTOCOL = 6052;		// 6052:'플랜번호'
	public static final int COMBOBOX_PURPOSE_LIST_RECEIVE_PROTOCOL = 6053;		// 6053:'구분1':'구분2':'구분3'....
	public static final int ALL_PLAN_LIST_RECEIVE_PROTOCOL = 6054;				// 6054:'플렌번호':'플랜명':'플랜방번호':'작성자':'구분':'예상지출':'기타':'링크':'좌표':'플랜상태':'플랜날짜':'플랜:시간':'대표여부':'동의자수'
	
	public static final int ADD_PLAN_TO_VOTE_RECEIVE_PROTOCOL = 6151;			// 6151:'플랜번호':'플랜시간':'플랜날짜'
	public static final int AGREE_PLAN_RECEIVE_PROTOCOL = 6152;					// 6152:1(1.모두동의, 2.아직남음):'플랜번호':'동의유저ID'
	
	
	
	
//	9000번대 오류 및 기타=============================================================================================================
	public static final int MESSAGE_VO = 9001;
	public static final int PLAN_VO = 9002;
	public static final int ROOM_VO = 9003;
	public static final int USER_VO = 9004;
	public static final int BOOLEAN = 9005;
	public static final int STRING = 9006;
	public static final int USER_VO_LIST = 9007;
	public static final int ROOM_VO_LIST = 9008;
	public static final int PLAN_VO_LIST = 9009;
	public static final int CHAT_VO = 9010;
	public static final int CHAT_VO_LIST = 9011;
	public static final int STRING_LIST = 9012;
	
	public static final int FILE_SEND_PROTOCOL = 9999;							// 9999:'파일 이름'
}
