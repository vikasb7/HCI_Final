package components;
import java.lang.Math;
public class collision_ball_paddle implements Commons{

	int collision_area;
	collision_ball_paddle(Paddle paddle,Ball ball){
		checkCollision (paddle,ball);
	}

	private void checkCollision(Paddle pad, Ball ball) {
		//collision of ball with paddle detection
		if ((ball.getRect()).intersects(pad.getRect())){
			pad.score++;
			if(pad.side==1 || pad.side==3){
				int paddlePos = (int) pad.getRect().getMinX();
				int ballPos = (int) ball.getRect().getCenterX();

				int up_down;
				if (pad.side==1){up_down = -1;}
				else{up_down = 1;}

				if( Math.abs(paddlePos-ballPos) < pad.i_width/5){
					ball.setXDir(-1);
					ball.setYDir(up_down);
				}
				else if (Math.abs(paddlePos-ballPos) > pad.i_width/5 && Math.abs(paddlePos-ballPos) < 2*pad.i_width/5){
					ball.setXDir(-0.5);
					ball.setYDir(up_down);
				}    				
				else if (Math.abs(paddlePos-ballPos) > 2*pad.i_width/5 && Math.abs(paddlePos-ballPos) < 3*pad.i_width/5){
					ball.setXDir(0.2);
					ball.setYDir(up_down);
				}
				else if (Math.abs(paddlePos-ballPos) > 3*pad.i_width/5 && Math.abs(paddlePos-ballPos) < 4*pad.i_width/5){
					ball.setXDir(0.5);
					ball.setYDir(up_down);
				} 
				else if (Math.abs(paddlePos-ballPos) > 4*pad.i_width/5 && Math.abs(paddlePos-ballPos) < pad.i_width){
					ball.setXDir(1);
					ball.setYDir(up_down);
				} 
			}

			else{
				int paddlePos = (int) pad.getRect().getMinY();
				int ballPos = (int) ball.getRect().getCenterY();

				int left_right;
				if (pad.side==2){left_right = 1;}
				else{left_right = -1;}


				if(Math.abs(paddlePos-ballPos) < pad.i_height/5){			
					ball.setYDir(-1);
					ball.setXDir(left_right);
				}    			
				else if (Math.abs(paddlePos-ballPos) > pad.i_height/5 && Math.abs(paddlePos-ballPos) < 2*pad.i_height/5){				
					ball.setYDir(-0.5);
					ball.setXDir(left_right);
				}    				
				else if (Math.abs(paddlePos-ballPos) > 2*pad.i_height/5 && Math.abs(paddlePos-ballPos) < 3*pad.i_height/5){
					ball.setYDir(0.2);
					ball.setXDir(left_right);
				}
				else if (Math.abs(paddlePos-ballPos) > 3*pad.i_height/5 && Math.abs(paddlePos-ballPos) < 4*pad.i_height/5){    					
					ball.setYDir(0.5);
					ball.setXDir(left_right);
				}
				else if (Math.abs(paddlePos-ballPos) > 4*pad.i_height/5 && Math.abs(paddlePos-ballPos) < 5*pad.i_height/5){			
					ball.setYDir(1);
					ball.setXDir(left_right);
				}

			}
		}
		//collision of ball with wall detection
		if(pad.side==1){
			if(ball.y >= HEIGHT - ball.i_height-30)
			{pad.life--;}
		}
		else if(pad.side==3)
		{
			if(ball.y <= 0)
			{pad.life--;}
		}
		else if(pad.side == 2){
			if(ball.x <= 0)
			{pad.life--;}
		}
		else if(pad.side == 4){
			if(ball.x >= WIDTH - ball.i_width)
			{pad.life--;}
		}

	}
}
