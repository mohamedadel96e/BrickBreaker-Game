package org.example.brickbreaker;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.example.brickbreaker.Ball;

public class HelloController implements Initializable {

    @FXML
    private Pane scene;

    @FXML
    private Ball ball;

    @FXML
    private Rectangle paddle;

    @FXML
    private Rectangle bottomZone;

    @FXML
    private Button startButton;

    private int lives = 3;

    private int score = 0;

    private String level;

    private int paddleStartSize = 200;
    boolean ballIsTouched;

    private enum Modes {START,PLAY,PAUSE,WIN,LOSE};
    Modes mode;
    Robot robot = new Robot();

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
                    timeline.stop();
                    System.out.println("You won!");
                }
                checkCollisionScene(scene);
                checkCollisionBottomZone();
            }
            else{
                ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth()  / 2);
                ball.setLayoutY(paddle.getLayoutY() - 10);
                scene.setOnMouseClicked(e ->
                {
                    ballIsTouched = false;
                });
            }
        }
    }));


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paddle.setWidth(paddleStartSize);
        paddle.setLayoutX(scene.getWidth() / 2 + paddle.getWidth());
        mode = Modes.START;
        paddle.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\block.jpg")));
        scene.setBackground(Background.fill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\71.jpg"))));
        ball.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
        timeline.setCycleCount(Animation.INDEFINITE);
        ball.setLayoutY( paddle.getLayoutY() - 10);
        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        int random = (int) Math.random() % 4;
        if(random >= 2)
        {
            ball.setDeltaX(-1);
            ball.setDeltaY(-1);
        }
        else {
            ball.setDeltaX(1);
            ball.setDeltaY(1);
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

            boolean bottomBorder = ball.getCenterY() >= brick.getY() + brick.getHeight() / 2 + ball.getRadius() ;
            boolean topBorder = ball.getCenterY() <= brick.getY() - brick.getHeight() / 2 - ball.getRadius();
            boolean rightBorder = ball.getCenterX() >= brick.getX() - brick.getWidth() / 2 - ball.getRadius();
            boolean leftBorder = ball.getCenterX() <= brick.getX() + brick.getWidth() / 2 + ball.getRadius();

            if (bottomBorder || topBorder) {
                ball.setDeltaY(ball.getDeltaY() * -1);
            }
            else if (rightBorder || leftBorder) {
                ball.setDeltaX(ball.getDeltaX() * -1);
            }
            scene.getChildren().remove(brick);
            if(lives == 3)
                score += 20;
            if(lives == 2)
                score += 50;
            if(lives == 1)
                score += 70;


            return true;
        }
        return false;
    }
    public void createBricks() {
        double width = 560;
        double height = 200;
        level = "One";
        int spaceCheck = 1;
        switch (level)
        {
            case "One":
                for (double i = height; i > 0; i = i - 50) {
                    for (double j = width; j > 0; j = j - 25) {
                        if (spaceCheck % 2 == 0) {
                            Brick brick = new Brick(j, i, 40, 20);
                            brick.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\93.jpg")));
                            scene.getChildren().add(brick);
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
        Bounds bounds = scene.localToScreen(scene.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();
        double paddleWidth = paddle.getWidth();

        if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
        } else if (xPos < sceneXPos + (paddleWidth / 2)) {
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
            paddle.setLayoutX(scene.getWidth() - paddleWidth);
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
            if(lives > 0)
            {
                ballIsTouched = true;
                ball.setLayoutX(paddle.getLayoutX());
                ball.setLayoutY(paddle.getLayoutY());
                Bounds bounds = scene.localToScreen(scene.getBoundsInLocal());
                double sceneXPos = bounds.getMinX();
                double xPos = robot.getMouseX();
                double paddleWidth = paddle.getWidth();

                if (xPos >= sceneXPos + (paddleWidth / 2) && xPos <= (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(xPos - sceneXPos - (paddleWidth / 2));
                } else if (xPos < sceneXPos + (paddleWidth / 2)) {
                    paddle.setLayoutX(0);
                } else if (xPos > (sceneXPos + scene.getWidth()) - (paddleWidth / 2)) {
                    paddle.setLayoutX(scene.getWidth() - paddleWidth);
                }

                if(((int) Math.random() )% 4 >= 2)
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
                bricks.forEach(brick -> scene.getChildren().remove(brick));
                bricks.clear();
                startButton.setVisible(true);
                lives = 3;
                paddle.setWidth(paddleStartSize);
                paddle.setLayoutX(scene.getWidth() / 2  - paddle.getWidth() / 2);
                int random = (int) Math.random() % 4;
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

}
