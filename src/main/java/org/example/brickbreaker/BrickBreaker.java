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
    Scene introScene; // intro page of the game
    Stage stage;  // the window of the game
    Button menu = new Button("Menu"); // menu button that retrun to intro scene from the pause page
    Scene playingScene;// The scene that contain all the game logic
    WinningScene winningScene = new WinningScene(); // a scene that shows after winning
    LoosingScene loosingScene = new LoosingScene(); // a scene that shows after loosing

    // Background element
    BackGround outerRoot = new BackGround();

    // Ball objects
    Ball ball = new Ball(); // the bouncing ball
    Ball advancedBall; // a ball that when you take it makes advanced option

    //A rectangle with orange color to show with low opacity when the ball is fired
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

    // Container for settings and sound buttons in intro scene
    VBox vboxPause = new VBox();

    // Flags for game settings
    boolean settingsOn;
    boolean levelsOn;
    boolean playFlag = true;
    boolean soundFlag = true;
    boolean musicFlag = true;

    // Player lives
    int lives = 3;
    // player lives as a Hearts
    ArrayList<Rectangle> numOfLives = new ArrayList<>(3);

    // Player score
    int score = 0;
    // a variable that track the number of crashed bricks to make an advanced ball after 5 crashes
    int numCrashed = 0;
    // a timer for the fire ball
    int  timer = 0;

    // Current level
//    int level;

    // Initial paddle size
    int paddleStartSize = 150;

    // Flag to indicate if ball is attached to paddle and if attached you must press the mouse to continue bouncing
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
   /* ArrayList<Brick> bricks = new ArrayList<>();*/
    // initialize brickCreator
    BricksCreator bricksCreator = new BricksCreator(outerRoot,1);

    // Timeline to handle game animation
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            movePaddle();
            //for detection of pause menu button
            playingScene.setOnKeyPressed(event -> {
                // When you press space you can get the pause menu and turn back to game
                if(event.getCode().equals(KeyCode.SPACE) )
                {
                    if(playFlag){
                        timeline.pause();
                        playFlag = !playFlag;
                        pausePage();
                    }
                    else {
                        outerRoot.getChildren().remove(vboxPause);
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
                // moving the advanced ball if it is exist
                if(isAdvancedBallCreated)
                {
                    advancedBall.setLayoutY(advancedBall.getLayoutY() + advancedBall.getDeltaY());
                    checkCollisionPaddle(paddle,advancedBall);
                    checkCollisionBottomZone1(advancedBall);
                }
                // checking the collision of the ball with bricks
                if (!bricksCreator.getBricks().isEmpty()) {
                    bricksCreator.getBricks().removeIf(brick -> checkCollisionBrick(brick));
                }
                // if bricks is empty so the player won and this condition should handle winning
                else {
                    score += 50000 * lives;
                    lblScore.setText("SCORE: " + score);
                    timeline.stop();
                    playSound(soundsOfGame.getWinningSound());
                    // change the root of playing to the winning root
                    playingScene.setRoot(winningScene);
                    // drawing stars depending on the score and lives and clear the previos page
                    winningScene.drawStars(score);
                    bricksCreator.getBricks().clear();
                    numOfLives.clear();
                    vboxPause.getChildren().clear();
                    playFlag = true;
                    outerRoot.getChildren().clear();
                    // initializing a new game
                    initialize();
                    score = 0;
                    winningScene.playAgain(playingScene,outerRoot,soundsOfGame.getButtonsSound(),soundFlag,paddle,ball);
                    winningScene.menuButton.setOnAction(event ->{
                        winningScene.menuButtonHandle();
                        playSound(soundsOfGame.getButtonsSound());
                        timeline.stop();
                        if(levelsOn)
                        {
                            levelsOn = false;
                        }
                        if(settingsOn)
                            settingsOn = false;
                        intro();
                        bricksCreator.getBricks().clear();
                        numOfLives.clear();
                        vboxPause.getChildren().clear();
                        playFlag = true;
                        stage.setScene(introScene);
                        playingScene.setRoot(new Pane());
                        outerRoot = new BackGround();
                    });
                    winningScene.getNextLevelButton().setOnAction(e ->{
                        playingScene.setRoot(outerRoot);
                        bricksCreator.getBricks().clear();
                        numOfLives.clear();
                        vboxPause.getChildren().clear();
                        playFlag = true;
                        playSound(soundsOfGame.getButtonsSound());
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
                        if(bricksCreator.getLevel() < 3)
                            bricksCreator.setLevel(bricksCreator.getLevel() + 1);
                        initialize();
                    });
                }

                checkCollisionScene(outerRoot);
                checkCollisionBottomZone(ball);
            }
            else{
                // making the ball attached to the paddle if the bricks is still existing
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
            playSound(soundsOfGame.getButtonsSound());

            // Set our root to the playingScene
            playingScene = new Scene(outerRoot);

            // Set the current level to "One"
            bricksCreator.setLevel(1);

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
            playSound(soundsOfGame.getButtonsSound());
            playingScene = new Scene(outerRoot);
            bricksCreator.setLevel(2);
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
            playSound(soundsOfGame.getButtonsSound());
            playingScene = new Scene(outerRoot);
            bricksCreator.setLevel(3);
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
            playSound(soundsOfGame.getButtonsSound());

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
            bricksCreator.getBricks().clear();
            numOfLives.clear();
            vboxPause.getChildren().clear();

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
        // Initialize game variables
        lives = 3;  // Number of starting lives
        score = 0;   // Starting score
        numCrashed = 0;  // Number of bricks crashed to make an advanced ball every 5 crashes
        isAdvancedBallCreated = false;  // Flag to track advanced ball creation

        // Set paddle position and size
        paddle.setWidth(paddleStartSize);
        paddle.setLayoutX(outerRoot.getPrefWidth() / 2 - paddle.getWidth() / 2); // position at center
        paddle.setLayoutY(outerRoot.getPrefHeight() - 50);  // Position paddle at the bottom

        // Set ball position and size
        ball.setLayoutY(paddle.getLayoutY() - 15);  // Position ball above paddle
        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        ball.setRadius(12);

        // Set animation loop properties
        timeline.setCycleCount(Animation.INDEFINITE);  // Animation runs indefinitely

        // Initialize score label
        lblScore.setText("SCORE: " + score);
        lblScore.setLayoutX(outerRoot.getPrefWidth() - 150);
        lblScore.setLayoutY(10);
        lblScore.setFont(Font.font("Elephant", 12));
        lblScore.setTextFill(Color.BLACK);
        lblScore.setBackground(Background.fill(Color.WHITE));

        // Set bottom zone (representing game over zone)
        bottomZone.setLayoutY(outerRoot.getPrefHeight() - 20);
        bottomZone.setLayoutX(0);
        bottomZone.setHeight(20);
        bottomZone.setWidth(outerRoot.getPrefWidth());
        bottomZone.setFill(Color.TRANSPARENT);  // Make it invisible initially

        // Set start button appearance
        ImageView startButtonImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\play.png"));
        startButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC, 15)); // Set custom font style
        startButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink"); // Set rounded corners, padding, and text color
        startButton.setGraphic(startButtonImg);  // Set the play button image
        startButtonImg.setFitWidth(20);
        startButtonImg.setFitHeight(20);
        startButton.setPrefWidth(100);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(outerRoot.getPrefWidth() / 2 - startButton.getPrefWidth() + 30);  // Center the button horizontally with some offset
        startButton.setLayoutY((outerRoot.getPrefHeight() / 2) - startButton.getPrefHeight());  // Center the button vertically
        startButton.setVisible(true);

        // Add game elements to the scene
        outerRoot.getChildren().addAll(paddle, ball, lblScore, bottomZone, startButton);

        // Draw initial lives on the screen
        drawLives(lives);

        // Set fire rectangle (used for visual effects) to be invisible initially
        fireRectangle.setOpacity(0);

        // Randomly determine initial ball direction (left or right)
        int random = (((int) (Math.random() * 100)) % 4);
        if (random >= 2) {
            ball.setDeltaX(-Math.random() * 4);
            ball.setDeltaY(-4);
        } else {
            ball.setDeltaX(Math.random() * 4);
            ball.setDeltaY(-4);
        }
    }
    private void startGame() {
         // Creates the brick layout for the level
        bricksCreator.setRoot(outerRoot);
        bricksCreator.createBricks();
        timeline.play();  // Starts the animation loop
    }
    public void drawLives(int lives) {
        for (int i = 0; i < lives; i++) {
            numOfLives.add(new Rectangle(40 * i + 10, 10, 30, 30));  // Create a rectangle for each life
            numOfLives.get(i).toBack();  // Send the life rectangle behind other elements
            numOfLives.get(i).setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\heart.png")));  // Set the life icon (heart image)
            outerRoot.getChildren().add(numOfLives.get(i));  // Add the life Image to the scene
        }
    }
    public void movePaddle() {
        Bounds bounds = outerRoot.localToScreen(outerRoot.getBoundsInLocal());  // Get scene bounds
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();  // Get mouse X position
        double paddleWidth = paddle.getWidth();  // Get paddle width

        if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));  // Move paddle based on mouse X while keeping it within scene bounds
        } else if (xPos < sceneXPos + (paddleWidth / 2)) {
            paddle.setLayoutX(0);  // Restrict paddle movement to scene bounds (left edge)
        } else if (xPos > (sceneXPos + outerRoot.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(outerRoot.getWidth() - paddleWidth);  // Restrict paddle movement to scene bounds (right edge)
        }
    }
    // Checks for ball collisions with walls and bounces accordingly
    private void checkBorders(boolean rightBorder, boolean leftBorder, boolean bottomBorder, boolean topBorder) {
        if (rightBorder && !(topBorder || bottomBorder) && ball.getDeltaX() < 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);  // Bounce the ball if it hits the right wall (except corners)
        } else if (leftBorder && !(topBorder || bottomBorder) && ball.getDeltaX() > 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);  // Bounce the ball if it hits the left wall (except corners)
        }
        if (rightBorder && (topBorder || bottomBorder) && ball.getDeltaX() < 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
            ball.setDeltaY(ball.getDeltaY() * -1);  // Bounce the ball if it hits a corner (right + top/bottom)
        } else if (leftBorder && (topBorder || bottomBorder) && ball.getDeltaX() > 0) {
            ball.setDeltaX(ball.getDeltaX() * -1);
            ball.setDeltaY(ball.getDeltaY() * -1);  // Bounce the ball if it hits a corner (left + top/bottom)
        }
        if (bottomBorder && ball.getDeltaY() < 0) {
            ball.setDeltaY(ball.getDeltaY() * -1);  // Bounce the ball if it hits the bottom wall
        } else if (topBorder && ball.getDeltaY() > 0) {
            ball.setDeltaY(ball.getDeltaY() * -1);  // Bounce the ball if it hits the top wall
        }
    }
    // Checks for ball collision with the paddle and adjusts its bounce angle based on the contact point
    public void checkCollisionPaddle(Paddle paddle) {
        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            // Calculate relative X-axis intersection point (offset from paddle center)
            double relativeIntersectX = (ball.getLayoutX() + ball.getRadius()) - (paddle.getLayoutX() + paddle.getWidth() / 2);

            // Normalize relative intersection based on paddle width (-1 to 1)
            double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);

            // Calculate bounce angle based on normalized intersection (maximum angle PI/3)
            double bounceAngle = normalizedIntersectX * Math.PI / 3;

            // Update ball's deltaX and deltaY based on bounce angle and constant speed factor
            ball.setDeltaX(Math.sin(bounceAngle) * 4);
            ball.setDeltaY(-Math.cos(bounceAngle) * 4);
        }
    }
    // Checks for ball collision with a brick and handles various actions
    public boolean checkCollisionBrick(Brick brick) {

        // Check for collision between ball and brick bounding boxes
        if (ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {

            // Special case for fire ball (instant destroy)
            if (ball.isFire()) {
                score += 500;
                lblScore.setText("SCORE: " + score);
                outerRoot.getChildren().remove(brick);  // Remove brick immediately
                return true;  // Collision detected
            }

            // Check collision with each brick wall
            boolean rightBorder = ball.getLayoutX() >= ((brick.getX() + brick.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (brick.getX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((brick.getY() + brick.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (brick.getY() + ball.getRadius());

            // Delegate wall collision checks to separate function
            checkBorders(rightBorder, leftBorder, bottomBorder, topBorder);

            // Reduce brick's health (number of crashes)
            brick.setNumOfCrashes(brick.getNumOfCrashes() - 1);

            // Play breaking sound if not already playing and sound is enabled
            playSound(soundsOfGame.getBreakingSound());

            // Check if brick is destroyed
            if (brick.getNumOfCrashes() <= 0) {
                numCrashed++;  // Increment number of crashed bricks

                // Create advanced ball on specific intervals (if not already created)
                if (numCrashed % 5 == 0 && !isAdvancedBallCreated) {
                    createAdvancedBall(brick);
                }

                score += 500;
                lblScore.setText("SCORE: " + score);
                outerRoot.getChildren().remove(brick);  // Remove destroyed brick
                return true;  // Collision detected and brick destroyed
            } else {
                return false;  // Collision detected but brick not destroyed
            }
        }

        return false;  // No collision
    }
    // Checks for ball collision with the scene boundaries (walls)
    public void checkCollisionScene(Node node) {
        // Get the scene node's bounding box in local coordinates
        Bounds bounds = node.getBoundsInLocal();

        // Calculate adjusted border positions considering ball radius and a small buffer
        boolean rightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius() - 3);
        boolean leftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius() + 3);
        boolean bottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius() - 3);
        boolean topBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius() + 3);

        // Handle ball bounces on horizontal walls
        if (rightBorder || leftBorder) {
            ball.setDeltaX(ball.getDeltaX() * -1);  // Invert ball's horizontal movement (bounce)
        }

        // Handle ball bounces on vertical walls (top and bottom)
        if (bottomBorder || topBorder) {
            ball.setDeltaY(ball.getDeltaY() * -1);  // Invert ball's vertical movement (bounce)
        }
    }

    // Handles ball collision with the bottom zone (usually representing game over area)
    public void checkCollisionBottomZone(Ball ball) {
        if (ball.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {

            // Play heart sound if lives are greater than 1 and sound is enabled
            if (lives > 1 && soundFlag) {
                if (soundsOfGame.getHeartSound().getStatus() == MediaPlayer.Status.PLAYING) {
                    soundsOfGame.getHeartSound().stop();  // Stop any ongoing heart sound
                    soundsOfGame.getHeartSound().seek(soundsOfGame.getHeartSound().getStartTime());  // Reset sound to beginning
                }
                soundsOfGame.getHeartSound().play();  // Play heart sound to indicate life loss
            }

            // Decrement lives and update lives display
            lives--;
            for (Rectangle live : numOfLives) {
                outerRoot.getChildren().remove(live);  // Remove live icon from scene
            }
            numOfLives.clear();  // Clear the list of life icons
            drawLives(lives);  // Update the number of lives displayed

            // Handle game over or life loss scenario
            if (lives > 0) { // Life loss but not game over

                ballIsTouched = true;

                // Reset ball position to center of paddle
                ball.setLayoutX(paddle.getLayoutX());
                ball.setLayoutY(paddle.getLayoutY());

                // Remove advanced ball if it exists
                outerRoot.getChildren().remove(advancedBall);

                // Update paddle position based on mouse position (similar to movePaddle)
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

                // Randomly set ball's initial horizontal movement direction after reset
                if (((int) (Math.random() * 100)) % 4 >= 2) {
                    ball.setDeltaX(-Math.random() * 4);
                    ball.setDeltaY(-4);
                } else {
                    ball.setDeltaX(Math.random() * 4);
                    ball.setDeltaY(-4);
                }

            } else { // Game Over

                // Stop animation timeline
                timeline.stop();

                // Switch scene to losing scene
                playingScene.setRoot(loosingScene);

                // Add action listener to Menu button in losing scene
                loosingScene.getMenuButton().setOnAction(e -> {
                    playSound(soundsOfGame.getButtonsSound());

                    // Reset game settings
                    levelsOn = false;
                    settingsOn = false;
                    intro();
                    bricksCreator.getBricks().clear();
                    numOfLives.clear();
                    vboxPause.getChildren().clear();
                    playFlag = true;
                    stage.setScene(introScene);
                    playingScene.setRoot(new Pane());  // Clear playing scene's root node
                    outerRoot = new BackGround();  // Create new background

                    // Back to intro scene
                });

                // Add action listener to Play Again button in losing scene
                loosingScene.getPlayAgainButton().setOnAction(e -> {
                    // Switch back to playing scene
                    playingScene.setRoot(outerRoot);

                    // Clear game elements
                    bricksCreator.getBricks().clear();
                    numOfLives.clear();

                    // Reset game for "Play Again" scenario
                    vboxPause.getChildren().clear();  // Clear any elements from the pause menu (vboxPause)
                    playFlag = true;  // Reset play flag (likely used to control game loop)

                    playSound(soundsOfGame.getButtonsSound());

                    // Reset paddle position to center of the scene
                    paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);

                    // Reset ball position next to centered paddle
                    ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);

                    // Randomly set ball's initial horizontal movement direction
                    if (((int) (Math.random() * 100)) % 4 >= 2) {
                        ball.setDeltaX(-Math.random() * 4);
                        ball.setDeltaY(-4);
                    } else {
                        ball.setDeltaX(Math.random() * 4);
                        ball.setDeltaY(-4);
                    }

                    // Set initial ball position slightly below the paddle
                    ball.setLayoutY(paddle.getLayoutY() - 10);

                    // Clear all children (game elements) from the main scene group (outerRoot)
                    outerRoot.getChildren().clear();

                    // Call the initialize() method to reset the game (likely creates bricks etc.)
                    initialize();
                });
                // Play "Game Over" Sound
                playSound(soundsOfGame.getGameOverSound()); // Play the "Game Over" sound if sound effects are enabled

                // Remove Advanced Ball
                if(isAdvancedBallCreated) {
                    outerRoot.getChildren().remove(advancedBall);  // Remove the "advanced ball" (if it exists) from the scene
                    isAdvancedBallCreated = false;
                }
                // Remove Bricks
                bricksCreator.getBricks().forEach(brick -> outerRoot.getChildren().remove(brick));  // Remove each brick from the scene
                bricksCreator.getBricks().clear();  // Clear the list of bricks

                // Show Start Button
                startButton.setVisible(true);  // Make the "Start" button visible again

                // Reset Score to zero
                score = 0;

                // Reset the number of lives to 3
                lives = 3;

                // Center Paddle
                paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);  // Position the paddle back in the center
            }

        }
    }
    private  void intro()
    {
        // Create buttons for the intro scene
        Button levels = new Button("Levels");
        Button setting = new Button("Setting");
        ImageView exitImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\logout.png"));
        exitImg.fitHeightProperty().bind(setting.prefHeightProperty().multiply(1));
        exitImg.fitWidthProperty().bind(exitImg.fitHeightProperty());
        Button exit = new Button("Exit", exitImg);  // Create exit button with image

        // Create image views for settings and sound buttons
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
        // Update sound and music icons based on soundFlag and musicFlag

        // Set sound icon based on sound flag
        if (!soundFlag) {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
        } else {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        }

        // Set music icon based on music flag (similar logic as sound)
        if (!musicFlag) {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
        } else {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        }

        // Action handler for sound button - toggles sound and icon
        sound.setOnAction(e -> {
            // Handle cases when the game is paused
            playSound(soundsOfGame.getButtonsSound());

            // Toggle sound flag and update icon based on the new flag state
            soundFlag = !soundFlag;

            // Update sound icon directly in action handler
            soundImg.setImage(soundFlag ? new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png") : new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
        });

        // Set initial sound button icon
        sound.setGraphic(soundImg);

        music.setOnAction(e -> {
            // Handle cases when the game is paused (same logic as sound button)
            playSound(soundsOfGame.getButtonsSound());

            // Toggle music flag and update icon based on the new flag state
            if (musicFlag) {
                // Music is currently on, so pause it
                soundsOfGame.getMusicSound().pause();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
                music.setGraphic(musicImg);
            } else {
                // Music is currently off, so play it
                soundsOfGame.getMusicSound().play();
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
                music.setGraphic(musicImg);
            }
            musicFlag = !musicFlag;
        });
        // set initialize music button icon
        music.setGraphic(musicImg);

        levels.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        setting.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        sound.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        music.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        exit.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level1.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level2.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        level3.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");

        // Initially hide level and sound buttons
        level1.setVisible(false);
        level2.setVisible(false);
        level3.setVisible(false);
        sound.setVisible(false);
        music.setVisible(false);

        // Action handler for levels button - toggles level visibility
        levels.setOnAction(event -> {
            // Handle cases when the game is paused (same logic as sound and music buttons)
            playSound(soundsOfGame.getButtonsSound());

            // Toggle levels visibility flag and update level buttons
            levelsOn = !levelsOn;
            level1.setVisible(levelsOn);
            level2.setVisible(levelsOn);
            level3.setVisible(levelsOn);
        });

        // Action handler for settings button - toggles sound/music visibility
        setting.setOnAction(event -> {
            playSound(soundsOfGame.getButtonsSound());

            // Toggle settings visibility flag and update sound/music buttons visibility
            settingsOn = !settingsOn;
            sound.setVisible(settingsOn);
            music.setVisible(settingsOn);
        });

        // Action handler for exit button - closes the stage
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


        // Create the root container for the scene
        StackPane root = new StackPane();

        // Create a VBox for centered layout of instruction label and grid
        VBox vBox = new VBox(50);
        vBox.setAlignment(Pos.CENTER);

        // Create a GridPane for level selection, settings, and exit buttons
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        // Configure instruction label
        Label lblInstruction = new Label("Mouse To Move and Press Space To pause");
        lblInstruction.setFont(Font.font("DejaVu Math TeX Gyre"));
        lblInstruction.setTextFill(Color.WHITE);
        lblInstruction.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC, 25));
        lblInstruction.setUnderline(true);

        // Add instruction label and grid to the VBox
        vBox.getChildren().addAll(lblInstruction, grid);

        // Set graphic and font for settings button
        setting.setGraphic(settingsImg); // Assuming settingsImg is an ImageView containing the settings icon
        setting.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC, 20));

        // Create background image
        Image introImage = new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\introBG.jpg");
        ImageView introImageView = new ImageView(introImage);

        // Bind background image to fit the scene size
        introImageView.fitHeightProperty().bind(root.heightProperty());
        introImageView.fitWidthProperty().bind(root.widthProperty());

        // Create VBoxes for level selection
        VBox vlevels = new VBox();
        vlevels.getChildren().addAll(level1, level2, level3); // level1, level2, level3 are buttons

        // Bind vlevels height to grid height
        vlevels.prefHeightProperty().bind(grid.prefHeightProperty());

        // Set spacing between buttons in vlevels
        vlevels.setSpacing(10);

        VBox vsetting = new VBox();
        vsetting.getChildren().addAll(sound, music); //sound and music are buttons

        // Set spacing between buttons in vsetting
        vsetting.setSpacing(10);

        // Set preferred sizes for buttons based on scene dimensions
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

        // Set spacing between elements in the grid
        grid.setVgap(10);
        grid.setHgap(10);

        // Add buttons and VBoxes to the grid with specific positioning
        grid.add(levels, 0, 0); // Levels button at row 0, column 0
        grid.add(setting, 1, 0); // Settings button at row 0, column 1
        grid.add(exit, 2, 0);   // Exit button at row 0, column 2
        grid.add(vlevels, 0, 1);  // Level selection VBox at row 1, column 0 (likely hidden initially)
        grid.add(vsetting, 1, 1); // Sound/music buttons VBox at row 1, column 1 (likely hidden

        vlevels.setAlignment(Pos.CENTER);
        vsetting.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(introImageView,vBox);

        introScene = new Scene(root, 1080, 720);

    }

    // Creates an "advanced ball" based on a broken brick
    private void createAdvancedBall(Brick brick) {
        // Calculate initial position for the advanced ball
        advancedBall = new Ball(brick.getX() + brick.getWidth() / 2,brick.getY() + brick.getHeight() + brick.getWidth() * 0.2,ball.getRadius() +3);

        // Determine advanced ball type based on lives remaining
        if (lives < 5) {
            // If lives are low, randomly choose type (0 or 1)
            advancedBall.setAdvancedBallType((int) (Math.random() * 100 % 2));
        } else {
            // If lives are high, set type to 1 (Fire ball only)
            advancedBall.setAdvancedBallType(1);
        }

        // Set appearance and initial movement
        advancedBall.setStroke(Color.BLACK);
        advancedBall.setDeltaX(0); // No horizontal movement
        advancedBall.setDeltaY(1.5); // Set initial downward movement

        // Track creation state
        if (!isAdvancedBallCreated) {
            isAdvancedBallCreated = true; // Flag to indicate an advanced ball is now present
        }

        // Add the advanced ball to the scene
        outerRoot.getChildren().add(advancedBall);
    }
    // Method to add a life
    private void addLives() {
        // Remove existing life icons from the scene
        for (Rectangle live : numOfLives) {
            outerRoot.getChildren().remove(live);
        }

        // Clear the list of life icons
        numOfLives.clear();

        // Increase the lives counter
        lives++;

        // Redraw the lives based on the updated lives count
        drawLives(lives);
    }
    // Function to activate a fire effect for the ball
    private void fireBall() {
        // Enable the fire effect for the ball
        ball.setFire(true);

        // Set fire rectangle opacity to 0
        fireRectangle.setOpacity(0);

        // Add the fire rectangle to the scene
        outerRoot.getChildren().add(fireRectangle);

        // initialize fire Sound
        playSound(soundsOfGame.getFireSound());
    }
    // Function to check collision between advanced ball and paddle
    private void checkCollisionPaddle(Paddle paddle, Ball advancedBall) {

        // Check for intersection between advanced ball and paddle bounding boxes
        if (advancedBall.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            playSound(soundsOfGame.getAdvancedBallSound());

            // Remove advanced ball from the scene
            outerRoot.getChildren().remove(advancedBall);

            // Reset advanced ball creation flag
            isAdvancedBallCreated = false;

            // Handle collision based on lives remaining
            if (lives >= 5) {
                // High lives: score points, update label, activate fire
                score += 1000;
                lblScore.setText("SCORE: " + score);
                fireBall();
            } else if (lives < 5) {
                // Low lives: handle collision based on advanced ball type
                switch (advancedBall.getAdvancedBallType()) {
                    case 0:
                        // Type 0: add a life
                        addLives();
                        break;
                    case 1:
                        // Type 1: activate fire
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
    // Function to create and display the pause menu
    private void pausePage() {

        // Create buttons for pause menu
        Button cont = new Button("Continue");
        Button sound = new Button("Sound");
        Button music = new Button("Music");

        // Set button sizes
        cont.setPrefHeight(40);
        cont.setPrefWidth(200);
        menu.setPrefHeight(40);
        menu.setPrefWidth(150);
        sound.setPrefHeight(40);
        sound.setPrefWidth(150);
        music.setPrefHeight(40);
        music.setPrefWidth(150);

        // Create a dark translucent rectangle as background for the pause menu
        Rectangle rectangle = new Rectangle(0, 0, outerRoot.getWidth(), outerRoot.getHeight());
        rectangle.setFill(Color.color(0, 0, 0, 0.5)); // 50% transparent black

        // Add the background rectangle to the scene
        outerRoot.getChildren().add(rectangle);

        // Define action for "Continue" button
        cont.setOnAction(e -> {
            // Play button sound if enabled
            playSound(soundsOfGame.getButtonsSound());

            // Resume the game timeline
            timeline.play();

            // Invert play flag for tracking pause/resume state
            playFlag = !playFlag;

            // Remove background rectangle and pause menu elements from the scene
            outerRoot.getChildren().remove(rectangle);
            outerRoot.getChildren().remove(vboxPause);
            vboxPause.getChildren().clear(); // Clear children from vboxPause for later reuse
        });

        // Create ImageViews for sound and music button icons based on sound/music state
        ImageView soundImg = new ImageView();
        soundImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        soundImg.fitWidthProperty().bind(soundImg.fitHeightProperty());

        ImageView musicImg = new ImageView();
        musicImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        musicImg.fitWidthProperty().bind(soundImg.fitHeightProperty());

        if (!soundFlag) {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
        } else {
            soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        }

        if (!musicFlag) {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
        } else {
            musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        }
        sound.setOnAction(e -> {
            // Play button sound if enabled
            playSound(soundsOfGame.getButtonsSound());

            // Toggle sound on/off and update button icon
            if (soundFlag) {
                // Sound is currently on, mute sound and update icon
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag; // Invert sound flag
            } else {
                // Sound is currently off, unmute sound and update icon
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag; // Invert sound flag
            }
        });

        // Set the initial graphic for the sound button based on soundFlag
        sound.setGraphic(soundImg);
        // same logic as sound button
        music.setOnAction(e ->{
            playSound(soundsOfGame.getButtonsSound());
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

        vboxPause.getChildren().addAll(cont,sound,music,menu);
        vboxPause.setSpacing(10);
        vboxPause.setAlignment(Pos.CENTER);
        vboxPause.setLayoutX(outerRoot.getWidth() / 3 + 75);
        vboxPause.setLayoutY(outerRoot.getHeight() / 3);
        outerRoot.getChildren().add(vboxPause);

    }
    public void playSound(MediaPlayer sound){
        if(sound.getStatus() == MediaPlayer.Status.PLAYING)
        {
            sound.stop();
            sound.seek(sound.getStartTime());
        }
        if(soundFlag)
            sound.play();
    }
    public static void main(String[] args) {
        launch();
    }
}

