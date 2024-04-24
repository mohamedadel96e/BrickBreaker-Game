package org.example.brickbreaker;

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
}
