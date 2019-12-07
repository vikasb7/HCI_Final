package components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

import components.Board.TAdapter;

@SuppressWarnings("serial")
public class multiplayerBoard extends Board {
	
	Paddle otherPlyr;
	int noCPU;
	private boolean playing = true;
	ArrayList<Paddle> otherPlyr2 = new ArrayList<Paddle>();
	private boolean replaced = true;
	private boolean gameOver = false;
	network_methods netMethods;
	int playerNo, noOfPlayers;
	
	public multiplayerBoard(network_methods netMethods,int PlayerNo,int NoOfPlayers){
    	super();				//calls Board() to set window settings.
    	System.out.println("multiplyr board call "+PlayerNo+" "+NoOfPlayers);
    	this.playerNo = PlayerNo;
    	this.noOfPlayers = NoOfPlayers;
		this.netMethods = netMethods;	//provides the critical network link formed previously in MainGame.
		this.isMultiplayer = true;
		multiplayer();	//Thread to continuously read output stream.
    }
	
	@Override
	protected void gameInit(){
		if(MainGame.no_ofPlayer=="2")
        	noCPU = 1;
        else
        	noCPU = 3;
		
		life = new JLabel[noCPU+1];
		
		for(int i=0;i<noCPU+1;i++){
    		life[i] = new JLabel("lf");
    		add(life[i]);
    	}
		
		addKeyListener(new TAdapter());
		//game components initializing
		ball = new Ball();
		
		if(MainGame.isHost){
			for(int i=1;i<noCPU+1;i++){
	    		Paddle pad;
	    		if(noCPU == 1)
	    			pad = new Paddle(i+2);
	    		else
	    			pad = new Paddle(i+1);
	    		//otherPlyr2.add(pad);
	    		life[i].setText(""+pad.life+" ::");
	    	}
			user_paddle = new userPaddle(1);
			otherPlyr = new Paddle(3);
		}
		else{
			user_paddle = new userPaddle(3);
			otherPlyr = new Paddle(1);
		}
		//life[1].setText(""+otherPlyr.life);
		life[0].setText(""+user_paddle.life+" ::");
		timer = new Timer();
        timer.scheduleAtFixedRate(new mScheduleTask(), (DELAY), PERIOD);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(playing){
        	Graphics2D g2d = (Graphics2D) g;
        	if(!MainGame.isHost){
        	int xRot = this.getWidth() / 2;
    		int yRot = this.getHeight() / 2; 
    		//g2d.rotate(Math.toRadians(180), xRot, yRot);
    		}
        	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        			RenderingHints.VALUE_ANTIALIAS_ON);

        	g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        			RenderingHints.VALUE_RENDER_QUALITY);
        	g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
        	if(MainGame.isHost){
        		g.drawString(String.valueOf(user_paddle.life), 290, 550);
        		g.drawString(String.valueOf(otherPlyr.life), 290, 50);
        	}
        	else{
        		g.drawString(String.valueOf(otherPlyr.life), 290, 550);
            	g.drawString(String.valueOf(user_paddle.life), 290, 50);
            	
        	}
        	Toolkit.getDefaultToolkit().sync();
        	doDrawing(g2d);
        }
        else{
        	 g.setColor(Color.WHITE);
        	 g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
             g.drawString("Player "+paddlelose+" loses", 165, 250);
        }
    }
	
	@Override
	protected void doDrawing(Graphics2D g2d){
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight(), this);
        g2d.drawImage(user_paddle.getImage(), user_paddle.getX(), user_paddle.getY(), user_paddle.getWidth(), user_paddle.getHeight(), this);
        /*for(int i=0;i<noCPU;i++){
        	g2d.drawImage(otherPlyr2.get(i).getImage(), otherPlyr2.get(i).getX(), otherPlyr2.get(i).getY(), otherPlyr2.get(i).getWidth(), otherPlyr2.get(i).getHeight(), this);
        }*/
        g2d.drawImage(otherPlyr.getImage(), otherPlyr.getX(), otherPlyr.getY(), otherPlyr.getWidth(), otherPlyr.getHeight(), this);
	}
	
	double roundTwoDecimals(double d) {
		  DecimalFormat twoDForm = new DecimalFormat("###.##");
		  return Double.valueOf(twoDForm.format(d));
		}
	
	//TimerTask which modifies the multiplayer components initialized above.
	private class mScheduleTask extends TimerTask {

        @Override
        public void run() {
            
            if(otherPlyr.life==0||user_paddle.life==0){
        		if(otherPlyr.life==0)
        			paddlelose = ""+otherPlyr.side;
        		else if(user_paddle.life==0)
                	paddlelose = ""+user_paddle.side;
        		if(MainGame.isHost){
        			ball.stop();
        			gameOver = true;
            		playing = false;
        		}
        	}
        	else{
        		if(MainGame.isHost){
        			ball.move();
        		}
        		otherPlyr.move(ball);
        		new collision_ball_paddle(otherPlyr,ball);
        		life[1].setText(""+otherPlyr.life);
        		
        		user_paddle.move(ball);
        		new collision_ball_paddle(user_paddle,ball);
        		life[0].setText(""+user_paddle.life+" ::");
        	}
           
            try {	
    				if(MainGame.isHost){
    					netMethods.sendUdpMessage("bal "+roundTwoDecimals(ball.x)+" "+roundTwoDecimals(ball.y));
    				}
    				netMethods.sendUdpMessage("lif " + user_paddle.life);
    				netMethods.sendUdpMessage("pad "+roundTwoDecimals(user_paddle.x)+" "+roundTwoDecimals(user_paddle.y));
    		} catch (IOException ex) {
    			System.out.println("disconnected");
    			MainGame.difficulty = "medium";
    			if(replaced){
    				otherPlyr = new cpuPaddle(MainGame.isHost?3:1);
    				replaced = false;
    				MainGame.isHost = true;
    			}
    		}
            repaint();
        }
    }
	
	public void multiplayer(){
		multiplayerThread();
	}
	
	private void multiplayerThread(){
		new Thread(){
			public void run(){
				String line;
				try {
					//DataInputStream input = new DataInputStream(netMethods.socket.getInputStream());
					while((line = netMethods.getUdpMessage()) != null){
						//System.out.println("the line is :"+line);
						if(line.contains("pad")){
							String[] words = line.split(" ");
							otherPlyr.x = Double.parseDouble(words[1]);
							otherPlyr.y = Double.parseDouble(words[2]);
						}
						else if(line.contains("bal")){
							if(!MainGame.isHost){
								String[] words = line.split(" ");
								ball.x = Double.parseDouble(words[1]);
								ball.y = Double.parseDouble(words[2]);
							}
						}
						else if(line.contains("lif")){
							String[] words = line.split(" ");
							otherPlyr.life = Integer.parseInt(words[1]);
						}
					}
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("disconnected");
	    			MainGame.difficulty = "medium";
	    			if(replaced){
	    				otherPlyr = new cpuPaddle(MainGame.isHost?3:1);
	    				replaced = false;
	    				MainGame.isHost = true;
	    			}
				}
			}
		}.start();
	}
	public class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
        	user_paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
        	user_paddle.keyPressed(e);    	
        	int key = e.getKeyCode();
        	 if (key == KeyEvent.VK_R||key == KeyEvent.VK_E) {
                 new MainGame();
             }
        }
    }
}

