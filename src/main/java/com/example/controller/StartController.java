package com.example.controller;

import com.example.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartController {

    @FXML private Button level1Button;
    @FXML private Button level2Button;

    @FXML
    private void initialize() {
        level1Button.setOnAction(e -> Main.showGameScreen(1));
        level2Button.setOnAction(e -> Main.showGameScreen(2));
    }
}