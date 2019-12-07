package components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

public class network_methods implements Commons {
	protected boolean isHost;
	protected BufferedWriter output;
	protected BufferedReader input;
	protected String hostName;
	protected String clientName;
	Socket socket;
	protected String ip;
	ArrayList<String> sendIps;
	private JFrame frame;
	DatagramSocket toSocket;
	DatagramSocket fromSocket;
	ArrayList<Integer> port;
	int playerNo,noOfPlayers;
	
	public network_methods(Socket socket, String hostName, String clientName, BufferedReader input, BufferedWriter output) {
		this.socket = socket;
		this.hostName = hostName;
		this.clientName = clientName;
		this.input = input;
		this.output = output;
		isHost = false;
	}

	public network_methods(String username,JFrame frame, int noOfPlayers){
		this.frame = frame;
		this.hostName = username;
		this.noOfPlayers = noOfPlayers;
		port = new ArrayList<>(Arrays.asList(9122,9123,9124,9125));
		sendIps = new ArrayList<String>();
		
	}
	
	protected void start(){
		if(isHost){
			try {
				System.out.println("entered start is host");
				exchangeIps();
				System.out.println("sent ips");
				sendUdpMessage("startgame");
				System.out.println("sent start msg");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("couldn't send startgame message");
			}
		}
		System.out.println("new maingame("+ this.playerNo+" " +this.noOfPlayers+")");
		new MainGame(frame,this,this.playerNo,this.noOfPlayers);	//start multi-player I/O loop.
	}
	
	private void exchangeIps() throws IOException{
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < sendIps.size(); i++) {
		   strBuilder.append(sendIps.get(i)+" ");
		}
		String ipString = strBuilder.toString();
		sendUdpMessage(ipString);
	}
	
	public final void lookForPlayers() throws IOException{
		this.isHost = true;
		
		for (int i=0;i<(noOfPlayers-1);i=i+1){
			
															//open a server socket
			ServerSocket ss = new ServerSocket(GAMEPORT);
			System.out.println("Waiting for a player to join at "+ InetAddress.getLocalHost());
			
			Socket s = ss.accept();							//wait for player to join
			this.socket = s;
			
			ip = s.getInetAddress().getHostAddress();		//get and store client ip
			System.out.println("client ip is stored as: "+ip);
															//set up input/output streams
			input = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
															//send name of game and player's name
			output.write(i+" hostname: " + hostName +"\n");
			output.flush();
															//user sends their name back
			clientName = getMessage(30000);
			System.out.println("Player "+clientName+" has been found! Starting game.");
			sendIps.add(ip);
			s.close();
			ss.close();
		}
		this.toSocket = new DatagramSocket();
		this.fromSocket = new DatagramSocket(port.get(0));
		port.remove(0);
		this.fromSocket.setSoTimeout(10000);
		start();
	}
	
	public final void joinGame(String clientName, Socket socket) throws IOException{
		this.isHost = false;
		ip = socket.getInetAddress().getHostAddress();
		this.socket = socket;
		System.out.println("host ip is stored as: "+ip);
		
		//establish streams
		input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		String in = getMessage(input, 30000);
		String[] words = in.split(" ");
		String hostName = words[2];
		playerNo = Integer.parseInt(words[0]);
		
		System.out.println("playerNo " + playerNo);
		System.out.println("Host: " + hostName);
		
		output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));		
		output.write(clientName + "\n");
		output.flush();
		
		socket.close(); //end of TCP connection
		
		this.fromSocket = new DatagramSocket(port.get(playerNo+1));
		port.remove(playerNo+1);
		this.toSocket = new DatagramSocket();
		
		String ipString = getIpMessage();
		System.out.println("recvd ips");
		
		words = ipString.split(" ");
		for(int i=0;i<words.length;i++){
			System.out.println(words[i]);
			sendIps.add(words[i]);
		}
		sendIps.remove(playerNo);
		sendIps.add(0,ip);
		
		String toStart = getUdpMessage();
		if(toStart.contains("startgame")){
			this.fromSocket.setSoTimeout(10000);
			start();
		}
	}
	
	public void exitGame() {
		toSocket.close();
		fromSocket.close();
	}
	
	public void sendMessage(String message) throws IOException {
		output.write(message + "\n");
		//output.flush();
	}
	
	private static String getMessage(BufferedReader input, long timeout) throws IOException {
		int count = 0;
		long maxCount = timeout / 100;
		while(!input.ready()) {
			System.out.println("input not ready");
			try{Thread.sleep(100);}catch(Exception e){}
			count++;
			//after 30 seconds just give up!
			if(count > maxCount){
				throw new IOException("Timed out after " + timeout + " milliseconds");
			}
		}
		String in = input.readLine();
		System.out.println(in);
		return in;
	}
	
	public String getMessage(long timeout) throws IOException {
		return network_methods.getMessage(input, timeout);
	}
	
	//----------------**UDP Methods**------------------\\
	
	public String getUdpMessage() throws IOException, SocketTimeoutException{
			DatagramPacket receivePacket = new DatagramPacket(new byte[18],18);
			fromSocket.receive(receivePacket);
			String s = new String(receivePacket.getData(),0,receivePacket.getLength());
			//System.out.println("rcvd msg: "+ s);
			return s;	
	}
	
	public String getIpMessage() throws IOException, SocketTimeoutException{
		DatagramPacket receivePacket = new DatagramPacket(new byte[50],50);
		fromSocket.receive(receivePacket);
		String s = new String(receivePacket.getData(),0,receivePacket.getLength());
		//System.out.println("rcvd msg: "+ s);
		return s;	
}
		
	public void sendUdpMessage(String message) throws IOException{
		try {
			byte[] sendBytes = message.getBytes("UTF-8");
			DatagramPacket sendPacket;
			for(int i=0;i<this.noOfPlayers-1;i=i+1){
				sendPacket = new DatagramPacket(sendBytes,sendBytes.length,InetAddress.getByName(sendIps.get(i)),port.get(i));
				toSocket.send(sendPacket);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}


