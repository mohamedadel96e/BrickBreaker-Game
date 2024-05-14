package org.example.brickbreaker;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;


public class BrickBreaker extends Application  {

    // Scene objects for the different game states
    Scene introScene;
    Stage stage;
    Button menu = new Button("Menu");
    Scene playingScene;
    WinningScene winningScene = new WinningScene();
    LoosingScene loosingScene = new LoosingScene();

    // Background element
    BackGround outerRoot = new BackGround();

    // Ball objects
    Ball ball = new Ball();
    Ball advancedBall;
    Rectangle fireRectangle = new Rectangle(1080,720,Color.ORANGE);
    boolean isAdvancedBallCreated = false;

    // Paddle object
    Paddle paddle = new Paddle();

    // Bottom zone to detect ball collision
    Rectangle bottomZone = new Rectangle();

    // Start button for the intro scene
    Button startButton = new Button("Start");

    // Level buttons for level selection
    Button level1 = new Button("level 1");
    Button level2 = new Button("level 2");
    Button level3 = new Button("level 3");

    // Container for settings and sound buttons
    VBox vbox2 = new VBox();

    // Flags for game settings
    boolean settingsOn;
    boolean levelsOn;
    boolean playFlag = true;
    boolean soundFlag = true;
    boolean musicFlag = true;

    // Player lives
    int lives = 3;
    ArrayList<Rectangle> numOfLives = new ArrayList<>(3);

    // Player score
    int score = 0;
    int numCrashed = 0;
    long timer = 0;

    // Current level
    int level;

    // Initial paddle size
    int paddleStartSize = 150;

    // Flag to indicate if ball is attached to paddle
    boolean ballIsTouched;

    // Robot class for mouse input
    Robot robot = new Robot();

    // Label to display score
    Label lblScore = new Label("SCORE: " + 0);

    // Sounds class for game sound effects and music
    Sounds soundsOfGame = new Sounds();

    // Button for sound settings
    Button sound = new Button("sound");

    // Button for music settings
    Button music = new Button("Music");

    // List of bricks
    ArrayList<Brick> bricks = new ArrayList<>();

    // Timeline to handle game animation
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            movePaddle();
            //for detection of pause menu button
            playingScene.setOnKeyPressed(event -> {
                if(event.getCode().equals(KeyCode.SPACE) )
                {
                    if(playFlag){
                        timeline.pause();
                        playFlag = !playFlag;
                        pausePage();
                    }
                    else {
                        outerRoot.getChildren().remove(vbox2);
                        playFlag = !playFlag;
                        timeline.play();

                    }
                }
            });
            // for make a timer for 3 seconds for the fireball
            if(ball.isFire())
            {
                fireRectangle.setOpacity((double) (timer % 300) / 1500.0);
                timer++;
                if(timer % 300 == 0)
                {
                    ball.setFire(false);
                    outerRoot.getChildren().remove(fireRectangle);
                    timer = 0;
                }
            }
            /* check for the ball if it touched with the paddle or not
             * and if is not the continue playing
             * and if is touched the game won't play until mouse clicked*/
            if(!ballIsTouched)
            {
                // checking the collision of the ball with the paddle and handle the detection
                checkCollisionPaddle(paddle);
                // moving The ball
                ball.setLayoutX(ball.getLayoutX() + ball.getDeltaX());
                ball.setLayoutY(ball.getLayoutY() + ball.getDeltaY());
                if(isAdvancedBallCreated)
                {
                    advancedBall.setLayoutY(advancedBall.getLayoutY() + advancedBall.getDeltaY());
                    checkCollisionPaddle(paddle,advancedBall);
                    checkCollisionBottomZone1(advancedBall);
                }
                // checking the collision of the ball with bricks
                if (!bricks.isEmpty()) {
                    bricks.removeIf(brick -> checkCollisionBrick(brick));
                }
                // if bricks is empty so the player won and i should handle winning
                else {
                    score += 50000 * lives;
                    lblScore.setText("SCORE: " + score);
                    timeline.stop();
                    if(soundsOfGame.getWinningSound().getStatus() == MediaPlayer.Status.PLAYING)
                    {
                        soundsOfGame.getWinningSound().stop();
                        soundsOfGame.getWinningSound().seek(soundsOfGame.getWinningSound().getStartTime());
                    }
                    if(soundFlag)
                        soundsOfGame.getWinningSound().play();
                    playingScene.setRoot(winningScene);
                    System.out.println(score);
                    winningScene.drawStars(score);
                    bricks.clear();
                    numOfLives.clear();
                    vbox2.getChildren().clear();
                    playFlag = true;
                    outerRoot.getChildren().clear();
                    initialize();
                    score = 0;
                    winningScene.playAgain(playingScene,outerRoot,soundsOfGame.getButtonsSound(),soundFlag,paddle,ball);
                    winningScene.menuButton.setOnAction(event ->{
                        winningScene.menuButtonHandle();
                        if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
                        {
                            soundsOfGame.getButtonsSound().stop();
                            soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
                        }
                        if(soundFlag)
                            soundsOfGame.getButtonsSound().play();
                        timeline.stop();
                        if(levelsOn)
                        {
                            levelsOn = false;
                        }
                        if(settingsOn)
                            settingsOn = false;
                        intro();
                        bricks.clear();
                        numOfLives.clear();
                        vbox2.getChildren().clear();
                        playFlag = true;
                        stage.setScene(introScene);
                        playingScene.setRoot(new Pane());
                        outerRoot = new BackGround();
                    });
                    winningScene.getNextLevelButton().setOnAction(e ->{
                        playingScene.setRoot(outerRoot);
                        bricks.clear();
                        numOfLives.clear();
                        vbox2.getChildren().clear();
                        playFlag = true;
                        if (soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING) {
                            soundsOfGame.getButtonsSound().stop();
                            soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
                        }
                        if (soundFlag)
                            soundsOfGame.getButtonsSound().play();
                        paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);
                        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);

                        if (((int) (Math.random() * 100)) % 4 >= 2) {
                            ball.setDeltaX(-Math.random() * 4);
                            ball.setDeltaY(-4);
                        } else {
                            ball.setDeltaX(Math.random() * 4);
                            ball.setDeltaY(-4);
                        }
                        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
                        ball.setLayoutY(paddle.getLayoutY() - 10);
                        outerRoot.getChildren().clear();
                        if(level < 3)
                            level = level + 1;
                        initialize();
                    });
                }

                checkCollisionScene(outerRoot);
                checkCollisionBottomZone(ball);
            }
            else{
                ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth()  / 2);
                ball.setLayoutY(paddle.getLayoutY() - 10);
                outerRoot.setOnMouseClicked(e ->
                {
                    ballIsTouched = false;
                });
            }

        }
    }));

    @Override
    public void start(Stage stage) throws Exception {

        // Prevent the window from being resized
        stage.setResizable(false);

        // Set the application icon
        stage.getIcons().add(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\gameIcon.png"));

        // Store the stage object for later use
        this.stage = stage;

        // Play the background music on a loop
        soundsOfGame.getMusicSound().play();
        soundsOfGame.getMusicSound().setAutoPlay(true);

        // Level 1 button action handler
        level1.setOnAction(e -> {
            // Play button click sound if enabled
            if (soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING) {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if (soundFlag)
                soundsOfGame.getButtonsSound().play();

            // Set our root to the playingScene
            playingScene = new Scene(outerRoot);

            // Set the current level to "One"
            level = 1;

            // Initialize the game elements
            initialize();

            // Start button action handler (nested inside level 1 button handler)
            startButton.setOnAction(e1 -> {
                // Hide the start button
                startButton.setVisible(false);

                // Start the game loop
                startGame();
            });

            // Create a fade transition to switch from intro scene to game scene
            FadeTransition ft = new FadeTransition(Duration.millis(1000), introScene.getRoot());
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setOnFinished(event -> stage.setScene(playingScene));
            ft.play();
        });
        // Similar button action handlers for level 2 and level 3 with same functionality
        level2.setOnAction( e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            playingScene = new Scene(outerRoot);
            level = 2;
            initialize();
            startButton.setOnAction( e1 -> {
                startButton.setVisible(false);
                startGame();
            });
            FadeTransition ft = new FadeTransition(Duration.millis(1000),introScene.getRoot());
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setOnFinished(event -> stage.setScene(playingScene));
            ft.play();
        });
        level3.setOnAction( e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            playingScene = new Scene(outerRoot);
            level = 3;
            initialize();
            startButton.setOnAction( e1 -> {
                startButton.setVisible(false);
                startGame();
            });
            FadeTransition ft = new FadeTransition(Duration.millis(1000),introScene.getRoot());
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setOnFinished(event -> stage.setScene(playingScene));
            ft.play();
        });
        // Menu button action handler
        menu.setOnAction(e -> {
            // Play button click sound if enabled
            if (soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING) {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if (soundFlag)
                soundsOfGame.getButtonsSound().play();

            // Stop the game loop
            timeline.stop();

            // Reset flags for settings and level selection menus
            if (levelsOn) {
                levelsOn = false;
            }
            if (settingsOn)
                settingsOn = false;

            // Show the intro scene
            intro();

            // Clear game elements from previous game
            bricks.clear();
            numOfLives.clear();
            vbox2.getChildren().clear();

            // Reset game state flags
            playFlag = true;

            // Switch back to the intro scene
            stage.setScene(introScene);

            // Clear the playing scene's root node
            playingScene.setRoot(new Pane());

            // Reset the background element
            outerRoot = new BackGround();
        });

        // Call intro method to setup the intro scene
        intro();

        // Set the initial scene to the intro scene
        stage.setScene(introScene);

        // Set the window title
        stage.setTitle("BrickBreaker Game");

        // Show the application window
        stage.show();
    }

    public void initialize()
    {
        lives = 3;
        score = 0;
        numCrashed = 0;
        isAdvancedBallCreated = false;
        paddle.setWidth(paddleStartSize);
        paddle.setLayoutX(outerRoot.getPrefWidth() / 2 - paddle.getWidth() /2);
        paddle.setLayoutY(outerRoot.getPrefHeight() - 50);
        ball.setLayoutY(paddle.getLayoutY() - 15);
        ball.setLayoutX(paddle.getLayoutX() +paddle.getWidth() / 2);
        ball.setRadius(12);
        timeline.setCycleCount(Animation.INDEFINITE);
        lblScore.setText("SCORE: " + score);
        lblScore.setLayoutX(outerRoot.getPrefWidth() - 150);
        lblScore.setLayoutY(10);
        lblScore.setFont(Font.font("Elephant",12));
        lblScore.setTextFill(Color.BLACK);
        lblScore.setBackground(Background.fill(Color.WHITE));
        bottomZone.setLayoutY(outerRoot.getPrefHeight() -20);
        bottomZone.setLayoutX(0);
        bottomZone.setHeight(20);
        bottomZone.setWidth(outerRoot.getPrefWidth());
        bottomZone.setFill(Color.TRANSPARENT);
        startButton.setBackground(Background.EMPTY);
        ImageView startButtonImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\play.png"));
        startButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,15));
        startButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        startButton.setGraphic(startButtonImg);
        startButtonImg.setFitWidth(20);
        startButtonImg.setFitHeight(20);
        startButton.setPrefWidth(100);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(outerRoot.getPrefWidth() / 2 - startButton.getPrefWidth() + 30);
        startButton.setLayoutY((outerRoot.getPrefHeight() / 2) - startButton.getPrefHeight());
        startButton.setVisible(true);
        outerRoot.getChildren().addAll(paddle,ball,lblScore,bottomZone,startButton);
        drawLives(lives);
        fireRectangle.setOpacity(0);
        int random = (((int) (Math.random() * 100)) %  4);
        if(random >= 2)
        {
            ball.setDeltaX(-Math.random() * 4);
            ball.setDeltaY(-4);
        }
        else {
            ball.setDeltaX(Math.random() * 4);
            ball.setDeltaY(-4);
        }

    }
    private void startGame()
    {
        createBricks();
        timeline.play();

    }
    public void drawLives(int lives)
    {
        for(int i = 0; i < lives; i++)
        {
            numOfLives.add(new Rectangle(40 * i + 10,10,30,30));
            numOfLives.get(i).toBack();
            numOfLives.get(i).setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\heart.png")));

            outerRoot.getChildren().add(numOfLives.get(i));

        }

    }
    public void movePaddle() {
        Bounds bounds = outerRoot.localToScreen(outerRoot.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();
        double paddleWidth = paddle.getWidth();

        if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
        } else if (xPos < sceneXPos + (paddleWidth / 2)) {
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(outerRoot.getWidth() - paddleWidth);
        }
    }
    private void checkBorders(boolean rightBorder, boolean leftBorder, boolean bottomBorder, boolean topBorder) {
        if (rightBorder  && !(topBorder || bottomBorder) && ball.getDeltaX() < 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
        } else if (leftBorder  && !(topBorder || bottomBorder) && ball.getDeltaX() > 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
        }
        if (rightBorder  && (topBorder || bottomBorder) && ball.getDeltaX() < 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
            ball.setDeltaY(ball.getDeltaY() * -1);
        } else if (leftBorder  && (topBorder || bottomBorder) && ball.getDeltaX() > 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
            ball.setDeltaY(ball.getDeltaY() * -1);
        }
        if (bottomBorder && ball.getDeltaY() < 0) {
            ball.setDeltaY(ball.getDeltaY() * -1);
        } else if (topBorder && ball.getDeltaY() > 0) {
            ball.setDeltaY(ball.getDeltaY() * -1);
        }
    }
    public void checkCollisionPaddle(Paddle paddle) {

        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {

            double relativeIntersectX = (ball.getLayoutX() + ball.getRadius()) - (paddle.getLayoutX() + paddle.getWidth() / 2);
            double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);
            double bounceAngle = normalizedIntersectX * Math.PI / 3; // Maximum bounce angle
            ball.setDeltaX( Math.sin(bounceAngle) * 4);
            ball.setDeltaY(-Math.cos(bounceAngle) * 4);


        }

    }
    public boolean checkCollisionBrick(Brick brick) {

        if (ball.getBoundsInParent().intersects(brick.getBoundsInParent()) ) {
            if(ball.isFire()){
                score += 500;
                lblScore.setText("SCORE: " + score);
                outerRoot.getChildren().remove(brick);
                return true;
            }
            boolean rightBorder = ball.getLayoutX() >= ((brick.getX() + brick.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (brick.getX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((brick.getY() + brick.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (brick.getY() + ball.getRadius());
            checkBorders(rightBorder, leftBorder, bottomBorder, topBorder);
            brick.setNumOfCrashes(brick.getNumOfCrashes() - 1);
            if(soundsOfGame.getBreakingSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getBreakingSound().seek(soundsOfGame.getBreakingSound().getStartTime());
            }
            if(soundFlag){
                soundsOfGame.getBreakingSound().play();
            }

            if(brick.getNumOfCrashes() <= 0)
            {
                numCrashed++;
                if(numCrashed % 5 == 0)
                {
                    createAdvancedBall(brick);
                }
                score += 500;
                lblScore.setText("SCORE: " + score);
                outerRoot.getChildren().remove(brick);
                return true;
            }
            else
                return false;
        }
        return false;
    }
    public void checkCollisionScene(Node node) {
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius() - 3 );
        boolean leftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius() + 3);
        boolean bottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius() - 3);
        boolean topBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius() + 3);

        if (rightBorder || leftBorder) {
            ball.setDeltaX(ball.getDeltaX() * -1);
        }
        if (bottomBorder || topBorder) {
            ball.setDeltaY(ball.getDeltaY() * -1);
        }
    }
    public void checkCollisionBottomZone(Ball ball) {
        if (ball.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
            if(soundsOfGame.getHeartSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getHeartSound().stop();
                soundsOfGame.getHeartSound().seek(soundsOfGame.getHeartSound().getStartTime());
            }
            if(soundFlag && lives > 1)
                soundsOfGame.getHeartSound().play();
            lives--;
            for(Rectangle live:numOfLives)
                outerRoot.getChildren().remove(live);
            numOfLives.clear();
            drawLives(lives);

            if (lives > 0) {
                ballIsTouched = true;
                ball.setLayoutX(paddle.getLayoutX());
                ball.setLayoutY(paddle.getLayoutY());
                outerRoot.getChildren().remove(advancedBall);
                Bounds bounds = outerRoot.localToScreen(outerRoot.getBoundsInLocal());
                double sceneXPos = bounds.getMinX();
                double xPos = robot.getMouseX();
                double paddleWidth = paddle.getWidth();

                if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
                } else if (xPos < sceneXPos + (paddleWidth / 2)) {
                    paddle.setLayoutX(0);
                } else if (xPos > (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(outerRoot.getWidth() - paddleWidth);
                }

                if ((((int) (Math.random() * 100)) % 4) >= 2) {
                    ball.setDeltaX(-Math.random() * 4);
                    ball.setDeltaY(-4);
                } else {
                    ball.setDeltaX(Math.random() * 4);
                    ball.setDeltaY(-4);
                }
            } else {

                timeline.stop();
                playingScene.setRoot(loosingScene);
                loosingScene.getMenuButton().setOnAction( e ->{
                    if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
                    {
                        soundsOfGame.getButtonsSound().stop();
                        soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
                    }
                    if(soundFlag)
                        soundsOfGame.getButtonsSound().play();
                    if(levelsOn)
                    {
                        levelsOn = false;
                    }
                    if(settingsOn)
                        settingsOn = false;
                    intro();
                    bricks.clear();
                    numOfLives.clear();
                    vbox2.getChildren().clear();
                    playFlag = true;
                    stage.setScene(introScene);
                    playingScene.setRoot(new Pane());
                    outerRoot = new BackGround();
                });

                loosingScene.getPlayAgainButton().setOnAction(e ->{
                    playingScene.setRoot(outerRoot);
                    bricks.clear();
                    numOfLives.clear();
                    vbox2.getChildren().clear();
                    playFlag = true;
                    if (soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING) {
                        soundsOfGame.getButtonsSound().stop();
                        soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
                    }
                    if (soundFlag)
                        soundsOfGame.getButtonsSound().play();
                    paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);
                    ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);

                    if (((int) (Math.random() * 100)) % 4 >= 2) {
                        ball.setDeltaX(-Math.random() * 4);
                        ball.setDeltaY(-4);
                    } else {
                        ball.setDeltaX(Math.random() * 4);
                        ball.setDeltaY(-4);
                    }
                    ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
                    ball.setLayoutY(paddle.getLayoutY() - 10);
                    outerRoot.getChildren().clear();
                    initialize();
                });
                if(soundsOfGame.getGameOverSound().getStatus() == MediaPlayer.Status.PLAYING)
                {
                    soundsOfGame.getGameOverSound().stop();
                    soundsOfGame.getGameOverSound().seek(soundsOfGame.getGameOverSound().getStartTime());
                }
                if(soundFlag)
                    soundsOfGame.getGameOverSound().play();
                outerRoot.getChildren().remove(advancedBall);
                bricks.forEach(brick -> outerRoot.getChildren().remove(brick));
                bricks.clear();
                startButton.setVisible(true);
                score = 0;
                lives = 3;
                paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);

                System.out.println("Game over!");
            }

        }
    }
    private int random1To3()
    {
        return (((int)(Math.random() * 100)) % 3) + 1;
    }
    public void createBricks()
    {
        Brick brick;
        double width = outerRoot.getPrefWidth();
        double height = outerRoot.getPrefHeight();
        double space = 15;
        int numOfBricksInRow = 10;
        int numOfBrickInColumn = 5;
        double brickWidth = (outerRoot.getPrefWidth() - numOfBricksInRow * space - 10) / numOfBricksInRow;
        double brickHeight = brickWidth * 0.5;
        int columnCount = 0;
        switch (level)
        {
            case 1:
                for(double i = lblScore.getLayoutY() + lblScore.getHeight() * 3; i < height * 0.6;i = i + brickHeight + space)
                {
                    if(columnCount >= numOfBrickInColumn)
                    {
                        break;
                    }
                    for (double j = 10; j < width - 10; j = j + brickWidth + space) {

                        brick = switch (columnCount) {
                            case 4 ->
                                    new Brick(j, i, brickWidth, brickHeight, 1);
                            case 3 ->
                                    new Brick(j, i, brickWidth, brickHeight, 2);
                            default ->
                                    new Brick(j, i, brickWidth, brickHeight, random1To3());
                        };

                        outerRoot.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    columnCount++;
                }
                break;
            case 2:
                int bricksTriangle = 5;
                brickWidth = (width / 2 - bricksTriangle * space)  / bricksTriangle;
                int rowCount = 0;
                for(double i = lblScore.getLayoutY() + lblScore.getHeight() * 3; (i < height * 0.6) && columnCount < bricksTriangle;i = i + brickWidth / 2 + space)
                {
                    for(double j = 10; (j < width / 2) && rowCount <= columnCount ; j = j + brickWidth + space)
                    {
                        rowCount++;
                        brick =switch (columnCount)
                        {
                            case 4 ->  new Brick(j, i, brickWidth, brickWidth / 2,1);
                            default -> new Brick(j, i, brickWidth, brickWidth / 2, random1To3());
                        };
                        outerRoot.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    rowCount = 0;
                    columnCount++;
                }
                columnCount = 0;
                for(double i = lblScore.getLayoutY() + lblScore.getHeight() * 3; (i < height * 0.6) && columnCount < bricksTriangle;i = i + brickWidth / 2 + space)
                {
                    columnCount++;
                    for(double j = outerRoot.getPrefWidth() - 10 - brickWidth; (j > width / 2) && rowCount < columnCount ; j = j - brickWidth - space)
                    {
                        rowCount++;
                        brick =switch (columnCount)
                        {
                            case 5 ->  new Brick(j, i, brickWidth, brickWidth / 2,1);
                            default -> new Brick(j, i, brickWidth, brickWidth / 2, random1To3());
                        };
                        outerRoot.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    rowCount = 0;
                }
                break;
            case 3:
                break;

        }
    }
    private  void intro()
    {
        Button levels = new Button("Levels");
        Button setting = new Button("Setting");
        ImageView exitImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\logout.png"));
        exitImg.fitHeightProperty().bind(setting.prefHeightProperty().multiply(1));
        exitImg.fitWidthProperty().bind(exitImg.fitHeightProperty());
        Button exit = new Button("Exit",exitImg);

        ImageView settingsImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\setting.png"));
        settingsImg.fitHeightProperty().bind(setting.prefHeightProperty().multiply(1));
        settingsImg.fitWidthProperty().bind(settingsImg.fitHeightProperty());
        ImageView levelsImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\select.png"));
        levelsImg.fitHeightProperty().bind(levels.prefHeightProperty().multiply(1));
        levelsImg.fitWidthProperty().bind(levelsImg.fitHeightProperty());

        ImageView soundImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        soundImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        soundImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        ImageView musicImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        musicImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        musicImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        if(!soundFlag)
        {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
        }
        else{
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        }
        if(!musicFlag)
        {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));

        }
        else{
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        }
        sound.setOnAction(e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            if(soundFlag)
            {
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));

                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
            else{
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
        });
        sound.setGraphic(soundImg);

        music.setOnAction(e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            if(musicFlag)
            {
                soundsOfGame.getMusicSound().pause();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
            else{
                soundsOfGame.getMusicSound().play();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
        });
        music.setGraphic(musicImg);

        levels.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        setting.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        sound.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        music.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        exit.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level1.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level2.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level3.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");

        level1.setVisible(false);
        level2.setVisible(false);
        level3.setVisible(false);
        sound.setVisible(false);
        music.setVisible(false);

        levels.setOnAction(event -> {
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            levelsOn = !levelsOn;
            level1.setVisible(levelsOn);
            level2.setVisible(levelsOn);
            level3.setVisible(levelsOn);
        });
        setting.setOnAction(event -> {
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            settingsOn = !settingsOn;
            sound.setVisible(settingsOn);
            music.setVisible(settingsOn);
        });
        exit.setOnAction(event -> {
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.close();
        });

        levels.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        levels.setGraphic(levelsImg);
        sound.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,16));
        music.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,16));
        exit.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        level1.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        level2.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        level3.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));


        StackPane root = new StackPane();
        VBox vBox = new VBox(50);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        Label lblInstruction = new Label("Mouse To Move and Press Space To pause");
        lblInstruction.setFont(Font.font("DejaVu Math TeX Gyre"));
        lblInstruction.setTextFill(Color.WHITE);
        lblInstruction.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        lblInstruction.setUnderline(true);
        vBox.getChildren().addAll(lblInstruction,grid);

        setting.setGraphic(settingsImg);
        setting.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));

        Image m = new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\introBG.jpg");
        ImageView mv = new ImageView(m);
        mv.fitHeightProperty().bind(root.heightProperty());
        mv.fitWidthProperty().bind(root.widthProperty());


        VBox vlevels = new VBox();
        vlevels.getChildren().addAll(level1,level2,level3);

        vlevels.prefHeightProperty().bind(grid.prefHeightProperty());

        vlevels.setSpacing(10);

        VBox vsetting = new VBox();
        vsetting.getChildren().addAll(sound,music);

        vsetting.setSpacing(10);

        levels.prefHeightProperty().bind(root.heightProperty().divide(20));
        levels.prefWidthProperty().bind(root.widthProperty().divide(7));

        level1.prefHeightProperty().bind(root.heightProperty().divide(22));
        level1.prefWidthProperty().bind(root.widthProperty().divide(9));

        level2.prefHeightProperty().bind(root.heightProperty().divide(22));
        level2.prefWidthProperty().bind(root.widthProperty().divide(9));

        level3.prefHeightProperty().bind(root.heightProperty().divide(22));
        level3.prefWidthProperty().bind(root.widthProperty().divide(9));

        setting.prefHeightProperty().bind(root.heightProperty().divide(20));
        setting.prefWidthProperty().bind(root.widthProperty().divide(7));

        sound.prefHeightProperty().bind(root.heightProperty().divide(22));
        sound.prefWidthProperty().bind(root.widthProperty().divide(9));

        music.prefHeightProperty().bind(root.heightProperty().divide(22));
        music.prefWidthProperty().bind(root.widthProperty().divide(9));

        exit.prefHeightProperty().bind(root.heightProperty().divide(20));
        exit.prefWidthProperty().bind(root.widthProperty().divide(7));

        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(levels, 0, 0);
        grid.add(setting, 1, 0);
        grid.add(exit, 2, 0);
        grid.add(vlevels, 0, 1);
        grid.add(vsetting, 1, 1);
        vlevels.setAlignment(Pos.CENTER);
        vsetting.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(mv,vBox);

        introScene = new Scene(root, 1080, 720);

    }

    private void createAdvancedBall(Brick brick)
    {
        advancedBall = new Ball(brick.getX() + brick.getWidth() / 2,brick.getY() + brick.getHeight() + brick.getWidth() * 0.2,ball.getRadius() +3);
        if(lives < 5) {
            advancedBall.setAdvancedBallType((int) (Math.random() * 100 % 2));
        }
        else{
            advancedBall.setAdvancedBallType(1);
        }
        advancedBall.setStroke(Color.BLACK);
        advancedBall.setDeltaX(0);
        advancedBall.setDeltaY(1.5);
        if(!isAdvancedBallCreated)
            isAdvancedBallCreated = true;
        outerRoot.getChildren().add(advancedBall);
    }
    private void addLives()
    {
        for(Rectangle live :numOfLives)
            outerRoot.getChildren().remove(live);
        numOfLives.clear();
        lives++;
        drawLives(lives);
    }
    private void fireBall(){
        ball.setFire(true);
        fireRectangle.setOpacity(0);
        outerRoot.getChildren().add(fireRectangle);
        if(soundsOfGame.getFireSound().getStatus() == MediaPlayer.Status.PLAYING)
        {
            soundsOfGame.getFireSound().stop();
            soundsOfGame.getFireSound().seek(soundsOfGame.getFireSound().getStartTime());
        }
        if(soundFlag)
            soundsOfGame.getFireSound().play();
    }
    private void checkCollisionPaddle(Paddle paddle,Ball advancedBall) {

        if (advancedBall.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            if(soundsOfGame.getAdvancedBallSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getAdvancedBallSound().stop();
                soundsOfGame.getAdvancedBallSound().seek(soundsOfGame.getAdvancedBallSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getAdvancedBallSound().play();
            outerRoot.getChildren().remove(advancedBall);
            isAdvancedBallCreated = false;
            if(lives >= 5){
                score += 1000;
                lblScore.setText("SCORE: " + score);
                fireBall();
            }
            if(lives < 5){
                switch (advancedBall.getAdvancedBallType())
                {
                    case 0:
                        addLives();
                        break;
                    case 1:
                        fireBall();
                        break;
                }
            }
        }

    }

    private void checkCollisionBottomZone1(Ball advancedBall)
    {
        if(advancedBall.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
            outerRoot.getChildren().remove(advancedBall);
        }
    }
    private void pausePage()
    {
        Button cont = new Button("Continue");
        Button sound = new Button("Sound");
        Button music = new Button("Music");

        cont.setPrefHeight(40);
        cont.setPrefWidth(200);
        menu.setPrefHeight(40);
        menu.setPrefWidth(150);
        sound.setPrefHeight(40);
        sound.setPrefWidth(150);
        music.setPrefHeight(40);
        music.setPrefWidth(150);
        Rectangle rectangle = new Rectangle(0,0,outerRoot.getWidth(),outerRoot.getHeight());
        rectangle.setFill(Color.color(0,0,0,0.5));
        outerRoot.getChildren().add(rectangle);
        cont.setOnAction(e -> {
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            timeline.play();
            playFlag = !playFlag;
            outerRoot.getChildren().remove(rectangle);
            outerRoot.getChildren().remove(vbox2);
            vbox2.getChildren().clear();
        });
        ImageView soundImg = new ImageView();
        soundImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        soundImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        ImageView musicImg = new ImageView();
        musicImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        musicImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        if(!soundFlag)
        {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
        }
        else{
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        }
        if(!musicFlag)
        {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));

        }
        else{
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        }
        sound.setOnAction(e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(!soundFlag)
                soundsOfGame.getButtonsSound().play();
            if(soundFlag)
            {
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
            else{
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
        });
        sound.setGraphic(soundImg);

        music.setOnAction(e ->{
            if(soundsOfGame.getButtonsSound().getStatus() == MediaPlayer.Status.PLAYING)
            {
                soundsOfGame.getButtonsSound().stop();
                soundsOfGame.getButtonsSound().seek(soundsOfGame.getButtonsSound().getStartTime());
            }
            if(soundFlag)
                soundsOfGame.getButtonsSound().play();
            if(musicFlag)
            {
                soundsOfGame.getMusicSound().pause();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
            else{
                soundsOfGame.getMusicSound().play();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
        });
        music.setGraphic(musicImg);

        cont.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        cont.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        menu.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        menu.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        sound.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        sound.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        music.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        music.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));

        vbox2.getChildren().addAll(cont,sound,music,menu);
        vbox2.setSpacing(10);
        vbox2.setAlignment(Pos.CENTER);
        vbox2.setLayoutX(outerRoot.getWidth() / 3 + 75);
        vbox2.setLayoutY(outerRoot.getHeight() / 3);
        outerRoot.getChildren().add(vbox2);

    }
    public static void main(String[] args) {
        launch();
    }
}

