import java.awt.event.*;

public class Controller extends KeyAdapter {
    private Model model;

    public Controller(Model model, View view) {
        this.model = model;
        view.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            model.onSpacePressed();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            model.togglePause();
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            System.exit(0);
        }
    }
}
