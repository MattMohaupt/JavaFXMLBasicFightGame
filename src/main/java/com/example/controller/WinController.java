package com.example.controller;

import com.example.Main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WinController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Button nextLevelButton;
    @FXML private Button replayButton;

    private int level;

    public void setLevels(int level, int nextLevel) {
        this.level = level;

        if (level >= 3) {
            // Final level — you beat the game
            titleLabel.setText("YOU BEAT THE GAME!");
            subtitleLabel.setText("Thanks for playing.");
            nextLevelButton.setVisible(false);
            nextLevelButton.setManaged(false);   // removes its layout space too
        } else {
            // Still more levels to go
            titleLabel.setText("LEVEL " + level + " COMPLETE!");
            subtitleLabel.setText("Ready for Level " + nextLevel + "?");
            nextLevelButton.setOnAction(e -> Main.showGameScreen(nextLevel));
        }

        // Replay always restarts from level 1
        replayButton.setText("Play Again");
        replayButton.setOnAction(e -> Main.showGameScreen(1));
    }
}