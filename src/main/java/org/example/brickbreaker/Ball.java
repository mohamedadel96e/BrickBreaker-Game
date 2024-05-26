package org.example.brickbreaker;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Ball extends Circle {
    private double deltaX;
    private double deltaY;
    private boolean fire = false;
    private int advancedBallType = 0; /*  0 --> +live Ball
                                          1 --> Fire Ball
                                          */

    public int getAdvancedBallType() {
        return advancedBallType;
    }

    public void setAdvancedBallType(int advancedBallType) {
        this.advancedBallType = advancedBallType;
        switch (advancedBallType)
        {
            case 0 :
                setFill(new ImagePattern(new Image("/org/example/brickbreaker/assets/addedLife.png")));
                break;
            case 1:
                setFill(new ImagePattern(new Image("/org/example/brickbreaker/assets/flame.png")));
                break;
        }
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
        if(fire)
            setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\goldball.png")));
        else
            setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));

    }

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
        setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\ball.png")));
    }
}
