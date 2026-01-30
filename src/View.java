import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class View extends JFrame implements ActionListener {
    private Model model;
    private MyPanel panel;
    private Timer timer;

    public View(Model model) {
        this.model = model;
        panel = new MyPanel();
        panel.setPreferredSize(new Dimension(500, 400));
        getContentPane().add(panel);
        this.timer = new Timer(16, this);
        this.timer.start();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        model.update();
        repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    class MyPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(model.isStarMode() ? Color.ORANGE : new Color(255, 192, 203));
            g.fillRect(0, 0, getWidth(), getHeight());

            int pipeWidth = 50;
            int gap = 120;
            Pipe p = model.getPipe();
            g.drawImage(p.getImg(), p.getX(), 0, pipeWidth, p.getY(), this);
            g.drawImage(p.getImg(), p.getX(), p.getY() + gap, pipeWidth, getHeight() - (p.getY() + gap), this);

            Item itm = model.getItem();
            g.drawImage(itm.getImg(), itm.getX(), itm.getY(), 30, 30, this);

            for (Effect ef : model.getEffects()) {
                int size = 40 + (ef.getMaxTimer() - ef.getTimer()) * 4;
                g.drawImage(ef.getImg(), ef.getX() - size/2, ef.getY() - size/2, size, size, this);
            }

            Bird b = model.getBird();
            g.drawImage(b.getImg(), b.getX(), b.getY(), 40, 40, this);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 16));

            if (model.getState() == GameState.TITLE) {
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("CHICKEN & NUGGETS", 80, 160);
                g.setFont(new Font("monospaced", Font.PLAIN, 20));
                g.drawString("Press SPACE to Start", 130, 210);
                g.drawString("Press Q to End", 130, 240);
            } else {
                g.drawString("Pipes: " + model.getPipesPassed() + " | Chicken: " + model.getChickenScore(), 10, 20);
                if (model.isStarMode()) g.drawString("STAR TIME: " + model.getStarTimer(), 10, 60);
            }

            if (model.getState() == GameState.GAMEOVER) {
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 130, 180);
                g.setFont(new Font("monospaced", Font.PLAIN, 20));
                g.setColor(Color.BLACK);
                g.drawString("Press SPACE to Title", 135, 230);
            }
        }
    }
}
