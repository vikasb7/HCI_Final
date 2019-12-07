package components;
import java.awt.Image;
import java.awt.Rectangle;

public class Sprite {

    protected double x;
    protected double y;
    protected int i_width;
    protected int i_height;
    protected Image image;

    public void setX(double x) {
        this.x = x;
    }

    public int getX() {
    	Double a = new Double(x);
    	int X = a.intValue();
        return X;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getY() {
    	Double a = new Double(y);
    	int Y = a.intValue();
        return Y;
    }

    public int getWidth() {
        return i_width;
    }

    public int getHeight() {
        return i_height;
    }

    Image getImage() {
        return image;
    }
  
    Rectangle getRect() {
    	Double a = new Double(x);
    	int X = a.intValue();
    	Double b = new Double(y);
    	int Y = b.intValue();
        return new Rectangle(X, Y,
                image.getWidth(null), image.getHeight(null));
    }
   
}