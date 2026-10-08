package com.example.controller;

import com.example.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoseController {

    @FXML private Label titleLabel;
    @FXML private Button retryButton;
    @FXML private Button menuButton;

    private int level;

    public void setLevel(int level) {
        this.level = level;
        titleLabel.setText("LEVEL " + level + " FAILED");
        retryButton.setOnAction(e -> Main.showGameScreen(level));
        menuButton.setOnAction(e -> Main.showStartScreen());
    }
}