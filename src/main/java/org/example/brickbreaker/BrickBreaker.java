package org.example.brickbreaker;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;


public class BrickBreaker extends Application  {

    Scene introScene ;
    Button menu = new Button("Menu");
    Scene playingScene;
    Pane outerRoot = new Pane();
    Ball ball = new Ball();
    Ball advancedBall;
    boolean isAdvancedBallCreated = false;
    Rectangle paddle = new Rectangle();
    Rectangle bottomZone = new Rectangle();
    Button startButton = new Button();
    Button level1 = new Button("level 1");
    Button level2 = new Button("level 2");
    Button level3 = new Button("level 3");
    VBox vbox2 = new VBox();

    boolean settingsOn;
    boolean levelsOn;
    boolean playFlag = true;
    boolean soundFlag = true;
    boolean musicFlag = true;
    int lives = 3;
    ArrayList<Rectangle>  numOfLives = new ArrayList<>(3);

    int score = 0;
    int numCrashed = 0;
    String level;
    int paddleStartSize = 150;
    boolean ballIsTouched;
    Robot robot = new Robot();
    Label lblScore = new Label("SCORE: " + 0);
    Media buttonsMedia = new Media(new File("src/main/resources/org/example/brickbreaker/assets/Click/ButtonClick.mp3").toURI().toString());
    MediaPlayer buttonsSound = new MediaPlayer(buttonsMedia);
    MediaPlayer breakingSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/breaking/Breaking.mp3").toURI().toString()));
    MediaPlayer gameOverSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/GameOver/GameOver.mp3").toURI().toString()));
    MediaPlayer advancedBallSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/GetAdvancedBall/bonus-points.mp3").toURI().toString()));
    MediaPlayer winningSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/Winning/Winning.mp3").toURI().toString()));
    ArrayList <Brick> bricks = new ArrayList<>();
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            movePaddle();

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
            if(!ballIsTouched)
            {
                checkCollisionPaddle(paddle);
                ball.setLayoutX(ball.getLayoutX() + ball.getDeltaX());
                ball.setLayoutY(ball.getLayoutY() + ball.getDeltaY());
                if(isAdvancedBallCreated)
                {
                    advancedBall.setLayoutY(advancedBall.getLayoutY() + advancedBall.getDeltaY());
                    checkCollisionPaddle(paddle,advancedBall);
                    checkCollisionBottomZone1(advancedBall);
                }
                if (!bricks.isEmpty()) {
                    bricks.removeIf(brick -> checkCollisionBrick(brick));
                } else {
                    score += 50000 * lives;
                    lblScore.setText("SCORE: " + score);
                    timeline.stop();
                    if(winningSound.getStatus() == MediaPlayer.Status.PLAYING)
                    {
                        winningSound.stop();
                        winningSound.seek(winningSound.getStartTime());
                    }
                    if(soundFlag)
                        winningSound.play();
                    System.out.println("You won!");
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

        stage.setResizable(false);

        level1.setOnAction( e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            playingScene = new Scene(outerRoot);
            level = "One";
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
        level2.setOnAction( e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            playingScene = new Scene(outerRoot);
            level = "Two";
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
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            playingScene = new Scene(outerRoot);
            level = "Three";
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
        menu.setOnAction(e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            timeline.stop();
            if(levelsOn)
            {
                levelsOn = false;
            }
            intro();
            stage.setScene(introScene);
            playingScene.setRoot(new Pane());
            outerRoot = new Pane();

        });
        intro();
        stage.setScene(introScene);
        stage.setTitle("BrickBreaker Game");
        stage.show();
    }

    public void initialize()
    {
        lives = 3;
        score = 0;
        numCrashed = 0;
        isAdvancedBallCreated = false;
        outerRoot.setPrefWidth(1080);
        outerRoot.setPrefHeight(720);
        paddle.setWidth(paddleStartSize);
        paddle.setArcWidth(5);
        paddle.setArcHeight(5);
        paddle.setStrokeType(StrokeType.INSIDE);
        paddle.setHeight(10);
        paddle.setLayoutX(outerRoot.getWidth() / 2 + paddle.getWidth() * 3);
        paddle.setLayoutY(outerRoot.getPrefHeight() - 50);
        ball.setLayoutY(paddle.getLayoutY() - 15);
        ball.setLayoutX(paddle.getLayoutX() +paddle.getWidth() / 2);
        ball.setRadius(12);
        timeline.setCycleCount(Animation.INDEFINITE);
        paddle.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\block.jpg")));
        outerRoot.setBackground(Background.fill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\71.jpg"))));
        ball.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
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
        startButton.setText("a7a");
        startButton.setPrefWidth(50);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(outerRoot.getPrefWidth() / 2 - startButton.getPrefWidth());
        startButton.setLayoutY((outerRoot.getPrefHeight() / 2) - startButton.getPrefHeight());
        startButton.setVisible(true);
        outerRoot.getChildren().addAll(paddle,ball,lblScore,bottomZone,startButton);
        drawLives(lives);
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
    public void checkCollisionPaddle(Rectangle paddle) {

        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {

            /*boolean rightBorder = ball.getLayoutX() >= ((paddle.getLayoutX() + paddle.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (paddle.getLayoutX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((paddle.getLayoutY() + paddle.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (paddle.getLayoutY() + ball.getRadius());

            checkBorders(rightBorder, leftBorder, bottomBorder, topBorder);*/
            double relativeIntersectX = (ball.getLayoutX() + ball.getRadius()) - (paddle.getLayoutX() + paddle.getWidth() / 2);
            double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);
            double bounceAngle = normalizedIntersectX * Math.PI / 3; // Maximum bounce angle
            ball.setDeltaX( Math.sin(bounceAngle) * 4);
            ball.setDeltaY(-Math.cos(bounceAngle) * 4);


        }

    }
    public boolean checkCollisionBrick(Brick brick) {

        if (ball.getBoundsInParent().intersects(brick.getBoundsInParent()) ) {
            boolean rightBorder = ball.getLayoutX() >= ((brick.getX() + brick.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (brick.getX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((brick.getY() + brick.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (brick.getY() + ball.getRadius());
            checkBorders(rightBorder, leftBorder, bottomBorder, topBorder);
            brick.setNumOfCrashes(brick.getNumOfCrashes() - 1);
            if(breakingSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                breakingSound.seek(breakingSound.getStartTime());
            }
            if(soundFlag)
                breakingSound.play();

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
            lives--;
            outerRoot.getChildren().remove(numOfLives.getLast());
            numOfLives.removeLast();

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
                if(gameOverSound.getStatus() == MediaPlayer.Status.PLAYING)
                {
                    gameOverSound.stop();
                    gameOverSound.seek(gameOverSound.getStartTime());
                }
                if(soundFlag)
                    gameOverSound.play();
                outerRoot.getChildren().remove(advancedBall);
                bricks.forEach(brick -> outerRoot.getChildren().remove(brick));
                bricks.clear();
                startButton.setVisible(true);
                score = 0;
                lives = 3;
                drawLives(lives);
                paddle.setWidth(paddleStartSize);
                paddle.setLayoutX(outerRoot.getWidth() / 2 - paddle.getWidth() / 2);
                int random = ((int) (Math.random() * 100)) % 4;
                if (random >= 2) {
                    ball.setDeltaX(-Math.random() * 4);
                    ball.setDeltaY(-4);
                } else {
                    ball.setDeltaX(Math.random() * 4);
                    ball.setDeltaY(-4);
                }

                ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
                ball.setLayoutY(paddle.getLayoutY() - 10);

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
            case "One":

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
                                /*rick.layoutXProperty().bind();*/
                               /* brick.widthProperty().bind(outerRoot.widthProperty().subtract(numOfBricksInRow * space).subtract(10).divide(numOfBricksInRow));*/
                                bricks.add(brick);
                        }
                        columnCount++;
                }
            case "Two":
                break;
            case "Three":
                break;

        }
    }
    private void intro()
    {
        Button levels = new Button("Levels");
        Button setting = new Button("Setting");
        Button sound = new Button("sound");
        Button music = new Button("Music");
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
        sound.setOnAction(e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
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
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            if(musicFlag)
            {
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
            else{
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
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            levelsOn = !levelsOn;
            level1.setVisible(levelsOn);
            level2.setVisible(levelsOn);
            level3.setVisible(levelsOn);
        });
        setting.setOnAction(event -> {
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
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


        /*levels.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        level1.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        level2.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        level3.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        setting.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        sound.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        music.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");
        exit.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: hotpink; -fx-background-color: snow; -fx-background-radius: 20; -fx-border-color: snow; -fx-border-radius: 20; -fx-padding: 0;");*/
        /*settingsImg.setFitHeight(setting.getHeight());
        settingsImg.setFitWidth(setting.getWidth());*/

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
        advancedBall.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\addedLife.png")));
        advancedBall.setStroke(Color.BLACK);
        advancedBall.setDeltaX(0);
        advancedBall.setDeltaY(1.5);
        isAdvancedBallCreated = true;
        outerRoot.getChildren().add(advancedBall);
    }
    private void addLives()
    {
        numOfLives.clear();
        lives++;
        drawLives(lives);
    }
    private void checkCollisionPaddle(Rectangle paddle,Ball advancedBall) {

        if (advancedBall.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
            if(advancedBallSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                advancedBallSound.stop();
                advancedBallSound.seek(advancedBallSound.getStartTime());
            }
            if(soundFlag)
                advancedBallSound.play();
            outerRoot.getChildren().remove(advancedBall);
            isAdvancedBallCreated = false;
            if(lives == 5){
                score += 1000;
                lblScore.setText("SCORE: " + score);
            }
            if(lives < 5)
                addLives();
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
        /*Button setting = new Button("Setting");*/
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
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            timeline.play();
            playFlag = !playFlag;
            outerRoot.getChildren().remove(rectangle);
            outerRoot.getChildren().remove(vbox2);
            vbox2.getChildren().clear();
        });
        ImageView soundImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
        soundImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        soundImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        ImageView musicImg = new ImageView(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\musicIcon.png"));
        musicImg.fitHeightProperty().bind(sound.prefHeightProperty().divide(1.2));
        musicImg.fitWidthProperty().bind(soundImg.fitHeightProperty());
        sound.setOnAction(e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            if(soundFlag)
            {
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\SoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
            else{
                soundImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteSoundIcon.png"));
                sound.setGraphic(soundImg);
                soundFlag = !soundFlag;
            }
        });
        sound.setGraphic(soundImg);

        music.setOnAction(e ->{
            if(buttonsSound.getStatus() == MediaPlayer.Status.PLAYING)
            {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if(soundFlag)
                buttonsSound.play();
            if(musicFlag)
            {
                musicImg.setImage(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\muteMusicIcon.png"));
                music.setGraphic(musicImg);
                musicFlag = !musicFlag;
            }
            else{
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
        /*setting.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        setting.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));*/
        sound.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        sound.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        music.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        music.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,20));
        /*HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(cont,setting,menu);
        hbox2.setSpacing(15);
        hbox2.setAlignment(Pos.CENTER);*/

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

