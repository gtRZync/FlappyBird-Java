package flappybird.core;

import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.Random;

import flappybird.graphics.sprite.SpriteManager;
import flappybird.graphics.texture.Texture;
import flappybird.input.Key;
import flappybird.math.Vector2;
import flappybird.ui.component.ComponentManager;
import flappybird.ui.component.UIButton;
import flappybird.ui.imagetext.ImageTextRenderer;
import flappybird.ui.transitions.TransitionManager;
import flappybird.ui.transitions.fade.DipToBlack;
import flappybird.ui.transitions.fade.DipToWhite;
import flappybird.ui.transitions.fade.FadeTransition;
import flappybird.ui.transitions.fade.ImageFade;
import flappybird.utils.Utils;
import flappybird.entities.*;
import flappybird.input.InputManager;

public class GamePanel extends JPanel{
    // === Pipes & Spawning ===
    private final ArrayList<Pipe> pipes;
    private boolean shouldSpawn = false;
    private final Random rand = new Random();

    // === Timing & FPS ===
    private final Timer game;
    private float deltaTime = 0.f;
    private long lastTime;
    private float fpsTimer = 0;
    private int fpsCounter = 0;
    private static float FPS = 0.f;
    private float floorX;
    private float floorX2;
    private float bobTimer = 0.f;

    // === Player & Input ===
    private final Bird bird;
    private final InputManager inputManager;
    private boolean mouseJumped = false;
    private boolean keyJumped = false;

    // === UI ===
    private final ComponentManager uiManager;
    private final ImageTextRenderer textRenderer;
    private final UIButton startBtn;
    private final TransitionManager transitionManager;
    private final FadeTransition dipToBlack;
    private final FadeTransition dipToWhite;
    private FadeTransition fadeMessage = null;

    // === Game State ===
    private GameState gameState = GameState.MENU;
    private int score = 0;
    private boolean debug = false;
    private boolean spawningStarted = false;
    private boolean deadPlayed = false;


    GamePanel(int width, int height) {
        this.setLayout(null);
        this.setPreferredSize(new Dimension(width, height));
        this.bird = new Bird( this);
        pipes = new ArrayList<>();
        floorX2 = width;
        setFocusable(true);
        inputManager = new InputManager(this);
        uiManager = new ComponentManager();
        transitionManager = new TransitionManager();

        dipToBlack = new DipToBlack(1.f);
        dipToWhite = new DipToWhite(.1f);
        transitionManager.push(dipToWhite);

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
        int w =(int)(SpriteManager.getTexture("start").getImage().getWidth() * 4.1f);
        int h =(int)(SpriteManager.getTexture("start").getImage().getHeight() * 4.1f);
        startBtn = new UIButton(150, height - 200, w, h,
        SpriteManager.getTexture("start"), SpriteManager.getTexture("startpressed") );
        this.add(startBtn.getComponent());
        uiManager.addElement(startBtn);
        game = new Timer(GameConstants.DELAY, e -> gameLoop());
        game.start();
        lastTime = System.nanoTime();
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
        final Texture TITLE = SpriteManager.getTexture("flappybird");
        if(TITLE.getImage() != null) {
            int titleBaseWidth = TITLE.getImage().getWidth();
            int titleBaseHeight = TITLE.getImage().getHeight();
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

            g2.drawImage(SpriteManager.getTexture("flappybird").getImage(), titlePosX, titlePosY, titleWidth, titleHeight, this);
            g2.drawImage(bird.getTexture().getImage(), birdPosX, birdPosY, birdX, birdY, null);
        }
    }

    private void drawPreGame(Graphics2D g2) {
        Texture MESSAGE = SpriteManager.getTexture("message");
        final float scale = 2.f;
        int width = (int) (MESSAGE.getImage().getWidth() * scale);
        int height = (int) (MESSAGE.getImage().getHeight() * scale);
        int x = (getWidth() - width ) / 2;
        int y = (getHeight() - height) / 2;
        if(fadeMessage == null) {
            fadeMessage = new ImageFade(MESSAGE, x, y, width, height, 3.f, .3f);
            transitionManager.push(fadeMessage);
            transitionManager.push(dipToBlack);//? that way the black screen is in front of the Image when fading
        }
        if(gameState.equals(GameState.START)) {
            ((ImageFade)fadeMessage).fadeIn();
        } else if(gameState.equals(GameState.PLAYING)){
            ((ImageFade)fadeMessage).fadeOut();
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
        g2.drawImage(SpriteManager.getTexture("background-day").getImage(),0, 0, getWidth(), getHeight(), this);
    }
    private void drawFloor(Graphics2D g2) {
        final Texture FLOOR = SpriteManager.getTexture("floor");
        if(FLOOR.getImage() != null) {
            g2.drawImage(FLOOR.getImage(), (int)floorX, getHeight() + GameConstants.FLOOR_SCALE_OFFSET - GameConstants.VIRTUAL_FLOOR_HEIGHT, getWidth(), GameConstants.VIRTUAL_FLOOR_HEIGHT, this);
            g2.drawImage(FLOOR.getImage(), (int)floorX2, getHeight() + GameConstants.FLOOR_SCALE_OFFSET - GameConstants.VIRTUAL_FLOOR_HEIGHT, getWidth(), GameConstants.VIRTUAL_FLOOR_HEIGHT, this);
        }
    }

    private void setScore()
    {
        if (!spawningStarted) return;
        int scoringOffset = 50;//!px
        if(!pipes.getFirst().isScored() && (pipes.getFirst().getCx() + scoringOffset) < bird.getPosition().x) {
            score += 1;
            pipes.getFirst().setScored(true);
            SoundManager.play("scored");
        }
    }

    private void applyParallax() {
        if(gameState == GameState.GAME_OVER) return;
        floorX += GameConstants.FG_X_SPEED;
        floorX2 += GameConstants.FG_X_SPEED;
        if(floorX2 <= 0) {
            floorX2 = getWidth();
            floorX = 0;
        }
    }

    private void updatePlayerVerticalMotion()
    {
        if(gameState.equals(GameState.MENU) || gameState.equals(GameState.START)) return;

        if(keyJumped || mouseJumped) {
            bird.velocityY = Bird.JUMP_VELOCITY;
            bird.startHeadUpTimer(true);
        }else {
            bird.velocityY += GameConstants.GRAVITY;
        }
        bird.velocityY = Math.min(bird.velocityY, (bird.getAngle() > Bird.MAX_DOWNWARD_ANGLE - 2) ? Bird.TERMINAL_DOWNWARD_VELOCITY : Bird.TERMINAL_VELOCITY );
        bird.move(0.f, bird.velocityY);
    }

    private void setShouldSpawn()
    {
        if (!spawningStarted) return;
        int DISTANCE_X_BETWEEN_PIPES = 276;//*px
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
        int height = (int)(Pipe.getTextureHeight() * Pipe.scale);
        int randPipeHeightChooser = rand.nextInt(2); //* 0 - Upper Pipe, 1 - Bottom Pipe
        int[] heights = computeHeights(randPipeHeightChooser, getHeight());

        for (int i = 0; i < 2; i++) {
            int h = height - heights[i];
            heights[i] += h;
            int x = getWidth();
            int y = (i % 2 == 0) ? -h : (getHeight() + h - GameConstants.FLOOR_HEIGHT);//! Add '- GameConstants.FLOOR_HEIGHT'
            pipes.add(new Pipe(x , y, heights[i], this));
        }
    }

    private void calculateDeltaTime()
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

    private void handleStartOrRestart()
    {
        if(gameState.equals(GameState.PLAYING)) return;

        if(gameState.equals(GameState.START)) {
            this.gameState = GameState.PLAYING;
        }else if(gameState.equals(GameState.GAME_OVER)) {
            dipToBlack.start();
            SoundManager.play("transition");
            resetGame();
            this.gameState = GameState.START;
        }
    }

    private void startGame() {
        dipToBlack.start();
        SoundManager.play("transition");
        this.gameState = GameState.START;
    }

    private void startPipeSpawning() {
        if(!spawningStarted){
            spawningStarted = gameState.equals(GameState.PLAYING);
        }
    }

    private void resetGame() {
        bird.reset();
        pipes.clear();
        this.score = 0;
        spawningStarted = false;
        deadPlayed = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); // or VALUE_INTERPOLATION_BICUBIC
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawBackground(g2);
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
            bird.renderTexture(g2);
            textRenderer.renderScore(g2, score);
        }
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        g2.drawString(String.format("FPS : %d", (int)FPS), 0, 15);

        drawFloor(g2);
        transitionManager.update(g2, new Vector2<>(getWidth(), getHeight()), deltaTime);
    }

    private void processInput() {
        if(startBtn.isUp() && startBtn.isHovered()) {
            startGame();
            startBtn.setActive(false);
        }

        if(inputManager.keyBoard.isPressed(Key.ESCAPE) && Utils.notPlaying(gameState)) {
            gameState = GameState.MENU;
            resetGame();
        }
        if(inputManager.keyBoard.isPressed(Key.F3)) {
            debug = !debug;
        }


        if(inputManager.mouse.LEFT_BUTTON.down && !mouseJumped)
        {
            handleStartOrRestart();
            mouseJumped = true;
            if(gameState == GameState.PLAYING)
                SoundManager.play("jumped");
        }
        if(inputManager.mouse.LEFT_BUTTON.up) {
            mouseJumped = false;
        }


        if(inputManager.keyBoard.isPressed(Key.SPACE) && !keyJumped) {
            handleStartOrRestart();
            keyJumped = true;
            if(gameState == GameState.PLAYING)
                SoundManager.play("jumped");
        }
        if(inputManager.keyBoard.isReleased(Key.SPACE)) {
            keyJumped = false;
        }
    }

    private void resetJump() {
        keyJumped = false;
        mouseJumped = false;
    }

    private void resetComponentsStates() {
        inputManager.resetInputStatesAfter();
        uiManager.updateStates();
        resetJump();
        this.repaint();
    }

    private void updatePlayer() {
        bird.updateAnimation(gameState, deltaTime);
        updatePlayerVerticalMotion();
        if(gameState.equals(GameState.PLAYING) || gameState.equals(GameState.GAME_OVER)){
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
        }
    }

    private void updateGame() {
        updatePlayer();
        applyParallax();
        if(gameState == GameState.MENU) startBtn.setActive(true);
        if(gameState == GameState.PLAYING) {
            startPipeSpawning();
            for (Pipe p : pipes) {
                p.move(GameConstants.FG_X_SPEED, 0.f);
                if(bird.isCollidedWith(p.getBounds())) {
                    //TODO: add delay, only play died after collided has ended
                    dipToWhite.start();
                    SoundManager.play("collided");
                    gameState = GameState.GAME_OVER;
                }
            }
            if(bird.isGrounded) {
                //TODO: add delay, only play died after collided has ended
                dipToWhite.start();
                SoundManager.play("collided");
                gameState = GameState.GAME_OVER;
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
        if(gameState.equals(GameState.GAME_OVER)) {
            if(SoundManager.isDone("collided") && !deadPlayed) {
                SoundManager.play("died");
                deadPlayed = true;
            }
        }
    }

    public void stopGameLoop() {
        game.stop();
    }

    private void gameLoop()
    {
        playMenuTheme();
        processInput();
        calculateDeltaTime();
        updateGame();
        resetComponentsStates();
    }
}
