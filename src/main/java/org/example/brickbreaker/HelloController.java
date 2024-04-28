package org.example.brickbreaker;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.example.brickbreaker.Ball;

public class HelloController implements Initializable {

    @FXML
    private Pane root;

    @FXML
    private Ball ball;

    @FXML
    private Rectangle paddle;

    @FXML
    private Rectangle bottomZone;

    @FXML
    private Button startButton;

    private int lives = 3;
    private  ArrayList <Rectangle> numOfLives = new ArrayList<>(3);

    private int score = 0;

    private String level;


    private int paddleStartSize = 150;
    boolean ballIsTouched;

    private enum Modes {START,PLAY,PAUSE,WIN,LOSE};
    Modes mode;
    Robot robot = new Robot();


    Label lblScore = new Label("SCORE: " +  0);

    private ArrayList<Brick> bricks = new ArrayList<>();

    /*double deltaX = -1;
    double deltaY = -1;*/

    //1 Frame evey 10 millis, which means 100 FPS
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {

            movePaddle();
            if(!ballIsTouched)
            {
                mode = mode.PLAY;
                checkCollisionPaddle(paddle);
                ball.setLayoutX(ball.getLayoutX() + ball.getDeltaX());
                ball.setLayoutY(ball.getLayoutY() + ball.getDeltaY());

                if (!bricks.isEmpty()) {
                    bricks.removeIf(brick -> checkCollisionBrick(brick));

                } else {
                    score += 50000 * lives;
                    lblScore.setText("SCORE: " + score);
                    timeline.stop();
                    System.out.println("You won!");
                }
                checkCollisionScene(root);
                checkCollisionBottomZone();
            }
            else{
                ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth()  / 2);
                ball.setLayoutY(paddle.getLayoutY() - 10);
                root.setOnMouseClicked(e ->
                {
                    ballIsTouched = false;
                });
            }
        }
    }));


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paddle.setWidth(paddleStartSize);
        paddle.setLayoutX(root.getWidth() / 2 + paddle.getWidth());
        mode = Modes.START;
        paddle.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\block.jpg")));
        root.setBackground(Background.fill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\71.jpg"))));
        ball.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
        timeline.setCycleCount(Animation.INDEFINITE);
        ball.setLayoutY( paddle.getLayoutY() - 10);
        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        lblScore.setText("SCORE: " + score);
        lblScore.setLayoutX(root.getPrefWidth() - 100);
        lblScore.setFont(Font.font("Elephant"));
        lblScore.setTextFill(Color.BLACK);
        lblScore.setLayoutY(10);

        lblScore.setBackground(Background.fill(Color.WHITE));
        root.getChildren().add(lblScore);
        drawLives(lives);
        int random = (((int) (Math.random() * 100)) %  4);
        if(random >= 2)
        {
            ball.setDeltaX(-1);
            ball.setDeltaY(-1);
        }
        else {
            ball.setDeltaX(1);
            ball.setDeltaY(-1);
        }
    }

    @FXML
    void startGameButtonAction(ActionEvent event) {
        startButton.setVisible(false);
        startGame();

    }

    public void startGame() {
        createBricks();
        timeline.play();
    }

    public void checkCollisionScene(Node node) {
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
        boolean leftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
        boolean bottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius());
        boolean topBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius());

        if (rightBorder || leftBorder) {
            ball.setDeltaX(ball.getDeltaX() * -1);
        }
        if (bottomBorder || topBorder) {
            ball.setDeltaY(ball.getDeltaY() * -1);
        }
    }


    public boolean checkCollisionBrick(Brick brick) {

        if (ball.getBoundsInParent().intersects(brick.getBoundsInParent()) ) {

            boolean bottomBorder = ball.getLayoutY() >= ((brick.getY() + brick.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (brick.getY() + ball.getRadius());
            boolean rightBorder = ball.getLayoutX() >= ((brick.getX() + brick.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (brick.getX() + ball.getRadius());

            /*if(bottomBorder  && ball.getDeltaY() < 0){
                ball.setDeltaY(ball.getDeltaY() * -1);
            } else if (topBorder && ball.getDeltaY() > 0) {
                ball.setDeltaY(ball.getDeltaY() * -1);
            }
            else if (rightBorder || leftBorder ) {
                ball.setDeltaX(ball.getDeltaX() * -1);
            }*/
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

            root.getChildren().remove(brick);
            if(numOfLives.size() == 3)
                score += 200;
            if(numOfLives.size() == 2)
                score += 500;
            if(numOfLives.size() == 1)
                score += 700;
            lblScore.setText("SCORE: " + score);


            return true;
        }
        return false;
    }
    public void createBricks() {
        double width = root.getScene().getWidth() - 20;
        double height = root.getScene().getHeight() * 0.5;
        level = "One";
        int spaceCheck = 1;
        switch (level)
        {
            case "One":
                for (double i = height; i > 0; i = i - 50) {
                    for (double j = width; j > 0; j = j - 30) {
                        if (spaceCheck % 2 == 0) {
                            Brick brick = new Brick(j, i, 40, 20);
                            brick.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\93.jpg")));
                            root.getChildren().add(brick);
                            bricks.add(brick);
                        }
                        spaceCheck++;
                    }
                }
                break;
            case "Two":
                break;
        }
    }

    public void movePaddle() {
        Bounds bounds = root.localToScreen(root.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();
        double paddleWidth = paddle.getWidth();

        if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + root.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
        } else if (xPos < sceneXPos + (paddleWidth / 2)) {
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + root.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(root.getWidth() - paddleWidth);
        }
    }

    public void checkCollisionPaddle(Rectangle paddle) {

        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {

            boolean rightBorder = ball.getLayoutX() >= ((paddle.getLayoutX() + paddle.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (paddle.getLayoutX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((paddle.getLayoutY() + paddle.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (paddle.getLayoutY() + ball.getRadius());

            if (rightBorder && ball.getDeltaX() < 0) {
                ball.setDeltaX(ball.getDeltaX() * -1);
            }
            if (leftBorder && (ball.getDeltaX() > 0)) {
                ball.setDeltaX(ball.getDeltaX() * -1);
            }
            if (bottomBorder || topBorder) {
                ball.setDeltaY(ball.getDeltaY() * -1);
            }
        }
    }

    public void checkCollisionBottomZone() {
        if (ball.getBoundsInParent().intersects(bottomZone.getBoundsInParent())) {
            lives--;
            root.getChildren().remove(numOfLives.getLast());
            numOfLives.removeLast();

            if(lives > 0)
            {
                ballIsTouched = true;
                ball.setLayoutX(paddle.getLayoutX());
                ball.setLayoutY(paddle.getLayoutY());

                Bounds bounds = root.localToScreen(root.getBoundsInLocal());
                double sceneXPos = bounds.getMinX();
                double xPos = robot.getMouseX();
                double paddleWidth = paddle.getWidth();

                if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + root.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
                } else if (xPos < sceneXPos + (paddleWidth / 2)) {
                    paddle.setLayoutX(0);
                } else if (xPos > (sceneXPos + root.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(root.getWidth() - paddleWidth);
                }

                if((((int) (Math.random() * 100)) %  4) >= 2)
                {
                    ball.setDeltaX(-1);
                    ball.setDeltaY(-1);
                }
                else {
                    ball.setDeltaX(1);
                    ball.setDeltaY(1);
                }
            }
            else {

                timeline.stop();
                mode = Modes.LOSE;
                bricks.forEach(brick -> root.getChildren().remove(brick));
                bricks.clear();
                startButton.setVisible(true);
                score = 0;
                lives = 3;
                drawLives(lives);
                paddle.setWidth(paddleStartSize);
                paddle.setLayoutX(root.getWidth() / 2  - paddle.getWidth() / 2);
                int random = ((int) (Math.random() * 100)) %  4;
                if(random >= 2)
                {
                    ball.setDeltaX(-1);
                    ball.setDeltaY(-1);
                }
                else {
                    ball.setDeltaX(1);
                    ball.setDeltaY(1);
                }

                ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
                ball.setLayoutY(paddle.getLayoutY() - 10);

                System.out.println("Game over!");
            }

        }
    }
    private void drawLives(int lives)
    {
        for(int i = 0; i < lives; i++)
        {
            numOfLives.add(new Rectangle(40 * i + 10,10,30,30));
            numOfLives.get(i).toBack();
            numOfLives.get(i).setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\heart.png")));

            root.getChildren().add(numOfLives.get(i));

        }
    }
}
