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
        repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    class MyPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            model.update();

            g.setColor(model.starMode ? Color.ORANGE : new Color(255, 192, 203));
            g.fillRect(0, 0, getWidth(), getHeight());

            int pipeWidth = 50;
            int gap = 120;
            g.drawImage(model.pipe.img, model.pipe.x, 0, pipeWidth, model.pipe.y, this);
            int lowerY = model.pipe.y + gap;
            g.drawImage(model.pipe.img, model.pipe.x, lowerY, pipeWidth, getHeight() - lowerY, this);

            g.drawImage(model.item.img, model.item.x, model.item.y, 30, 30, this);

            for (Effect ef : model.effects) {
                int size = 40 + (ef.maxTimer - ef.timer) * 4;
                g.drawImage(ef.img, ef.x - size/2, ef.y - size/2, size, size, this);
            }

            g.drawImage(model.bird.img, model.bird.x, model.bird.y, 40, 40, this);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 16));

            if (model.state == GameState.TITLE) {
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("CHICKEN & NUGGETS", 80, 160);
                g.setFont(new Font("monospaced", Font.PLAIN, 20));
                g.drawString("Press SPACE to Start", 130, 210);
                g.drawString("Press Q to End", 130, 240);
            } else {
                g.drawString("Pipes: " + model.pipesPassed + " | Coins: " + model.coinScore, 10, 20);
                if (model.starMode) g.drawString("STAR TIME: " + model.starTimer, 10, 60);
            }

            if (model.state == GameState.GAMEOVER) {
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.setColor(Color.RED);
                g.drawString("GAME OVER", 130, 180);
                g.setFont(new Font("monospaced", Font.PLAIN, 20));
                g.setColor(Color.BLACK);
                g.drawString("Press SPACE to Title", 135, 230);
            }

            if (model.state == GameState.PAUSED) {
                // g.setColor(new Color(0, 0, 0, 100));
                // g.fillRect(0, 0, getWidth(), getHeight());
                // g.setColor(Color.WHITE);
                // g.setFont(new Font("Arial", Font.BOLD, 30));
                // g.drawString("DEBUG PAUSE", 140, 200);
            }
        }
    }
}
