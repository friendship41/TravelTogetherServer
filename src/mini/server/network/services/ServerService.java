package mini.server.network.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javafx.application.Platform;
import javafx.stage.Stage;
import mini.server.database.TravelTogetherDAO;
import mini.server.network.controllers.MainController;
import mini.server.network.staticValues.ConnectionStaticValues;
import mini.server.network.staticValues.NetworkProtocolHeads;

public class ServerService 
{
	MainController mainController;
	Stage primaryStage;
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	ServerSocketChannel fileServerSocketChannel = null;
	List<Client> connections = new Vector<>();
	IRequestProcessService iRequestProcessService;
	
	
	public ServerService(MainController mainController) {
		this.mainController = mainController;
		this.primaryStage = mainController.getPrimaryStage();
	}

//	Platform.runLater(()->mainController.displayToTA(""));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public void startServer()
	{	
		iRequestProcessService = new RequestProcessService();
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(ConnectionStaticValues.SERVER_INET_ADDRESS,ConnectionStaticValues.SERVER_PORT));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			if(serverSocketChannel.isOpen())
				stopServer();
			mainController.displayToTA("selector, 서버소켓채널 생성중 에러");
			System.out.println("selector, 서버소켓채널 생성중 에러");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			return;
		}
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while(true)
				{
					try {
						int keyCnt = selector.select();
						if(keyCnt == 0)
							continue;
						Set<SelectionKey> selectedKeys = selector.selectedKeys();
						Iterator<SelectionKey> iter = selectedKeys.iterator();
						while(iter.hasNext())
						{
							SelectionKey selectionKey = iter.next();
							if(selectionKey.isAcceptable())
							{
								accept(selectionKey);
							}
							else if(selectionKey.isReadable())
							{
								Client client = (Client)selectionKey.attachment();
								client.recieve(selectionKey);
							}
							else if(selectionKey.isWritable())
							{
								Client client = (Client)selectionKey.attachment();
								client.send(selectionKey);
							}
							iter.remove();
						}
						
					} catch (IOException e) {
						Platform.runLater(()->mainController.displayToTA("스레드에서 키 가져와서 메서드 호출 도중 에러"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						System.out.println("스레드에서 키 가져와서 메서드 호출 도중 에러");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						if(serverSocketChannel.isOpen())
							stopServer();
						break;
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
		Platform.runLater(()->mainController.displayToTA("[서버 시작]"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		System.out.println("[서버 시작]");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}
	
	public void stopServer()
	{
		try {
		Iterator<Client> iter = connections.iterator();
			while (iter.hasNext()) {

				Client client = iter.next();
				client.socketChannel.close();
				iter.remove();
			}
			if(serverSocketChannel != null && serverSocketChannel.isOpen())
			{
				serverSocketChannel.close();
			}
			if(selector != null && selector.isOpen())
				selector.close();
			
			Platform.runLater(()->mainController.displayToTA("[서버 닫음]"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			System.out.println("[서버 닫음]");			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		} 
		catch (IOException e) {
			Platform.runLater(()->mainController.displayToTA("서버닫는중 에러"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			System.out.println("서버닫는중 에러");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
	
	void accept(SelectionKey selectionKey)
	{
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			
			String message = "[연결 수락: "+socketChannel.getRemoteAddress()+": "+Thread.currentThread().getName()+"]";
			Platform.runLater(()->mainController.displayToTA(message));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			System.out.println(message);		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			
			Client client = new Client(socketChannel);
			connections.add(client);
			
			Platform.runLater(()->mainController.displayToTA("[현재 연결 개수: "+connections.size()+" ]"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			System.out.println("[현재 연결 개수: "+connections.size()+" ]");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		} 
		catch (IOException e) {
			Platform.runLater(()->mainController.displayToTA("accept 도중 에러"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			System.out.println("accept 도중 에러");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(serverSocketChannel.isOpen())
				stopServer();
		}
		
	}
	
	class Client
	{
		SocketChannel socketChannel;
		TravelTogetherDAO travelTogetherDAO;
		String clientId;
		String sendData;
		
		public Client() {
		}
		public Client(SocketChannel socketChannel) throws IOException {
			this.socketChannel = socketChannel;
			socketChannel.configureBlocking(false);
			SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
			selectionKey.attach(this);
			travelTogetherDAO = TravelTogetherDAO.getTravelTogetherDAO();
		}
		
//		데이터 리시브
		void recieve(SelectionKey selectionKey)
		{
			try {
				ByteBuffer byteBuffer = ByteBuffer.allocate(500);
				
				int byteCnt = socketChannel.read(byteBuffer);
				
				if(byteCnt == -1)
					throw new IOException();
				
				String message = "[요청 처리: "+socketChannel.getRemoteAddress()+": "+Thread.currentThread().getName()+"/ ID: "+this.clientId+"]";
//				Platform.runLater(()->mainController.displayToTA(message));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				System.out.println(message);		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				
				byteBuffer.flip();
				Charset charset = Charset.forName("UTF-8");
				String data = charset.decode(byteBuffer).toString();
//========================================================================================================================
//				여기부터 데이터 받은걸로 처리해서 객체data로 저장 및 발송 준비
				if(data.length() <4)
					return;
				
				Platform.runLater(()->mainController.displayToTA("받은 데이타: "+data));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				System.out.println("받은 데이타: "+data);		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				
				
				
				String head = data.substring(0,4);
				int headNum = Integer.parseInt(data.substring(0, 4));
				


				String protocol = new String(data);

				switch(headNum)
				{
//				파일이 넘어온다면...?
				// 9999:'파일 이름'
				case 9999:
//					**************************************************************************************************************
//					**************************************************************************************************************
//					파일이 넘어오고있다아아아아아아아아아
					Thread fileThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								if(fileServerSocketChannel == null)
								{
									fileServerSocketChannel = ServerSocketChannel.open();
									fileServerSocketChannel.bind(new InetSocketAddress(ConnectionStaticValues.SERVER_INET_ADDRESS, ConnectionStaticValues.FILE_SERVER_PORT));
								}
								SocketChannel fileSocketChannel = fileServerSocketChannel.accept();

								String fileName = "";
								Charset charset = Charset.forName("UTF-8");
								ByteBuffer fileNameByteBuffer = ByteBuffer.allocate(500);
								int byteCnt = fileSocketChannel.read(fileNameByteBuffer);
								
								fileNameByteBuffer.flip();
								fileName += charset.decode(fileNameByteBuffer).toString();
								
								String filePath = ConnectionStaticValues.SERVER_IMGS_LOC+fileName.trim();
								
								File file = new File(filePath);
								System.out.println(file.getPath());

								ByteBuffer getImgBuffer = ByteBuffer.allocate(100000);
								int bytesRead = fileSocketChannel.read(getImgBuffer);
								FileOutputStream getImgFileOutputStream = new FileOutputStream(file);
								FileChannel getImgFileChannel = getImgFileOutputStream.getChannel();

								while(bytesRead != -1) 
								{
									getImgBuffer.flip();
									getImgFileChannel.write(getImgBuffer);
									getImgBuffer.compact();
									bytesRead = fileSocketChannel.read(getImgBuffer);
								}
								
								
								
								
								getImgFileChannel.close();
								getImgFileOutputStream.close();
								fileSocketChannel.close();
								
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return;
						}
					});
					fileThread.setDaemon(true);
					fileThread.start();
					
					
					return;
//					**************************************************************************************************************
//					**************************************************************************************************************
					
				
//				아이디 중복확인이 들어왔을떄
				case NetworkProtocolHeads.CHECK_ID_PROTOCOL :
					if(iRequestProcessService.checkDuplId(data.substring(5).trim()))
						this.sendData = NetworkProtocolHeads.ID_DUPLICATE_CHECK_RECEIVE_PROTOCOL+":false";
					else
						this.sendData = "1051:true";
					break;
					
//				회원가입 요청이 들어왔을때
				case NetworkProtocolHeads.SIGNUP_PROTOCOL :
					if(iRequestProcessService.signUp(data))
						this.sendData = NetworkProtocolHeads.SIGNUP_RESULT_RECEIVE_PROTOCOL+":true";
					else
						this.sendData = NetworkProtocolHeads.SIGNUP_RESULT_RECEIVE_PROTOCOL+":false";
					break;
					
//				친구 테이블 받아오기
				case NetworkProtocolHeads.GET_FRIEND_LIST_PROTOCOL :
					String frList = iRequestProcessService.getFriendList(data);
					if(frList!=null)
						this.sendData = NetworkProtocolHeads.FRIEND_LIST_RECEIVE_PROTOCOL+frList;
					else
						this.sendData = NetworkProtocolHeads.FRIEND_LIST_RECEIVE_PROTOCOL+":";
					break;
					
//				로그인 체크 (비밀번호 확인)
				case NetworkProtocolHeads.LOGIN_PROTOCOL :
					String result = iRequestProcessService.loginCheck(protocol);
					if(result.charAt(1)=='1')
					{
						StringTokenizer st =new StringTokenizer(result, ":");
						st.nextToken();
						this.clientId = st.nextToken();
//						친구 아이디 찾아서 접속중 띄워주기
						Set<String> tempSet = iRequestProcessService.getSomeoneAddmeStringSet(this.clientId);
						for(Client client : connections)
						{
							if(tempSet.contains(client.clientId))
							{
								client.sendData = NetworkProtocolHeads.FRIEND_LOGIN_RECEIVE_PROTOCOL+":"+this.clientId;
								SelectionKey key = client.socketChannel.keyFor(selector);
								key.interestOps(SelectionKey.OP_WRITE);
							}
						}
					}
					this.sendData = NetworkProtocolHeads.LOGIN_RESULT_RECEIVE_PROTOCOL+result;
					break;
					
//				쪽지 날라오면 해당 사람에게 보내주기
				case NetworkProtocolHeads.SEND_MESSAGE_PROTOCOL :
					// 2001:'아이디from':'아이디to':'메세지내용'
					// 2051:'아이디from':'아이디to':'메세지내용'
					StringTokenizer st = new StringTokenizer(data, ":");
					st.nextToken();
					String from = new String(st.nextToken());
					String to = new String(st.nextToken());
					String receivedMessage = new String(st.nextToken());
					StringBuffer sb = new StringBuffer();
					sb.append(NetworkProtocolHeads.MESSAGE_RECEIVE_PROTOCOL);
					sb.append(":");
					sb.append(to);
					sb.append(":");
					sb.append(from);
					sb.append(":");
					sb.append(receivedMessage);
					for(Client client : connections)
					{
						if(client.clientId.equals(to))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = sb.toString();
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
							return;
						}
					}
					break;
					
//				친구 추가 요청 처리
				case NetworkProtocolHeads.ADD_FRIEND_PROTOCOL :
					this.sendData = NetworkProtocolHeads.ADD_FRIEND_RESULT_RECEIVE_PROTOCOL+iRequestProcessService.addFriend(protocol);
					break;
					
//				친구 삭제
					// 3003:'친구아이디'
					// 3055:true
				case NetworkProtocolHeads.DELETE_FRIEND_PROTOCOL :
					this.sendData = NetworkProtocolHeads.DELETE_FRIEND_RESULT_RECEIVE_PROTOCOL+":"+iRequestProcessService.deleteFriend(this.clientId, protocol);
					break;
//				방 리스트 받아오기 요청 처리
					// 4001:'내아이디'
					// 4054:'room_num':'room_owner_id':'room_name':'room_place':'start_date':'end_date':'room_member_num'
				case NetworkProtocolHeads.GET_ROOM_LIST_PROTOCOL :
					this.sendData = NetworkProtocolHeads.ROOM_LIST_RECEIVE_PROTOCOL+iRequestProcessService.getRoomList(protocol);
					break;
//				방 추가 요청 처리
					// 4002:'내아이디':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE'
					// 4051:'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM'
				case NetworkProtocolHeads.ADD_ROOM_PROTOCOL :
					String roomResult = iRequestProcessService.getAddRoomResult(protocol);
					if(roomResult!=null)
					{
						this.sendData = NetworkProtocolHeads.ADD_ROOM_RESULT_RECEIVE_PROTOCOL + roomResult;
					}
					else
					{
						this.sendData = NetworkProtocolHeads.ADD_ROOM_RESULT_RECEIVE_PROTOCOL + ":error";
					}
					break;
					
//				방나가기 오청 처리
					// 4003:'내아이디':'방번호'
					// 4053:true
				case NetworkProtocolHeads.GET_OUT_ROOM_PROTOCOL : 
//					삭제 결과값 받아오기 (1. 에러, 2. 방에 사람 남았음, 3. 방에 사람없음)
					String deleteRoomResult = iRequestProcessService.getOutRoom(protocol);
					if(deleteRoomResult.equals("1"))
					{
						this.sendData = NetworkProtocolHeads.GET_OUT_ROOM_RECEIVE_PROTOCOL + ":false";
					}
					else if(deleteRoomResult.equals("2"))
					{
						this.sendData = NetworkProtocolHeads.GET_OUT_ROOM_RECEIVE_PROTOCOL + ":true";
//						방 사람들에게 업뎃 하라고 전달**********************************************************************************************************
//						for(Client client : connections)
//						{
//							if(client.clientId.equals(to))
//							{
//								System.out.println("SEND TO : "+client.clientId);
//								client.sendData = sb.toString();
//								SelectionKey key = client.socketChannel.keyFor(selector);
//								key.interestOps(SelectionKey.OP_WRITE);
//							}
//						}
//						********************************************************************************************************************************
					}
					else
					{
						this.sendData = NetworkProtocolHeads.GET_OUT_ROOM_RECEIVE_PROTOCOL + ":true";
					}
					break;
					
//				방 들어갈떄 방 멤버 조회해서 가져오기
					// 5001:'방번호'
					// 5051:'멤버1아이디':'멤버1이름':'멤버1상태':'멤버2아이디':'멤버2이름':'멤버2상태'....
				case NetworkProtocolHeads.GET_ROOM_MEMBER_LIST_PROTOCOL :
					String roomMemberResult = iRequestProcessService.getRoomMemberList(protocol);
					this.sendData = NetworkProtocolHeads.ROOM_MEMBER_LIST_RECEIVE_PROTOCOL+roomMemberResult;
					break;
					
//				방에 멤버 추가하는 요청 (추가 요청을 받아서 해당 아이디로 초대 메세지 보냄)
					// 5003:'내아이디':'방번호':'여행지':'친구아이디' -> 4052
					// 4052:'초대한사람':'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM' -> 5004
					// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num'
					// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
				case NetworkProtocolHeads.ADD_ROOM_MEMBER_PROTOCOL :
					String[] armp = protocol.split(":");
					String inviteMessage = iRequestProcessService.getInviteMessage(protocol);
					
					if(inviteMessage == null)
					{
						// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
						this.sendData = NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL+":2:"+armp[4];
					}
					else
					{
						boolean flag = false;
						// 4052:'초대한사람':'ROOM_NUM':'ROOM_OWNER_ID':'ROOM_NAME':'ROOM_PLACE':'ROOM_START_DATE':'ROOM_END_DATE':'ROOM_MEMBER_NUM' -> 5004
						for(Client client : connections)
						{
							if(client.clientId == null)
								continue;
							if(client.clientId.equals(armp[4]))
							{
								System.out.println("SEND TO : "+client.clientId);
								client.sendData = NetworkProtocolHeads.ROOM_INVITE_RECEIVE_PROTOCOL+inviteMessage;
								flag = true;
								SelectionKey key = client.socketChannel.keyFor(selector);
								key.interestOps(SelectionKey.OP_WRITE);
							}
//							친구가 접속중이라면 즉시리턴 / 아니라면 친구 없다고 보냄
							if(flag)
							{
								return;
							}
							else
							{
								// 5053:'4'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
								this.sendData = NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL+":4:"+armp[4];
							}
						}
					}
					break;
					
//				방 초대 답장이 서버로 왔을때 처리
					// 5004:'T'(수락여부):'초대받은아이디':'초대한아이디':'room_num'
					// 5053:'2'(1:성공,2:친구id가없음,3:친구가거절, 4:친구가 미접속):'친구아이디':'친구이름':'친구상태'
				case NetworkProtocolHeads.REPLY_ROOM_INVITE_PROTOCOL :
					String replyRoomInvite = iRequestProcessService.replyRoomInvite(protocol);
					String[] temp1 = protocol.split(":");
					String replyTo = temp1[3];
					for(Client client : connections)
					{
						if(client.clientId.equals(replyTo))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = NetworkProtocolHeads.ADD_ROOM_MEMBER_RESULT_RECEIVE_PROTOCOL+replyRoomInvite; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
							return;
						}
					}
					break;
					
//				채팅 왔을때 처리
					// 5002:'내아이디':'방번호':'메세지' -> 5052
					// 5052:'방번호':'채팅한사람':'채팅내용'
				case NetworkProtocolHeads.SEND_CHAT_PROTOCOL :
					String chatResult = iRequestProcessService.chatProcess(protocol);
					String[] temp5002 = protocol.split(":");
					int roomNum5002 = Integer.parseInt(temp5002[2]);
					Set<String> roomMemberSet = iRequestProcessService.getRoomMemberSet(roomNum5002);
//					채팅방 멤버들 셋으로 받아와서 해당되는 애들만 보내기
					for(Client client : connections)
					{
						if(client.clientId == null)
							continue;
						if(roomMemberSet.contains(client.clientId))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = NetworkProtocolHeads.CHAT_RECEIVE_RECEIVE_PROTOCOL+chatResult; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					return;
					
//				방 입장시 이전 채팅들 불러오기
					// 5005:'채팅방번호'
					// 5054:'방번호1':'채팅시간1':'채팅한사람1':'채팅내용1':'채팅시간2':'채팅한사람2':'채팅내용2'....
				case NetworkProtocolHeads.GET_CHAT_LIST_PROTOCOL :
					this.sendData = NetworkProtocolHeads.CHAT_LIST_RECEIVE_PROTOCOL+iRequestProcessService.getChatList(protocol);
					break;
					
//				이미지 파일을 달라는 요청이 온다...
					//* 6001:'플랜번호'
				case NetworkProtocolHeads.GET_PLAN_IMG_PROTOCOL :
					iRequestProcessService.sendImgToClient(protocol);
					return;
					
//				콤보박스 리스트 달라하면 던져주기
					// 6005:
					// 6053:'구분1':'구분2':'구분3'....
				case NetworkProtocolHeads.GET_COMBOBOX_PURPOSE_LIST_PROTOCOL :
					this.sendData = NetworkProtocolHeads.COMBOBOX_PURPOSE_LIST_RECEIVE_PROTOCOL+iRequestProcessService.getComboBoxList();
					break;
					
//				새로운 플랜 들어오면 DB저장하고 뿌려주기
					// 6003:'작성자':'방번호':'제목':'구분':'예상지출':'좌표':'기타':'링크':'날짜':'이미지명' -> 6051
					// 6051:'플랜번호':'제목':'플랜방번호':'작성자':'구분':'예상지출':'좌표':'기타':'링크'
				case NetworkProtocolHeads.ADD_CANDIDATE_PLAN_PROTOCOL :
					String sendString6051 = NetworkProtocolHeads.NEW_PLAN_RECEIVE_PROTOCOL+iRequestProcessService.getAddPlanResponse(protocol);
					this.sendData = sendString6051;
//					이 방에 속해있는 사람들중 접속한 사람들에게 주기
					String[] roomNum6003Arr = protocol.split(":");
					Set<String> roomUserSet = iRequestProcessService.getRoomMemberSet(Integer.parseInt(roomNum6003Arr[2]));
					for(Client client : connections)
					{
						if(client.clientId == null)
							continue;
						if(roomUserSet.contains(client.clientId))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = sendString6051; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					break;
					
//				특정 방에 있는 모든 플랜들 주기
					// 6002:'방번호'	-> 6054
					// 6054:'플렌번호':'플랜명':'플랜방번호':'작성자':'구분':'예상지출':'기타':'링크':'좌표':'플랜상태':'플랜날짜':'플랜:시간':'대표여부':'동의자수'
				case NetworkProtocolHeads.GET_ALL_PLAN_LIST_PROTOCOL :
					this.sendData = NetworkProtocolHeads.ALL_PLAN_LIST_RECEIVE_PROTOCOL+iRequestProcessService.getAllPlanList(Integer.parseInt(protocol.substring(5).trim()));
					break;
					
//				플랜 삭제요청 들어왔을떄 삭제하고 뿌려주기
					// 6004:'플렌번호':'방번호'	-> 6052
					// 6052:'플랜번호'
				case NetworkProtocolHeads.DELETE_CANDIDATE_PLAN_PROTOCOL :
					String[] roomNum6004Arr = protocol.split(":");
					String sendString6052 = NetworkProtocolHeads.DELETE_CANDIDATE_PLAN_RECEIVE_PROTOCOL+iRequestProcessService.deleteSinglePlan(Integer.parseInt(roomNum6004Arr[1])); 
					this.sendData = sendString6052;
					Set<String> roomUserSet6052 = iRequestProcessService.getRoomMemberSet(Integer.parseInt(roomNum6004Arr[2]));
					for(Client client : connections)
					{
						if(client.clientId == null)
							continue;
						if(roomUserSet6052.contains(client.clientId))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = sendString6052; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					break;
					
//				플랜 투표 요청 처리 후 각 방 사람들에게 뿌리기
					// 6102:'플렌번호':'플랜:시간':'방번호':'플랜날짜' -> 6151
					// 6151:'플랜번호':'플랜시간':'플랜날짜'
				case NetworkProtocolHeads.ADD_PLAN_TO_VOTE_PROTOCOL :
					String[] roomNum6102 = protocol.split(":");
					String sendString6102 = NetworkProtocolHeads.ADD_PLAN_TO_VOTE_RECEIVE_PROTOCOL + iRequestProcessService.updatePlanToVote(protocol);
					this.sendData = sendString6102;
					Set<String> roomUserSet6102 = iRequestProcessService.getRoomMemberSet(Integer.parseInt(roomNum6102[4]));
					System.out.println(roomUserSet6102);
					for(Client client : connections)
					{
						if(client.clientId == null)
							continue;
						if(roomUserSet6102.contains(client.clientId))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = sendString6102; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					break;
					
//				투표플랜 동의 요청 처리하고 뿌려주기
					// 6103:'방번호':'플랜번호':'동의유저ID':'방인원수' -> 6152
					// 6152:1(1.모두동의, 2.아직남음):'플랜번호':'동의유저ID'
				case NetworkProtocolHeads.AGREE_PLAN_PROTOCOL :
					String[] protocolToken6103 = protocol.split(":");
					String sendString6152 = NetworkProtocolHeads.AGREE_PLAN_RECEIVE_PROTOCOL+iRequestProcessService.agreePlan(protocol);
					this.sendData = sendString6152;
					Set<String> roomUserSet6103 = iRequestProcessService.getRoomMemberSet(Integer.parseInt(protocolToken6103[1]));
					for(Client client : connections)
					{
						if(client.clientId == null)
							continue;
						if(roomUserSet6103.contains(client.clientId))
						{
							System.out.println("SEND TO : "+client.clientId);
							client.sendData = sendString6152; 
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					break;
					
					
					
				} // 프로토콜 머리에 따른 스위치
				
				
				
				
				SelectionKey key = this.socketChannel.keyFor(selector);
				key.interestOps(SelectionKey.OP_WRITE);
				
				/* 모두에게 보내야 할떄 쓰는 부분
				for(Client client : connections)
				{
					if(data.substring(0, 4).equals("1001"))
						client.sendData = "1051:true";
					else if(data.substring(0, 4).equals("1002"))
						client.sendData = "1052:false";
					SelectionKey key = client.socketChannel.keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
				}*/
				
				selector.wakeup();
			} 
			catch (IOException e) {	
				Platform.runLater(()->mainController.displayToTA("리시브 도중 에러"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				System.out.println("리시브 도중 에러");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				try {
//					***********************************************************************************************************
//					클라이언트 끊어져서 로그아웃 처리랑 그런거 보냄
//					***********************************************************************************************************
					Set<String> tempSet = iRequestProcessService.getSomeoneAddmeStringSet(this.clientId);
					for(Client client : connections)
					{
						if(tempSet.contains(client.clientId))
						{
							client.sendData = NetworkProtocolHeads.FRIEND_LOGOUT_RECEIVE_PROTOCOL+":"+this.clientId;
							SelectionKey key = client.socketChannel.keyFor(selector);
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
					iRequestProcessService.logout(this.clientId);
					
					
					connections.remove(this);
					String message = "[클라이언트 통신 안됨: "+socketChannel.getRemoteAddress()+": "+Thread.currentThread().getName()+"]";
					Platform.runLater(()->mainController.displayToTA(message));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println(message);	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					
					Platform.runLater(()->mainController.displayToTA("[현재 연결 개수: "+connections.size()+" ]"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println("[현재 연결 개수: "+connections.size()+" ]");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					socketChannel.close();
				} catch (IOException e1) {
					Platform.runLater(()->mainController.displayToTA("총체적 난국"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println("총체적 난국");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
		}	//receive()
		
		
//		문자열 보내기
		void send(SelectionKey selectionKey)
		{
			try {
				Charset charset = Charset.forName("UTF-8");
				ByteBuffer byteBuffer = charset.encode(sendData);
				socketChannel.write(byteBuffer);
				selectionKey.interestOps(SelectionKey.OP_READ);
				selector.wakeup();
			} catch (IOException e) {
				try {
					connections.remove(this);
					String message = "[클라이언트 통신 안됨: "+socketChannel.getRemoteAddress()+": "+Thread.currentThread().getName()+"]";
					Platform.runLater(()->mainController.displayToTA(message));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println(message);		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					socketChannel.close();
				} catch (IOException e1) {
					Platform.runLater(()->mainController.displayToTA("총체적 난국2"));		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
					System.out.println("총체적 난국2");		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
			}
			
		}	// send()
		
	}

	
	
	
	
	
	
	
	
} // class
