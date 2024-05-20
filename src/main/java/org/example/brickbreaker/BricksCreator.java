package org.example.brickbreaker;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class BricksCreator {
    int level;
    Pane root;
    ArrayList <Brick> bricks = new ArrayList<>();

    public ArrayList<Brick> getBricks() {
        return bricks;
    }

    public BricksCreator(Pane root, int level) {
        this.root = root;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public void setRoot(Pane root) {
        this.root = root;
    }

    public void createBricks() {
        // Variables used for brick creation
        Brick brick;  // Represents a single brick object as a buffer
        double width = root.getPrefWidth();  // Get the scene's preferred width
        double height = root.getPrefHeight();  // Get the scene's preferred height
        double space = 15;  // Space between bricks
        int numOfBricksInRow = 10;  // Number of bricks in a row
        int numOfBrickInColumn = 5;  // Number of brick columns

        // Calculate individual brick width based on scene size and number of bricks
        double brickWidth = (root.getPrefWidth() - numOfBricksInRow * space - 10) / numOfBricksInRow;
        double brickHeight = brickWidth * 0.5;  // Set brick height to half the width because of the photo i have 2 * 1

        // Counter variable for number of brick columns created
        int columnCount = 0;

        // Switch statement to handle different level layouts
        switch (level) {
            case 1:
                // Level 2: Brick triangle formation on each half of the scene
                int bricksTriangle = 5;  // Number of rows in each triangle

                // Calculate adjusted brick width to fit the triangle formation within half the scene width
                brickWidth = (width / 2 - bricksTriangle * space) / bricksTriangle;

                // Loop to create bricks for the left triangle
                int rowCount = 0;  // Counter for the number of bricks in a row (reset for each row)
                for (double i = 50;
                     (i < height * 0.6) && columnCount < bricksTriangle;
                     i = i + brickWidth / 2 + space) {
                    for (double j = 10; (j < width / 2) && rowCount <= columnCount; j = j + brickWidth + space) {
                        rowCount++;

                        // Create a brick with health 1 for the last row of the triangle
                        brick = switch (columnCount) {
                            case 4 -> new Brick(j, i, brickWidth, brickWidth / 2, 1);
                            default -> new Brick(j, i, brickWidth, brickWidth / 2, random1To3());
                        };

                        // Add the brick to the scene and the list of bricks
                        root.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    rowCount = 0;  // Reset the row counter for the next row
                    columnCount++;  // Increment the column counter after completing a row
                }

                // Reset counters for the right triangle
                columnCount = 0;

                // Loop to create bricks for the right triangle (mirrored from the left)
                for (double i = 50;
                     (i < height * 0.6) && columnCount < bricksTriangle;
                     i = i + brickWidth / 2 + space) {
                    columnCount++;
                    for (double j = root.getPrefWidth() - 10 - brickWidth;
                         (j > width / 2) && rowCount < columnCount;
                         j = j - brickWidth - space) {
                        rowCount++;

                        // Create a brick with health 1 for the last row of the triangle
                        brick = switch (columnCount) {
                            case 5 -> new Brick(j, i, brickWidth, brickWidth / 2, 1);
                            default -> new Brick(j, i, brickWidth, brickWidth / 2, random1To3());
                        };

                        // Add the brick to the scene and the list of bricks
                        root.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    rowCount = 0;  // Reset the row counter for the next row
                }
                break;
            case 2:
                // Level 1: Brick rectangle formation
                for (double i = 50; i < height * 0.6; i = i + brickHeight + space) {
                    if (columnCount >= numOfBrickInColumn) {
                        break;
                    }
                    for (double j = 10; j < width - 10; j = j + brickWidth + space) {

                        // Create a brick with random health (1-3) except for the last column (health = 1) and before last (health = 2)
                        brick = switch (columnCount) {
                            case 4 -> new Brick(j, i, brickWidth, brickHeight, 1);
                            case 3 -> new Brick(j, i, brickWidth, brickHeight, 2);
                            default -> new Brick(j, i, brickWidth, brickHeight, random1To3());
                        };

                        // Add the brick to the scene and the list of bricks
                        root.getChildren().add(brick);
                        bricks.add(brick);
                    }
                    columnCount++;//increment the bricks column created
                }
                break;
            case 3:
                int diamondRows = 5;  // Number of rows in each half of the diamond
                int diamonds = 3;  // Number of diamonds aligned horizontally

                // Calculate adjusted brick width to fit the diamonds within the scene width
                brickWidth = (width / (diamonds ) - space * diamondRows) / diamondRows;

                // Loop to create bricks for each diamond
                for (int d = 0; d < diamonds; d++) {
                    double offsetX = d * (width / diamonds) + 5;  // Offset for each diamond

                    // Upper half of the diamond
                    for (int row = 0; row < diamondRows; row++) {
                        double y = 50 + row * (brickWidth / 2 + space);
                        for (int col = 0; col <= row; col++) {
                            double x = offsetX + (width / (diamonds * 2.5)) - (row * (brickWidth + space) / 2) + col * (brickWidth + space);
                            brick = new Brick(x, y, brickWidth, brickWidth / 2, random1To3());
                            root.getChildren().add(brick);
                            bricks.add(brick);
                        }
                    }

                    // Lower half of the diamond
                    for (int row = diamondRows - 1; row >= 0; row--) {
                        double y = 50 + (diamondRows * (brickWidth / 2 + space)) + row * (brickWidth / 2 + space);
                        for (int col = 0; col <= row; col++) {
                            double x = offsetX + (width / (diamonds * 2.5)) - (row * (brickWidth + space) / 2) + col * (brickWidth + space);
                            brick = new Brick(x, y, brickWidth, brickWidth / 2, random1To3());
                            root.getChildren().add(brick);
                            bricks.add(brick);
                        }
                    }
                }
                break;
        }
    }
    private int random1To3()
    {
        return (((int)(Math.random() * 100)) % 3) + 1;
    }
}
