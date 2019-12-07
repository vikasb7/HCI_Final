package components;
import javax.swing.ImageIcon;

public class Ball extends Sprite implements Commons {

    private double xdir;
    private double ydir;

    public Ball() {

        xdir = 1;
        ydir = -1;

        ImageIcon ii = new ImageIcon("ball.png");
        image = ii.getImage();

        i_width = image.getWidth(null);
        i_height = image.getHeight(null);

        resetState();
    }

    public void move() {
    	double speed;
        if(MainGame.difficulty == "hard")
        	speed = 1.6;
        else if(MainGame.difficulty == "medium")
        	speed = 1.3;
        else
        	speed = 1;
        x += (xdir*speed);
        y += (ydir*speed);
        
        /*
        * Setting the direction of movement of the ball i.e., xdir and ydir
        * based on the collision condition with the wall
        */
        if (x <= 0) {
            setXDir(1);
        }

        if (x >= WIDTH - i_width) {
            setXDir(-1);
        }
        if (y <= 0) {
            setYDir(1);
        }
        if (y >= HEIGHT - i_height-30) {
            setYDir(-1);
        }
    }
    
    public void stop() {
    	this.x = x;
    	this.y = y;
    }

    private void resetState() {
        x = INIT_BALL_X;
        y = INIT_BALL_Y;
    }

    public void setXDir(double x) {
        xdir = x;
    }

    public void setYDir(double y) {
        ydir = y;
    }

    public int getYDir() {
    	Double a = new Double(ydir);
    	int Y = a.intValue();
        return Y;
    }
    
    public int getXDir() {
    	Double a = new Double(xdir);
    	int X = a.intValue();
        return X;
    }
}