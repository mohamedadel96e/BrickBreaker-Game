package org.example.brickbreaker;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class LoosingScene extends Pane{
    private Button playAgainButton = new Button("Play Again");
    private Button menuButton = new Button("Menu");

    private HBox hBox = new HBox(50);


    public LoosingScene()
    {
        ImagePattern win = new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\lose.jpg"));
        this.setBackground(Background.fill(win));
        hBox.getChildren().addAll(playAgainButton,menuButton);
        hBox.setLayoutY(480);
        hBox.setLayoutX(350);
        this.getChildren().addAll(hBox);
        playAgainButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        menuButton.setFont(Font.font("DejaVu Math TeX Gyre", FontWeight.BOLD, FontPosture.ITALIC,25));
        playAgainButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
        menuButton.setStyle("-fx-background-radius: 20; -fx-padding: 10 20;-fx-text-fill: hotpink");
    }

    public Button getPlayAgainButton() {
        return playAgainButton;
    }
    public Button getMenuButton() {
        return menuButton;
    }

}
