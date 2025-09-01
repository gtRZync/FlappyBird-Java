package flappybird.core;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Random;

import flappybird.input.Key;
import flappybird.utils.Utils;
import flappybird.entities.*;
import flappybird.input.InputManager;

public class GamePanel extends JPanel implements ActionListener{
    // === Pipes & Spawning ===
    private final ArrayList<Pipe> pipes;
    private boolean shouldSpawn = false;
    private long lastPipeTimer;
    private final Random rand = new Random();

    // === Timing & FPS ===
    private float deltaTime = 0.f;
    private long lastTime = System.nanoTime();
    private float fpsTimer = 0;
    private int fpsCounter = 0;
    private static float FPS = 0.f;
    private float floorX;
    private float floorX2;

    // === Player & Input ===
    private final Bird bird;
    private final InputManager inputManager;
    private boolean mouseJumped = false;
    private boolean keyJumped = false;

    // === Game State ===
    private GameState gameState = GameState.MENU;
    private int score = 0;
    private boolean debug = false;
    private boolean spawningStarted = false;



    GamePanel(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.bird = new Bird( this);
        pipes = new ArrayList<>();
        floorX2 = width;
        setFocusable(true);
        inputManager = new InputManager(this);
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
            AudioManger.POINT.play();
        }
    }

    private void applyParallax() {
        if(gameState.equals(GameState.GAME_OVER) || gameState.equals(GameState.MENU)) return;
        int FG_X_SPEED = -3;
        floorX += FG_X_SPEED;
        floorX2 += FG_X_SPEED;
        if(floorX2 <= 0) {
            floorX2 = getWidth();
            floorX = 0;
        }
    }

    private void updateVerticalMotion()
    {
        if(keyJumped || mouseJumped) {
            AudioManger.SWOOSH.play();
            bird.velocityY = Bird.JUMP_VELOCITY;
            bird.startHeadUpTimer(true);
        }else {
            bird.velocityY += GameConstants.GRAVITY;
        }
        bird.velocityY = Math.min(bird.velocityY, Bird.TERMINAL_VELOCITY);
        bird.move(0.f, bird.velocityY);
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
        deltaTime = (currentTime - lastTime) / 1e9f;
        lastTime = currentTime;

        fpsCounter++;
        fpsTimer += deltaTime;

        if (fpsTimer >= 1.0f) {
            FPS = fpsCounter / fpsTimer;
            fpsCounter -= (int)FPS;
            fpsTimer -= 1.f;
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
        float spawnTime = (currentTime - lastPipeTimer) / 1e9f;
        final float START_PIPE_SPAWNING = 3.f;

        if(spawnTime >= START_PIPE_SPAWNING) {
            spawningStarted = true;
        }
    }

    private void resetGame() {
        bird.reset();
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
                bird.drawBounds(g2);
            for (Pipe p : pipes) {
                p.spawn(g2);
                if (debug) {
                    p.drawBounds(g2);
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

    private void processInput() {
        if(inputManager.keyBoard.isPressed(Key.ESCAPE) && Utils.notPlaying(gameState)) {
            gameState = GameState.MENU;
            resetGame();
        }
        if(inputManager.keyBoard.isPressed(Key.F3)) {
            debug = !debug;
        }


        if(inputManager.mouse.LEFT_BUTTON.pressed && !mouseJumped)
        {
            mouseJumped = true;
            startGame();
        }
        if(inputManager.mouse.LEFT_BUTTON.released) {
            mouseJumped = false;
        }


        if(inputManager.keyBoard.isPressed(Key.SPACE) && !keyJumped) {
            keyJumped = true;
            startGame();
        }
        if(inputManager.keyBoard.isReleased(Key.SPACE)) {
            keyJumped = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        processInput();
        calculateFPS();
        applyParallax();
        bird.updateAnimation(gameState, deltaTime);

        if(gameState == GameState.PLAYING)
        {
            startPipeSpawning();
            updateVerticalMotion();
            if(bird.isGrounded)
            {
                bird.setPosition(bird.getPosition().x, (float)(getHeight() - (bird.getSize().y + GameConstants.FLOOR_HEIGHT)));
                bird.velocityY = Math.max(0.f, bird.velocityY);
            }
            else if(bird.isBelowCeiling)
            {
                bird.setPosition(bird.getPosition().x, GameConstants.MAX_HEIGHT);
                bird.velocityY = Math.max(0.f, bird.velocityY);
            }
            for (Pipe p : pipes) {
                p.move(GameConstants.X_SPEED, 0.f);
                if(bird.isCollidedWith(p.getBounds())) {
                    AudioManger.HIT.play();
                    gameState = GameState.GAME_OVER;
                    AudioManger.DIE.play();
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
        inputManager.resetInputStatesAfter();
    }
}
