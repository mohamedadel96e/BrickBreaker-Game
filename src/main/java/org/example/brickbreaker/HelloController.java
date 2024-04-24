package org.example.brickbreaker;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private AnchorPane scene;

    @FXML
    private Ball ball;

    @FXML
    private Rectangle paddle;

    @FXML
    private Rectangle bottomZone;

    @FXML
    private Button startButton;

    private int paddleStartSize = 600;

    Robot robot = new Robot();

    private ArrayList<Brick> bricks = new ArrayList<>();

    double deltaX = -1;
    double deltaY = -1;

    //1 Frame evey 10 millis, which means 100 FPS
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            movePaddle();
            checkCollisionPaddle(paddle);
            ball.setLayoutX(ball.getLayoutX() + deltaX);
            ball.setLayoutY(ball.getLayoutY() + deltaY);



            if(!bricks.isEmpty()){
                bricks.removeIf(brick -> checkCollisionBrick(brick));
            } else {
                timeline.stop();
                System.out.println("You won!");
            }

            checkCollisionScene(scene);
            checkCollisionBottomZone();

        }
    }));


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paddle.setWidth(paddleStartSize);
        paddle.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\block.jpg")));
        scene.setBackground(Background.fill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\bg.jpg"))));
        ball.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    @FXML
    void startGameButtonAction(ActionEvent event) {
        startButton.setVisible(false);
        startGame();

    }

    public void startGame(){
        createBricks();
        timeline.play();
    }

    public void checkCollisionScene(Node node){
        Bounds bounds = node.getBoundsInLocal();
        boolean rightBorder = ball.getLayoutX() >= (bounds.getMaxX() - ball.getRadius());
        boolean leftBorder = ball.getLayoutX() <= (bounds.getMinX() + ball.getRadius());
        boolean bottomBorder = ball.getLayoutY() >= (bounds.getMaxY() - ball.getRadius());
        boolean topBorder = ball.getLayoutY() <= (bounds.getMinY() + ball.getRadius());

        if (rightBorder || leftBorder) {
            deltaX *= -1;
        }
        if (bottomBorder || topBorder) {
            deltaY *= -1;
        }
    }


    public boolean checkCollisionBrick(Brick brick){

        if(ball.getBoundsInParent().intersects(brick.getBoundsInParent())){
            boolean rightBorder = ball.getCenterX() >= brick.getX() - brick.getWidth() / 2 - ball.getRadius() ;
            boolean leftBorder = ball.getCenterX() <= brick.getX() + brick.getWidth() / 2 + ball.getRadius() ;
            boolean bottomBorder = ball.getCenterY() >= brick.getY() + brick.getHeight() / 2 + ball.getRadius() ;
            boolean topBorder = ball.getCenterY() <= brick.getY() - brick.getHeight() / 2 - ball.getRadius() ;
            /*boolean bottomLeft ;
            boolean bottomRight;
            boolean topLeft;
            boolean topRight;*/

            if(bottomBorder || topBorder) {
                deltaY *= -1;
            }
            else if(rightBorder || leftBorder)
            {
                deltaX *= -1;
            }



            paddle.setWidth(paddle.getWidth() - (0.05 * paddle.getWidth()));
            scene.getChildren().remove(brick);

            return true;
        }
        return false;
    }


    public void createBricks(){
        double width = 560;
        double height = 200;

        int spaceCheck = 1;

        for (double i = height; i > 0 ; i = i - 50) {
            for (double j = width; j > 0 ; j = j - 25) {
                if(spaceCheck % 2 == 0){
                    Brick brick = new Brick(j,i,30,30);
                    brick.setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\c1.png")));
                    scene.getChildren().add(brick);
                    bricks.add(brick);
                }
                spaceCheck++;
            }
        }
    }

    public void movePaddle(){
        Bounds bounds = scene.localToScreen(scene.getBoundsInLocal());
        double sceneXPos = bounds.getMinX();

        double xPos = robot.getMouseX();
        double paddleWidth = paddle.getWidth();

        if(xPos >= sceneXPos + (paddleWidth/2) && xPos <= (sceneXPos + scene.getWidth()) - (paddleWidth/2)){
            paddle.setLayoutX(xPos - sceneXPos - (paddleWidth/2));
        } else if (xPos < sceneXPos + (paddleWidth/2)){
            paddle.setLayoutX(0);
        } else if (xPos > (sceneXPos + scene.getWidth()) - (paddleWidth/2)){
            paddle.setLayoutX(scene.getWidth() - paddleWidth);
        }
    }

    public void checkCollisionPaddle(Rectangle paddle){

        if(ball.getBoundsInParent().intersects(paddle.getBoundsInParent())){

            boolean rightBorder = ball.getLayoutX() >= ((paddle.getLayoutX() + paddle.getWidth()) - ball.getRadius());
            boolean leftBorder = ball.getLayoutX() <= (paddle.getLayoutX() + ball.getRadius());
            boolean bottomBorder = ball.getLayoutY() >= ((paddle.getLayoutY() + paddle.getHeight()) - ball.getRadius());
            boolean topBorder = ball.getLayoutY() <= (paddle.getLayoutY() + ball.getRadius());

            if (rightBorder && deltaX < 0) {
                deltaX *= -1;
            }
            if(leftBorder && deltaX > 0)
            {
                deltaX *= -1;
            }
            if (bottomBorder || topBorder) {
                deltaY *= -1;
            }
        }
    }

    public void checkCollisionBottomZone(){
        if(ball.getBoundsInParent().intersects(bottomZone.getBoundsInParent())){
            timeline.stop();
            bricks.forEach(brick -> scene.getChildren().remove(brick));
            bricks.clear();
            startButton.setVisible(true);

            paddle.setWidth(paddleStartSize);

            deltaX = -1;
            deltaY = -3;

            ball.setLayoutX(300);
            ball.setLayoutY(300);

            System.out.println("Game over!");
        }
    }
}