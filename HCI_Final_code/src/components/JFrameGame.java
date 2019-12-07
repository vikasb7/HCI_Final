package components;

import javax.swing.JFrame;

public class JFrameGame extends JFrame implements Commons {
	
	
	public JFrameGame() {
        initUI();
    }
	
	private void initUI() {
			add(new Board());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Commons.WIDTH, Commons.HEIGHT);
        setTitle("Pong!");
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
	}
}
