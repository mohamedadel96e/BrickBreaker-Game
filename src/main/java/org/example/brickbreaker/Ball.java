package org.example.brickbreaker;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Ball extends Circle {
    private double deltaX;
    private double deltaY;

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public Ball(double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
    }

    public Ball() {
    }
}
