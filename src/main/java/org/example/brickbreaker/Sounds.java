package org.example.brickbreaker;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Sounds {
    Media buttonsMedia = new Media(new File("src/main/resources/org/example/brickbreaker/assets/Click/ButtonClick.mp3").toURI().toString());
    MediaPlayer buttonsSound = new MediaPlayer(buttonsMedia);
    MediaPlayer breakingSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/breaking/Breaking.mp3").toURI().toString()));
    MediaPlayer gameOverSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/GameOver/GameOver.mp3").toURI().toString()));
    MediaPlayer advancedBallSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/GetAdvancedBall/bonus-points.mp3").toURI().toString()));
    MediaPlayer winningSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/Winning/Winning.mp3").toURI().toString()));
    MediaPlayer musicSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/musicSound.mp3").toURI().toString()));
    MediaPlayer heartSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/breaking/glassBreaking.mp3").toURI().toString()));
    MediaPlayer fireSound = new MediaPlayer(new Media(new File("src/main/resources/org/example/brickbreaker/assets/fireSound.mp3").toURI().toString()));
    public Sounds(){

    }

    public MediaPlayer getFireSound() {
        return fireSound;
    }

    public MediaPlayer getHeartSound() {
        return heartSound;
    }

    public MediaPlayer getButtonsSound() {
        return buttonsSound;
    }

    public MediaPlayer getBreakingSound() {
        return breakingSound;
    }

    public MediaPlayer getGameOverSound() {
        return gameOverSound;
    }

    public MediaPlayer getAdvancedBallSound() {
        return advancedBallSound;
    }

    public MediaPlayer getWinningSound() {
        return winningSound;
    }

    public MediaPlayer getMusicSound() {
        return musicSound;
    }
}
