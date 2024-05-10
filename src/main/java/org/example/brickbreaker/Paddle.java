package org.example.brickbreaker;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Paddle extends Rectangle {

    public Paddle()
    {
        setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\block.jpg")));
        setArcWidth(5);
        setArcHeight(5);
        setStrokeType(StrokeType.INSIDE);
        setHeight(10);
    }
}