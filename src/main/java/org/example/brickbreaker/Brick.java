package org.example.brickbreaker;

import javafx.scene.shape.Rectangle;

public class Brick  extends Rectangle {
    public Brick(double x, double y, double width, double height)
    {
        super(x,y,width,height);
    }
    private boolean crashed;

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }
}
