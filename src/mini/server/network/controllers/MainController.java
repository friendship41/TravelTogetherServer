package mini.server.network.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mini.server.network.services.ServerService;
import mini.server.network.staticValues.ConnectionStaticValues;

public class MainController extends Application
{
	ServerService serverService;
	Stage primaryStage;
	
	@FXML private TextArea taMain;
	@FXML private Button btnOnOff;
	@FXML private Button btnLog;
	
	public MainController getMainController()
	{
		return this;
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		serverService = new ServerService(this);
		
		try {
			this.primaryStage = primaryStage;
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("../../../../fxml/ServerUI.fxml"));
			AnchorPane mainUI = (AnchorPane)loader.load();
			
			btnOnOff = (Button) mainUI.lookup("#btnOnOff");
			btnLog = (Button) mainUI.lookup("#btnLog");
			taMain = (TextArea) mainUI.lookup("#taMain");
			
			Scene mainScene = new Scene(mainUI);
			this.primaryStage.setScene(mainScene);
			this.primaryStage.setTitle("서버");
			this.primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		서버 온오프 버튼
		btnOnOff.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(btnOnOff.getText().equals("Start"))
					{
						serverService.startServer();
						Platform.runLater(()->{
							btnOnOff.setText("Stop");
						});
					}
					else
					{
						serverService.stopServer();
						Platform.runLater(()->{
							btnOnOff.setText("Start");
						});
					}
			}
		});
		
		btnLog.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) 
			{
				Date now = new Date();
				BufferedWriter bw = null;
				StringBuffer sb = new StringBuffer(now.toString().substring(4, 19).replace(' ', '-'));
				sb.replace(9, 10, "_");
				sb.replace(12, 13, "");
				sb.insert(0, ConnectionStaticValues.SERVER_LOG_LOC);
				sb.append(".log");
				File file = new File(sb.toString());
				System.out.println(file.getPath());
				try {
					bw = new BufferedWriter(new FileWriter(file));
					bw.write(taMain.getText());
					bw.flush();
				} catch (IOException e) {
					System.out.println("서버 로그 저장중 오류 : "+e.getMessage());	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				}
				finally
				{
					try {if(bw != null) bw.close();} catch (IOException e) {}
				}
			}
		});
		
	}
	
	public void displayToTA(String str)
	{
		taMain.appendText(str+"\n");
	}
	

}