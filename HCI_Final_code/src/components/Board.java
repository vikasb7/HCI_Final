package components;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;

import components.collision_ball_paddle;


@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {
	
	int noCPU;
	String paddlelose="";
	protected Timer timer;
    ArrayList<Paddle> paddle = new ArrayList<Paddle>();
    userPaddle user_paddle;
    Ball ball;
    public static final int DELAY = 1000;
    public static final int PERIOD = 10;
    //public JLabel score[];
    public JLabel life[];
    protected boolean isMultiplayer = false;
    private boolean playing = true;
    private boolean gameOver = false;
    
    public Board() {
    	initBoard();
    }
    private void initBoard() {
    	//score = new JLabel[noCPU+1];
        setFocusable(true);
        setBackground(new Color(171,143,228,255));
        gameInit();
    }
    
    protected void gameInit(){

    	if(MainGame.no_ofPlayer=="2")
        	noCPU = 1;
        else
        	noCPU = 3;
    	
    	life = new JLabel[noCPU+1];
    	for(int i=0;i<noCPU+1;i++){
    		//score[i] = new JLabel("sc");
    		life[i] = new JLabel("lf");
    		//add(score[i]);
    		add(life[i]);
    	}

    	addKeyListener(new TAdapter());
    	//game components initializing
    	ball = new Ball();
    	for(int i=1;i<noCPU+1;i++){
    		cpuPaddle pad;
    		if(noCPU == 1)
    			pad = new cpuPaddle(i+2);
    		else
    			pad = new cpuPaddle(i+1);
    		paddle.add(pad);
    		//score[i].setText((""+pad.score));
    		if(i == noCPU)
    			life[i].setText(""+pad.life);
    		else	
    			life[i].setText(""+pad.life+" ::");
    	}
    	user_paddle = new userPaddle(1);
    	//score[0].setText(""+user_paddle.score);
    	life[0].setText(""+user_paddle.life+" ::");
    	
    	timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), DELAY, PERIOD);
    }


    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	if(!isMultiplayer){
    		if(playing){
    			Graphics2D g2d = (Graphics2D) g;

    			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    					RenderingHints.VALUE_ANTIALIAS_ON);

    			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
    					RenderingHints.VALUE_RENDER_QUALITY);

    			doDrawing(g2d);
    			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
    			g.drawString(String.valueOf(user_paddle.life), 290, 550);
    			if(noCPU==1){
    				g.drawString(String.valueOf(paddle.get(0).life), 290, 50);
    			}
    			else{
    				g.drawString(String.valueOf(paddle.get(0).life), 50, 290);
    				g.drawString(String.valueOf(paddle.get(1).life), 290, 50);
    				g.drawString(String.valueOf(paddle.get(2).life), 550, 290);
    			}

    			Toolkit.getDefaultToolkit().sync();
    		}
    		else{
    			g.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
    			g.drawString("Press R to replay", 100, 50);
    			g.setColor(Color.WHITE);
    			g.setFont(new Font(Font.DIALOG, Font.BOLD, 36));
    			g.drawString("Player "+paddlelose+" loses", 165, 250);
    		}
    	}
    }

    protected void doDrawing(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;  
        
        g.setColor(new Color(204,0,102,255));
        g2d.fillOval(300-35,287-35, 70, 70);
        g.setColor(new Color(109,67,192,255));
        BasicStroke bs1 = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(bs1);
        g2d.drawRect(1,1,597,575);
        float[] dash = { 4f, 4f, 1f };
        BasicStroke bs2 = new BasicStroke(1, BasicStroke.CAP_BUTT, 
        	    BasicStroke.JOIN_ROUND, 1.0f, dash, 2f );
        g2d.setStroke(bs2);
        g2d.drawLine(20,20,300,287);
        g2d.drawLine(20,555,300,287);
        g2d.drawLine(580,20,300,287);
        g2d.drawLine(580,555,300,287);
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),ball.getWidth(), ball.getHeight(), this);
        for(int i=0;i<noCPU;i++){
        	g2d.drawImage(paddle.get(i).getImage(), paddle.get(i).getX(), paddle.get(i).getY(), paddle.get(i).getWidth(), paddle.get(i).getHeight(), this);
        }
        g2d.drawImage(user_paddle.getImage(), user_paddle.getX(), user_paddle.getY(), user_paddle.getWidth(), user_paddle.getHeight(), this);
    }
    
    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {
        	if(MainGame.no_ofPlayer=="4"){
            	if(paddle.get(0).life==0||paddle.get(1).life==0||paddle.get(2).life==0||user_paddle.life==0){
            		if(paddle.get(0).life==0)
            			paddlelose = ""+paddle.get(0).side;
            		else if(paddle.get(1).life==0)
            			paddlelose = ""+paddle.get(1).side;
            		else if(paddle.get(2).life==0)
            			paddlelose = ""+paddle.get(2).side;
            		else if(user_paddle.life==0)
                    	paddlelose = ""+user_paddle.side;
            		ball.stop();
            		gameOver = true;
            		playing = false;
            	}
            	else{
            		ball.move();
                    for(int i=0;i<noCPU;i++){
                    	paddle.get(i).move(ball);
                    	new collision_ball_paddle(paddle.get(i),ball);
                    	//score[i+1].setText(""+paddle.get(i).score);
                    	if(i == noCPU-1)
                    		life[i+1].setText(""+paddle.get(i).life);
                    	else
                    		life[i+1].setText(""+paddle.get(i).life+" ::");
                    }
                    user_paddle.move(ball);
                    new collision_ball_paddle(user_paddle,ball);
                    //score[0].setText(""+user_paddle.score);
                    life[0].setText(""+user_paddle.life+" ::");
            	}
        	}
        	else{
        		if(paddle.get(0).life==0||user_paddle.life==0){
            		if(paddle.get(0).life==0)
            			paddlelose = ""+paddle.get(0).side;
            		else if(user_paddle.life==0)
                    	paddlelose = ""+user_paddle.side;
            		ball.stop();
            		gameOver = true;
            		playing = false;
            	}
            	else{
            		ball.move();
                    for(int i=0;i<noCPU;i++){
                    	paddle.get(i).move(ball);
                    	new collision_ball_paddle(paddle.get(i),ball);
                    	//score[i+1].setText(""+paddle.get(i).score);
                    	if(i == noCPU-1)
                    		life[i+1].setText(""+paddle.get(i).life);
                    	else
                    		life[i+1].setText(""+paddle.get(i).life+" ::");
                    }
                    user_paddle.move(ball);
                    new collision_ball_paddle(user_paddle,ball);
                    //score[0].setText(""+user_paddle.score);
                    life[0].setText(""+user_paddle.life+" ::");
            	}
        	}
                repaint(); 
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        //paddle.move(ball);
        repaint();  
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