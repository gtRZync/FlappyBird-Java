package flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import utils.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // === Pipes & Spawning ===
    private final ArrayList<Pipe> pipes;
    private boolean shouldSpawn = false;
    private long lastPipeTimer;
    private final Random rand = new Random();

    // === Timing & FPS ===
    private long lastTime = System.nanoTime();
    private float fpsTimer = 0;
    private int fpsCounter = 0;
    private static float FPS = 0.f;
    private float floorX;
    private float floorX2;

    // === Player & Input ===
    private final Bird bird;
    private boolean jumped = false;

    // === Game State ===
    private GameState gameState = GameState.MENU;
    private int score = 0;
    private boolean debug = false;
    private boolean spawningStarted = false;



    GamePanel(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.bird = new Bird(100.f, 250.f, 50, 50, GameConstants.BIRD, this);
        pipes = new ArrayList<>();
        floorX2 = width;
        setFocusable(true);
        addKeyListener(this);
        Timer game = new Timer(GameConstants.DELAY, this);
        game.start();
    }

    private void drawBackground(Graphics2D g2) {
        if(GameConstants.BACKGROUND != null) {
            g2.drawImage(GameConstants.BACKGROUND,0, 0, getWidth(), getHeight(), this);
        }

        if(gameState.equals(GameState.MENU) && GameConstants.MESSAGE != null) {
            int width = (getWidth() - getWidth() / 2) / 2;
            int height = (getHeight() - getHeight() / 2) / 2;
            g2.drawImage(GameConstants.MESSAGE,width, height, getWidth() / 2, getHeight() / 2 , this);
        }
    }
    private void drawFloor(Graphics2D g2) {
        if(GameConstants.FLOOR != null) {
            g2.drawImage(GameConstants.FLOOR, (int)floorX, getHeight() - GameConstants.FLOOR_HEIGHT, getWidth(), GameConstants.FLOOR_HEIGHT, this);
            g2.drawImage(GameConstants.FLOOR, (int)floorX2, getHeight() - GameConstants.FLOOR_HEIGHT, getWidth(), GameConstants.FLOOR_HEIGHT, this);
        }
    }

    private void setScore()
    {
        if (!spawningStarted) return;

        if(!pipes.getFirst().isScored() && pipes.getFirst().getCx() < bird.getPosition().x) {
            score += 1;
            pipes.getFirst().setScored(true);
            Audio.POINT.play();
        }
    }

    private void applyParallax() {
        int FG_X_SPEED = -3;
        floorX += FG_X_SPEED;
        floorX2 += FG_X_SPEED;
        if(floorX2 <= 0) {
            floorX2 = getWidth();
            floorX = 0;
        }
    }

    private void applyGravity()
    {
        if(jumped) {
            bird.velocityY = GameConstants.JUMP_VELOCITY;
        }else {
            bird.velocityY += GameConstants.GRAVITY;
        }
        bird.velocityY = Math.min(bird.velocityY, GameConstants.TERMINAL_VELOCITY);
        bird.move(new vector2<>(0.f, bird.velocityY));
    }

    private void setShouldSpawn()
    {
        if (!spawningStarted) return;
        int DISTANCE_X_BETWEEN_PIPES = 210;//*px
        if(pipes.isEmpty() || pipes.getLast().getCx() < getWidth() - DISTANCE_X_BETWEEN_PIPES )
            shouldSpawn = true;
    }

    private int[] computeHeights(int chooser, int screenHeight)
    {
        //TODO: Add check for smaller window height to avoid negatives height if height1 is too big
        //! Maybe not let's just setResizable to false lol
        int height1 = this.rand.nextInt(Pipe.MIN_HEIGHT, Pipe.MAX_HEIGHT);
        int height2 = screenHeight - (height1 + GameConstants.SPACE_BETWEEN_PIPES + GameConstants.FLOOR_HEIGHT);
        switch(chooser) {
            case Pipe.UPPER -> { return new int[] {height1, height2}; }
            case Pipe.BOTTOM -> { return new int[] {height2, height1}; }
            default -> throw new IllegalStateException("Unexpected value: " + chooser);
        }
    }

    private void addPipe()
    {
        int height = GameConstants.PIPE.getHeight();
        int randPipeHeightChooser = rand.nextInt(2); //* 0 - Upper Pipe, 1 - Bottom Pipe
        int[] heights = computeHeights(randPipeHeightChooser, getHeight());

        for (int i = 0; i < 2; i++) {
            int h = height - heights[i];
            heights[i] += h;
            int x = getWidth() + Pipe.WIDTH;
            int y = (i % 2 == 0) ? -h : (getHeight() + h) ;//! Add '- FLOOR_HEIGHT'
            pipes.add(new Pipe(x , y, heights[i], GameConstants.PIPE, this));
        }
    }

    private void calculateFPS()
    {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastTime) / 1_000_000_000f;
        lastTime = currentTime;

        fpsCounter++;
        fpsTimer += deltaTime;

        if (fpsTimer >= 1.0f) {
            FPS = fpsCounter / fpsTimer;
            fpsCounter = 0;
            fpsTimer = 0;
        }
    }

    private void startGame()
    {
        if(gameState.equals(GameState.MENU)) {
            this.gameState = GameState.START;
        } else if(gameState.equals(GameState.START)) {
            this.gameState = GameState.PLAYING;
            lastPipeTimer = System.nanoTime();
        }else if(gameState.equals(GameState.GAME_OVER)) {
            resetGame();
            this.gameState = GameState.START;
        }
    }

    private void startPipeSpawning() {
        long currentTime = System.nanoTime();
        float spawnTime = (currentTime - lastPipeTimer) / 1_000_000_000f;
        final float START_PIPE_SPAWNING = 3.f;

        if(spawnTime >= START_PIPE_SPAWNING) {
            spawningStarted = true;
        }
    }

    private void resetGame() {
        this.bird.setPosition(new vector2<>(bird.getPosition().x, 250.f));
        pipes.clear();
        this.score = 0;
        spawningStarted = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        drawBackground(g2);
        if(!gameState.equals(GameState.MENU))
        {
            if (debug)
                bird.bounds.draw(g2);
            for (Pipe p : pipes) {
                p.spawn(g2);
                if (debug) {
                    p.bounds.draw(g2);
                }
            }
            bird.drawSprite(g2);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Wobble Board", Font.PLAIN, 30));
            g2.drawString(Integer.toString(this.score), (getWidth() / 2) - 3, 100);
            g2.drawString(Integer.toString(this.score), (getWidth() / 2) + 3, 100);
            g2.drawString(Integer.toString(this.score), (getWidth() / 2), 100 - 3);
            g2.drawString(Integer.toString(this.score), (getWidth() / 2), 100 + 3);
            g2.setColor(Color.WHITE);
            g2.drawString(Integer.toString(this.score), getWidth() / 2, 100);
        }
        drawFloor(g2);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        calculateFPS();
        if(!gameState.equals(GameState.GAME_OVER) && !gameState.equals(GameState.MENU)) {
            applyParallax();
        }

        if(gameState == GameState.PLAYING)
        {
            startPipeSpawning();
            applyGravity();
            if(bird.getPosition().y > getHeight() - (bird.getSize().y + GameConstants.FLOOR_HEIGHT))
            {
                bird.setPosition(new vector2<>(bird.getPosition().x, (float)(getHeight() - (bird.getSize().y + GameConstants.FLOOR_HEIGHT))));                bird.velocityY = Math.max(0.f, bird.velocityY);
                bird.velocityY = Math.max(0.f, bird.velocityY);
            }
            else if(bird.getPosition().y <=  GameConstants.MAX_HEIGHT)
            {
                bird.setPosition(new vector2<>(bird.getPosition().x, GameConstants.MAX_HEIGHT));
                bird.velocityY = Math.max(0.f, bird.velocityY);
            }
            for (Pipe p : pipes) {
                p.move(GameConstants.X_SPEED, 0.f);
                if(bird.isCollidedWith(p.bounds)) {
                    Audio.HIT.play();
                    gameState = GameState.GAME_OVER;
                    Audio.DIE.play();
                }
            }
            pipes.removeIf(p -> p.getCx() < - Pipe.WIDTH);
            setShouldSpawn();
            if(shouldSpawn)
            {
                addPipe();
                shouldSpawn = false;
            }
            setScore();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE && !jumped)
        {
            jumped = true;
            startGame();
            if(gameState.equals(GameState.PLAYING))
                Audio.SWOOSH.play();
        }
        if(e.getKeyCode() == KeyEvent.VK_F3) {
            debug = !debug;
        }
        if(!gameState.equals(GameState.PLAYING) && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gameState = GameState.MENU;
            resetGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            jumped = false;
        }
    }
}
