package components;

import java.awt.event.KeyEvent;

public class userPaddle extends Paddle{

	public userPaddle(int side) {
		super(side);
	}
	
	@Override
	public void move(Ball b){
		if(this.x >= 0 && this.x <= (WIDTH-this.i_width)){
			this.x += this.dx;
		}
		else if(this.x<0)
			this.x = 1;
		else if(this.x > WIDTH-this.i_width)
			this.x = WIDTH-this.i_width-1;
	}
	
	public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            this.dx = -2;
        }

        if (key == KeyEvent.VK_RIGHT) {
        	this.dx = 2;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            this.dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
        	this.dx = 0;
        }
    }
	
}
