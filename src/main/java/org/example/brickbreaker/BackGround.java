package org.example.brickbreaker;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;

public class BackGround extends Pane {
    public BackGround(){
        setPrefWidth(1080);
        setPrefHeight(720);
        Image playImage = new Image("/org/example/brickbreaker/assets/playingBG.png");
        setBackground(Background.fill(new ImagePattern(playImage)));
    }
}
