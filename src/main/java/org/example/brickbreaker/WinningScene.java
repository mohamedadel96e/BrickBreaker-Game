package org.example.brickbreaker;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class WinningScene extends Pane {
    private Button nextLevelButton = new Button("Next Level");
    private Button playAgainButton = new Button("Play Again");
    public Button menuButton = new Button("Menu");
    private HBox hBox = new HBox(30);
    private Text winText = new Text("Congratulations");
    private int numOfStars = 0;
    private ArrayList<Rectangle> stars = new ArrayList(0);
    public WinningScene()
    {
        ImagePattern win = new ImagePattern(new Image("/org/example/brickbreaker/assets/winningPhoto.jpg"));
        this.setBackground(Background.fill(win));
        hBox.getChildren().addAll(nextLevelButton,playAgainButton,menuButton);
        hBox.setLayoutY(550);
        hBox.setLayoutX(270);
        winText.setLayoutY(150);
        winText.setLayoutX(350);
        winText.setFill(Color.HOTPINK);
        winText.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,50));
        this.getChildren().addAll(winText,hBox);
        nextLevelButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        playAgainButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        menuButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        nextLevelButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        playAgainButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        menuButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
    }
    public void drawStars(int score)
    {
        stars.clear();
        if (score >= 150000)
            numOfStars = 3;
        else if (score >= 100000)
            numOfStars = 2;
        else if(score >= 50000)
            numOfStars = 1;

        for(int i = 0; i < numOfStars;i++)
        {
            stars.add(new Rectangle(150 * i + 320,20,100,100));
            stars.get(i).setFill(new ImagePattern(new Image("/org/example/brickbreaker/assets/StarIcon.png")));
            this.getChildren().add(stars.get(i));
        }

    }
    public void playAgain(Scene scene, Pane pane, MediaPlayer buttonsSound,boolean soundFlag,Paddle paddle,Ball ball)
    {
        playAgainButton.setOnAction(e -> {
            scene.setRoot(pane);
            if (buttonsSound.getStatus() == MediaPlayer.Status.PLAYING) {
                buttonsSound.stop();
                buttonsSound.seek(buttonsSound.getStartTime());
            }
            if (soundFlag)
                buttonsSound.play();

            for (int i = 0; i < numOfStars; i++) {
                this.getChildren().remove(stars.get(i));
            }
            stars.clear();
            numOfStars = 0;
            paddle.setLayoutX(pane.getWidth() / 2 - paddle.getWidth() / 2);
            ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        });
    }
    public void menuButtonHandle(){
        for (int i = 0; i < numOfStars; i++) {
            this.getChildren().remove(stars.get(i));
        }
        stars.clear();
        numOfStars = 0;
    }

    public Button getNextLevelButton() {
        return nextLevelButton;
    }
}
