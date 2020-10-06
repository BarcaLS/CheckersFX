package info.sarna.checkersfx;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class GUI {
    private final GridPane mainGrid;
    private final GridPane boardGrid;
    private final Label gameText;

    public GridPane getMainGrid() { return mainGrid; }
    public GridPane getBoardGrid() { return boardGrid; }
    public Label getGameText() { return gameText; }

    public GUI(Board board) {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        Image imageback = new Image("file:src/main/resources/background.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(imageback, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setPadding(new Insets(20, 20, 20, 20));
        mainGrid.setHgap(20);
        mainGrid.setVgap(20);
        mainGrid.setBackground(background);

        ColumnConstraints mainGridCol0 = new ColumnConstraints();
        mainGridCol0.setHalignment(HPos.CENTER);
        mainGridCol0.setPrefWidth(400);
        mainGrid.getColumnConstraints().add(mainGridCol0);
        ColumnConstraints mainGridCol1 = new ColumnConstraints();
        mainGridCol1.setHalignment(HPos.CENTER);
        mainGridCol1.setPrefWidth(400);
        mainGrid.getColumnConstraints().add(mainGridCol1);

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        FadeTransition ftBoardGrid = new FadeTransition((Duration.millis(3000)), boardGrid);
        ftBoardGrid.setFromValue(0.0);
        ftBoardGrid.setToValue(1.0);

        GridPane textGrid = new GridPane();
        textGrid.setAlignment(Pos.CENTER);
        textGrid.setPadding(new Insets(20, 20, 20, 20));
        textGrid.setBorder(new Border(new BorderStroke(Color.DARKGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        FadeTransition ftTextGrid = new FadeTransition((Duration.millis(3000)), textGrid);
        ftTextGrid.setFromValue(0.0);
        ftTextGrid.setToValue(1.0);

        ColumnConstraints textGridCol0 = new ColumnConstraints();
        textGridCol0.setHalignment(HPos.CENTER);
        textGrid.getColumnConstraints().add(textGridCol0);

        GridPane aboutGrid = new GridPane();
        aboutGrid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints aboutGridCol0 = new ColumnConstraints();
        aboutGridCol0.setHalignment(HPos.CENTER);
        aboutGridCol0.setPrefWidth(300);
        aboutGrid.getColumnConstraints().add(aboutGridCol0);

        ColumnConstraints aboutGridCol1 = new ColumnConstraints();
        aboutGridCol1.setHalignment(HPos.CENTER);
        aboutGridCol1.setPrefWidth(100);
        aboutGrid.getColumnConstraints().add(aboutGridCol1);

        RowConstraints aboutGridRow = new RowConstraints();
        aboutGridRow.setValignment(VPos.CENTER);
        aboutGridRow.setPrefHeight(50);
        aboutGrid.getRowConstraints().add(aboutGridRow);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER_RIGHT);
        buttonGrid.setHgap(10);

        Text subtitle = new Text("CheckersFX");
        Stop[] stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(1, Color.DARKGRAY)};
        subtitle.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops));
        subtitle.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 40));
        FadeTransition ftSubtitle = new FadeTransition((Duration.millis(3000)), subtitle);
        ftSubtitle.setFromValue(0.0);
        ftSubtitle.setToValue(1.0);

        ParallelTransition p = new ParallelTransition();
        p.getChildren().addAll(ftBoardGrid,ftTextGrid, ftSubtitle);
        p.play();

        final Hyperlink aboutLinkKodilla = new Hyperlink("Kodilla");
        aboutLinkKodilla.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        aboutLinkKodilla.setBorder(Border.EMPTY);
        aboutLinkKodilla.setPadding(new Insets(0));
        aboutLinkKodilla.setOnAction((ActionEvent event) -> {
            try {
                Desktop.getDesktop().browse(new URL("https://kodilla.com").toURI());
                aboutLinkKodilla.setVisited(false);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        final Hyperlink aboutLinkMarcinSarna = new Hyperlink("Marcin Sarna");
        aboutLinkMarcinSarna.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        aboutLinkMarcinSarna.setBorder(Border.EMPTY);
        aboutLinkMarcinSarna.setPadding(new Insets(0));
        aboutLinkMarcinSarna.setOnAction((ActionEvent event) -> {
            try {
                Desktop.getDesktop().browse(new URL("mailto:marcin@sarna.info").toURI());
                aboutLinkMarcinSarna.setVisited(false);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        final Hyperlink aboutLinkAI = new Hyperlink("Mini-Max AI Algorithm");
        aboutLinkAI.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        aboutLinkAI.setBorder(Border.EMPTY);
        aboutLinkAI.setPadding(new Insets(0));
        aboutLinkAI.setOnAction((ActionEvent event) -> {
            try {
                Desktop.getDesktop().browse(new URL("https://towardsdatascience.com/how-a-chess-playing-computer-thinks-about-its-next-move-8f028bd0e7b1").toURI());
                aboutLinkAI.setVisited(false);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        final Text aboutText1 = new Text("Classic checkers game written in JavaFX\nby ");
        aboutText1.setFill(Color.DARKGRAY);
        aboutText1.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        final Text aboutText2 = new Text(" during ");
        aboutText2.setFill(Color.DARKGRAY);
        aboutText2.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        final Text aboutText3 = new Text("'s course.");
        aboutText3.setFill(Color.DARKGRAY);
        aboutText3.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        final Text aboutText4 = new Text("\nUses ");
        aboutText4.setFill(Color.DARKGRAY);
        aboutText4.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        final Text aboutText5 = new Text(".");
        aboutText5.setFill(Color.DARKGRAY);
        aboutText5.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));
        final Text aboutText6 = new Text("\nHitting: back & forward, not obligatory.");
        aboutText6.setFill(Color.DARKGRAY);
        aboutText6.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 14));

        final TextFlow about = new TextFlow(aboutText1, aboutLinkMarcinSarna, aboutText2, aboutLinkKodilla, aboutText3, aboutText4, aboutLinkAI, aboutText5, aboutText6);
        about.setTextAlignment(TextAlignment.CENTER);
        about.setMaxHeight(1);
        about.setPadding(new Insets(5, 0, 0, 0));

        ImageView gitHubLogo = new ImageView(new Image("file:src/main/resources/GitHub_Logo_White.png", 100, 100, true, true));
        final Hyperlink gitHubLink = new Hyperlink();
        gitHubLink.setBorder(Border.EMPTY);
        gitHubLink.setGraphic(gitHubLogo);
        gitHubLink.setOnAction((ActionEvent event) -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/BarcaLS/CheckersFX").toURI());
                gitHubLink.setVisited(false);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        Button restart = new Button();
        restart.setText("Restart");
        restart.setOnAction((e) -> {
            Optional<ButtonType> result = confirm("You're about to restart this game.");
            if (result.get() == ButtonType.OK){
                board.populate();
                board.show(board.getGui().getBoardGrid());
                board.getGui().getGameText().setText("Let's play again.\nYou're still playing with white figures.");
            }
        });

        Button quit = new Button();
        quit.setText("Quit");
        quit.setOnAction((e) -> {
            Optional<ButtonType> result = confirm("You are about to quit.");
            if (result.get() == ButtonType.OK){
                System.exit(0);
            }
        });

        gameText = new Label("");
        gameText.setTextFill(Color.LIGHTGRAY);
        gameText.setWrapText(true);
        gameText.setFont(Font.loadFont("file:src/main/resources/Sansation/Sansation_Bold.ttf", 20));

        mainGrid.add(subtitle,0, 0, 2,1);
        mainGrid.add(aboutGrid,0, 1, 1, 1);
        mainGrid.add(buttonGrid,1, 1, 1, 1);
        mainGrid.add(boardGrid,0, 2, 1, 1);
        mainGrid.add(textGrid,1, 2, 1,1);
        aboutGrid.add(about,0,0,1,1);
        aboutGrid.add(gitHubLink,1,0,1,1);
        buttonGrid.add(restart,0,0,1,1);
        buttonGrid.add(quit, 1,0,1,1);
        textGrid.add(gameText, 0, 1,1,1);
    }

    private Optional<ButtonType> confirm(String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("I'd like to ask you something...");
        alert.setHeaderText(headerText);
        alert.setContentText("Are you sure?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/field_queen_white.jpg"));

        return alert.showAndWait();
    }

    private Optional<ButtonType> confirmOnlyRestart() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game over");
        alert.setHeaderText(null);
        alert.setContentText("Let's play again.");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/field_queen_white.jpg"));

        return alert.showAndWait();
    }

    public void gameOver(Board board) {
        Optional<ButtonType> result = confirmOnlyRestart();
        if (result.get() == ButtonType.OK){
            board.populate();
            board.show(board.getGui().getBoardGrid());
            board.nextMove = FigureColor.WHITE;
            board.getGui().getGameText().setText("Let's play again.\nYou're still playing with white figures.");
        }
    }
}
