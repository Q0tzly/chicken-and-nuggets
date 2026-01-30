import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

enum ItemType { GOLD, STAR }
enum GameState { TITLE, PLAYING, GAMEOVER, PAUSED }

public class Model {
    private Bird bird;
    private Pipe pipe;
    private Item item;
    private List<Effect> effects = new ArrayList<>();
    private Image effectImg;

    private GameState state = GameState.TITLE;
    private int pipesPassed = 0;
    private int chickenScore = 0;
    private boolean starMode = false;
    private int starTimer = 0;
    private Random rand = new Random();

    public Model() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        bird = new Bird(50, 200, tk.getImage(getClass().getResource("/bird.png")));
        pipe = new Pipe(400, rand.nextInt(200) + 50, tk.getImage(getClass().getResource("/pipe.png")));
        item = new Item(550, 150, tk.getImage(getClass().getResource("/item.png")), ItemType.GOLD);
        effectImg = tk.getImage(getClass().getResource("/effect.png"));
    }

    // --- ゲッター ---
    public Bird getBird() { return bird; }
    public Pipe getPipe() { return pipe; }
    public Item getItem() { return item; }
    public List<Effect> getEffects() { return effects; }
    public GameState getState() { return state; }
    public int getPipesPassed() { return pipesPassed; }
    public int getChickenScore() { return chickenScore; }
    public boolean isStarMode() { return starMode; }
    public int getStarTimer() { return starTimer; }

    public void onSpacePressed() {
        switch (state) {
            case TITLE: state = GameState.PLAYING; break;
            case PLAYING: bird.jump(); break;
            case GAMEOVER: reset(); state = GameState.TITLE; break;
        }
    }

    public void reset() {
        this.pipesPassed = 0;
        this.chickenScore = 0;
        this.starMode = false;
        this.starTimer = 0;
        this.effects.clear();
        this.bird.resetPosition(50, 200);
        this.pipe.setX(400);
        this.item.setX(550);
    }

    public void togglePause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED;
        else if (state == GameState.PAUSED) state = GameState.PLAYING;
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

        if (pipe.getX() < -50) {
            pipe.setX(500);
            pipe.setY(rand.nextInt(200) + 50);
            pipesPassed++;
        }

        if (item.getX() < -50) {
            item.setX(500 + rand.nextInt(100));
            item.setY(rand.nextInt(300));
            item.setType((rand.nextInt(5) == 0) ? ItemType.STAR : ItemType.GOLD);
        }

        if (!starMode) {
            int pipeWidth = 50;
            int gap = 120;
            if (bird.getX() + 25 > pipe.getX() && bird.getX() + 5 < pipe.getX() + pipeWidth) {
                if (bird.getY() + 5 < pipe.getY() || bird.getY() + 25 > pipe.getY() + gap) {
                    state = GameState.GAMEOVER;
                }
            }
        }

        if (Math.abs(bird.getX() - item.getX()) < 30 && Math.abs(bird.getY() - item.getY()) < 30) {
            if (item.getType() == ItemType.STAR) {
                starMode = true;
                starTimer = 100;
                effects.add(new Effect(item.getX(), item.getY(), 20, effectImg));
            } else {
                chickenScore++;
            }
            item.setX(-100);
        }

        if (starMode && --starTimer <= 0) starMode = false;
        if (bird.getY() > 400 || bird.getY() < 0) state = GameState.GAMEOVER;
    }
}

abstract class Charactor {
    private int x, y;
    private Image img;
    protected int speed = 7;
    Charactor(int x, int y, Image img) { this.x = x; this.y = y; this.img = img; }
    public abstract void update();
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public Image getImg() { return img; }
}

class Bird extends Charactor {
    private double velocity = 0;
    Bird(int x, int y, Image img) { super(x, y, img); }
    @Override public void update() {
        setY(getY() + (int)velocity);
        velocity += 0.9;
    }
    public void jump() { velocity = -12; }
    public void resetPosition(int x, int y) { setX(x); setY(y); velocity = 0; }
}

class Pipe extends Charactor {
    Pipe(int x, int y, Image img) { super(x, y, img); }
    public void setSpeed(int s) { this.speed = s; }
    @Override public void update() { setX(getX() - speed); }
}

class Item extends Charactor {
    private ItemType type;
    Item(int x, int y, Image img, ItemType type) { super(x, y, img); this.type = type; }
    public void setSpeed(int s) { this.speed = s; }
    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }
    @Override public void update() { setX(getX() - speed); }
}

class Effect {
    private int x, y, timer, maxTimer;
    private Image img;
    Effect(int x, int y, int duration, Image img) {
        this.x = x; this.y = y; this.timer = duration;
        this.maxTimer = duration; this.img = img;
    }
    public boolean update() { timer--; return timer > 0; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTimer() { return timer; }
    public int getMaxTimer() { return maxTimer; }
    public Image getImg() { return img; }
}
