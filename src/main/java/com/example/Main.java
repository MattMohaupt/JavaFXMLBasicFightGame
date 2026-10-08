package com.example;

import java.io.IOException;

import com.example.controller.GameController;
import com.example.controller.LoseController;
import com.example.controller.WinController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Basic JavaFX Game");
        primaryStage.setResizable(false);
        showStartScreen();
        primaryStage.show();
    }

    public static void showStartScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/start.fxml"));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
            primaryStage.setScene(scene);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void showGameScreen(int levelNumber) {
        // Safety clamp: never allow a level above 3
        if (levelNumber > 3) levelNumber = 3;
        if (levelNumber < 1) levelNumber = 1;

        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/game.fxml"));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
            GameController controller = loader.getController();
            controller.startLevel(levelNumber);
            primaryStage.setScene(scene);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void showWinScreen(int levelNumber, int nextLevel) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/win.fxml"));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
            WinController controller = loader.getController();
            controller.setLevels(levelNumber, nextLevel);
            primaryStage.setScene(scene);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void showLoseScreen(int levelNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/lose.fxml"));
            Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
            LoseController controller = loader.getController();
            controller.setLevel(levelNumber);
            primaryStage.setScene(scene);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}