package org.example.brickbreaker;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Brick  extends Rectangle {

    private boolean crashed = false;

    int numOfCrashes = 1;



    public Brick()
    {

    }
    public Brick(double x, double y, double width, double height,int numOfCrashes)
    {
        super(x,y,width,height);
        this.numOfCrashes = numOfCrashes;
        automateFill();

    }

    public int getNumOfCrashes() {
        return numOfCrashes;
    }

    public void setNumOfCrashes(int numOfCrashes) {
        this.numOfCrashes = numOfCrashes;
        automateFill();
    }
    public void automateFill()
    {
        switch (numOfCrashes)
        {
            case 1:
                setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\brick3.png")));
                break;
            case 2:
                setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\brick6.png")));
                break;
            case 3:
                setFill(new ImagePattern(new Image("file:D:\\2nd Semester\\Programming\\2nd Semester Project\\BrickBreaker\\src\\main\\resources\\org\\example\\brickbreaker\\assets\\brick5.png")));
        }
    }
}
