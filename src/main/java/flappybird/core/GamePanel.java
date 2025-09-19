package flappybird.core;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Random;

import flappybird.graphics.sprite.SpriteManager;
import flappybird.input.Key;
import flappybird.math.Vector2;
import flappybird.ui.imagetext.ImageTextRenderer;
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
    private float bobTimer = 0.f;

    // === Player & Input ===
    private final Bird bird;
    private final InputManager inputManager;
    private final ImageTextRenderer textRenderer;
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
        //TODO: maybe add a loadAll(HashMap <String, String> sounds)
        //TODO: add a loading menu and load the assets there, with a representative loading bar
        SoundManager.init();
        SoundManager.loadSound("died", GameConstants.AUDIO_DIR+"die.wav");
        SoundManager.loadSound("scored", GameConstants.AUDIO_DIR+"point.wav");
        SoundManager.loadSound("collided", GameConstants.AUDIO_DIR+"hit.wav");
        SoundManager.loadSound("jumped", GameConstants.AUDIO_DIR+"wing.wav");
        SoundManager.loadSound("transition", GameConstants.AUDIO_DIR+"swoosh.wav");
        SoundManager.loadSingleInstance("Menu Theme", GameConstants.AUDIO_DIR+"FlappyBird_Menu.wav");
        try
        {
            Class.forName("flappybird.graphics.sprite.SpriteManager");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[ERROR] - Class SpriteManager was not found : ", e);
        }
        textRenderer = new ImageTextRenderer(new Vector2<>(width, height));
        Timer game = new Timer(GameConstants.DELAY, this);
        game.start();
    }

    private void playMenuTheme() {
        if(gameState != GameState.MENU) {
            SoundManager.stopInstance("Menu Theme");
        }else {
            SoundManager.playInstance("Menu Theme");
        }
    }

    private void drawTitle(Graphics2D g2) {
        final int OFFSET_TITLE_BIRD = 30;
        if(GameConstants.TITLE != null) {
            int titleBaseWidth = GameConstants.TITLE.getWidth();
            int titleBaseHeight = GameConstants.TITLE.getHeight();
            final float scale = 3.5f;

            int titleWidth = Math.round(titleBaseWidth * scale);
            int titleHeight = Math.round(titleBaseHeight * scale);

            Vector2<Integer> birdSize = bird.getSize();
            final float birdScale = 1.15f;
            int birdX = (int) (birdSize.x * birdScale);
            int birdY = (int) (birdSize.y * birdScale);

            int totalWidth = titleWidth + OFFSET_TITLE_BIRD + birdX;

            int titlePosX = (getWidth() - totalWidth) / 2;
            int titlePosY = (getHeight() - titleHeight) / 4;
            int titleCenterY = titlePosY + titleHeight / 2;


            int birdPosX = titlePosX + titleWidth + OFFSET_TITLE_BIRD;
            int birdPosY = titleCenterY - birdY / 2;

            float speed = 7.f;
            float amplitude = 310.f;
            bobTimer += deltaTime;
            int offsetY = Math.round((float) Math.sin(bobTimer * speed) * deltaTime * amplitude);
            titlePosY += offsetY;
            birdPosY += offsetY;

            g2.drawImage(GameConstants.TITLE, titlePosX, titlePosY, titleWidth, titleHeight, this);
            g2.drawImage(bird.getCurrentFrame(), birdPosX, birdPosY, birdX, birdY, this);
        }
    }

    private void drawPreGame(Graphics2D g2) {
        if(GameConstants.MESSAGE != null) {
            final float scale = 2.f;
            int width = (int) (GameConstants.MESSAGE.getWidth() * scale);
            int height = (int) (GameConstants.MESSAGE.getHeight() * scale);
            int x = (getWidth() - width ) / 2;
            int y = (getHeight() - height) / 2;
            g2.drawImage(GameConstants.MESSAGE,x, y, width, height , this);
        }
    }

    private void drawMenus(Graphics2D g2) {
        if(gameState == GameState.MENU) {
            drawTitle(g2);
        } else if (gameState == GameState.START) {
            drawPreGame(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        if(GameConstants.BACKGROUND != null) {
            g2.drawImage(GameConstants.BACKGROUND,0, 0, getWidth(), getHeight(), this);
        }
    }
    private void drawFloor(Graphics2D g2) {
        if(GameConstants.FLOOR != null) {
            g2.drawImage(GameConstants.FLOOR, (int)floorX, getHeight() + GameConstants.FLOOR_SCALE_OFFSET - GameConstants.VIRTUAL_FLOOR_HEIGHT, getWidth(), GameConstants.VIRTUAL_FLOOR_HEIGHT, this);
            g2.drawImage(GameConstants.FLOOR, (int)floorX2, getHeight() + GameConstants.FLOOR_SCALE_OFFSET - GameConstants.VIRTUAL_FLOOR_HEIGHT, getWidth(), GameConstants.VIRTUAL_FLOOR_HEIGHT, this);
        }
    }

    private void setScore()
    {
        if (!spawningStarted) return;
        int scoringOffset = 30;//!px
        if(!pipes.getFirst().isScored() && (pipes.getFirst().getCx() + scoringOffset) < bird.getPosition().x) {
            score += 1;
            pipes.getFirst().setScored(true);
            SoundManager.play("scored");
        }
    }

    private void applyParallax() {
        if(gameState == GameState.GAME_OVER) return;
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
        int height = (int)(GameConstants.PIPE.getHeight() * Pipe.scale);
        int randPipeHeightChooser = rand.nextInt(2); //* 0 - Upper Pipe, 1 - Bottom Pipe
        int[] heights = computeHeights(randPipeHeightChooser, getHeight());

        for (int i = 0; i < 2; i++) {
            int h = height - heights[i];
            heights[i] += h;
            int x = getWidth() + (int) (GameConstants.PIPE.getWidth() * Pipe.scale);
            int y = (i % 2 == 0) ? -h : (getHeight() + h);//! Add '- GameConstants.FLOOR_HEIGHT'
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
            SoundManager.play("transition");
            this.gameState = GameState.START;
        } else if(gameState.equals(GameState.START)) {
            this.gameState = GameState.PLAYING;
            lastPipeTimer = System.nanoTime();
        }else if(gameState.equals(GameState.GAME_OVER)) {
            SoundManager.play("transition");
            resetGame();
            this.gameState = GameState.START;
        }
    }

    private void startPipeSpawning() {
        if(!spawningStarted){
            long currentTime = System.nanoTime();
            float spawnTime = (currentTime - lastPipeTimer) / 1e9f;
            final float START_PIPE_SPAWNING = 1.f;

            if (spawnTime >= START_PIPE_SPAWNING) {
                spawningStarted = true;
            }
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // or VALUE_INTERPOLATION_BICUBIC
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawBackground(g2);
        //TODO: correct the superfast flap bug on the menu screen (due to delta time accumulation when screen is still invisible i think)
        drawMenus(g2);
        if(gameState != GameState.MENU)
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
            textRenderer.renderScore(g2, score);
        }
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        g2.drawString("numbers of pipes : " + pipes.size(), getWidth() - 150, 15);
        g2.drawString(String.format("FPS : %d", (int)FPS), 0, 15);

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


        if(inputManager.mouse.LEFT_BUTTON.down && !mouseJumped)
        {
            mouseJumped = true;
            startGame();
            if(gameState == GameState.PLAYING)
                SoundManager.play("jumped");
        }
        if(inputManager.mouse.LEFT_BUTTON.up) {
            mouseJumped = false;
        }


        if(inputManager.keyBoard.isPressed(Key.SPACE) && !keyJumped) {
            keyJumped = true;
            startGame();
            if(gameState == GameState.PLAYING)
                SoundManager.play("jumped");
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
        playMenuTheme();
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
                    //TODO: add delay, only play died after collided has ended
                    SoundManager.play("collided");
                    gameState = GameState.GAME_OVER;
                    SoundManager.play("died");
                }
            }
            pipes.removeIf(p -> p.getCx() < - p.getWidth());
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
