import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

enum ItemType { GOLD, STAR }
enum GameState { TITLE, PLAYING, GAMEOVER, PAUSED }

public class Model {
    Bird bird;
    Pipe pipe;
    Item item;
    List<Effect> effects = new ArrayList<>();
    Image effectImg;

    GameState state = GameState.TITLE;
    int pipesPassed = 0;
    int coinScore = 0;
    boolean starMode = false;
    int starTimer = 0;
    Random rand = new Random();

    public Model() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        bird = new Bird(50, 200, tk.getImage(getClass().getResource("/bird.png")));
        pipe = new Pipe(400, rand.nextInt(200) + 50, tk.getImage(getClass().getResource("/pipe.png")));
        item = new Item(550, 150, tk.getImage(getClass().getResource("/item.png")), ItemType.GOLD);
        effectImg = tk.getImage(getClass().getResource("/effect.png"));
    }

    public void onSpacePressed() {
        switch (state) {
            case TITLE: 
                state = GameState.PLAYING; 
                break;
            case PLAYING: 
                bird.jump(); 
                break;
            case GAMEOVER: 
                reset(); 
                state = GameState.TITLE;
                break;
        }
    }

    public void reset() {
        this.pipesPassed = 0;
        this.coinScore = 0;
        this.starMode = false;
        this.starTimer = 0;
        this.effects.clear();
        this.bird.y = 200;
        this.bird.velocity = 0;
        this.pipe.x = 400;
        this.item.x = 550;
    }

    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    public void update() {
        if (state != GameState.PLAYING) return;

        bird.update();
        
        int speed = starMode ? 12 : 7;
        pipe.setSpeed(speed);
        item.setSpeed(speed);
        pipe.update();
        item.update();

        Iterator<Effect> it = effects.iterator();
        while (it.hasNext()) {
            if (!it.next().update()) it.remove();
        }

        if (pipe.x < -50) {
            pipe.x = 500;
            pipe.y = rand.nextInt(200) + 50;
            pipesPassed++;
        }

        if (item.x < -50) {
            item.x = 500 + rand.nextInt(100);
            item.y = rand.nextInt(300);
            item.type = (rand.nextInt(5) == 0) ? ItemType.STAR : ItemType.GOLD;
        }

        if (!starMode) {
            int pipeWidth = 50;
            int gap = 120;
            if (bird.x + 25 > pipe.x && bird.x + 5 < pipe.x + pipeWidth) {
                if (bird.y + 5 < pipe.y || bird.y + 25 > pipe.y + gap) {
                    state = GameState.GAMEOVER;
                }
            }
        }

        if (Math.abs(bird.x - item.x) < 30 && Math.abs(bird.y - item.y) < 30) {
            if (item.type == ItemType.STAR) {
                starMode = true;
                starTimer = 100;
                effects.add(new Effect(item.x, item.y, 20, effectImg));
            } else {
                coinScore++;
            }
            item.x = -100;
        }

        if (starMode && --starTimer <= 0) starMode = false;
        if (bird.y > 400 || bird.y < 0) state = GameState.GAMEOVER;
    }
}

abstract class Charactor {
    int x, y;
    Image img;
    int speed = 7;
    Charactor(int x, int y, Image img) { this.x = x; this.y = y; this.img = img; }
    public abstract void update();
}

class Bird extends Charactor {
    double velocity = 0;
    Bird(int x, int y, Image img) { super(x, y, img); }
    @Override public void update() { 
        y += (int)velocity; 
        velocity += 0.9;
    }
    public void jump() { velocity = -12; }
}

class Pipe extends Charactor {
    Pipe(int x, int y, Image img) { super(x, y, img); }
    public void setSpeed(int s) { this.speed = s; }
    @Override public void update() { x -= speed; }
}

class Item extends Charactor {
    ItemType type;
    Item(int x, int y, Image img, ItemType type) { super(x, y, img); this.type = type; }
    public void setSpeed(int s) { this.speed = s; }
    @Override public void update() { x -= speed; }
}

class Effect {
    int x, y, timer, maxTimer;
    Image img;
    Effect(int x, int y, int duration, Image img) {
        this.x = x; this.y = y; this.timer = duration;
        this.maxTimer = duration; this.img = img;
    }
    public boolean update() { timer--; return timer > 0; }
}
