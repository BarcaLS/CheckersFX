package info.sarna.checkersfx;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class App extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Board board = new Board();

        board.populate();

        primaryStage.setScene(new Scene(board.getGui().getMainGrid(), 1024, 618, Color.WHITE));
        primaryStage.setTitle("CheckersFX");
        primaryStage.getIcons().add(new Image("file:src/main/resources/field_queen_white.jpg"));
        primaryStage.setResizable(false);
        primaryStage.show();

        board.show(board.getGui().getBoardGrid());
        board.getGui().getGameText().setText("Hello, let's play.\nYou're playing with white figures.");
        doMouseCheck(board);
    }

    private static void doMouseCheck(Board board) {
        board.getGui().getBoardGrid().setOnMouseClicked(event -> {
            int x = (int) (event.getX() / 50);
            int y = (int) (event.getY() / 50);
            doClick(x, y, board);
        });
    }

    private static void doClick(int x, int y, Board board) {
        if(board.getOldX() == -1) {
            board.setOldX(x);
            board.setOldY(y);
            markField(board, board.getOldX(), board.getOldY());
        } else {
            int oldX = board.getOldX();
            int oldY = board.getOldY();
            board.setOldX(-1);
            board.setOldY(-1);
            unmarkFields(board);
            if(board.moveFigure(oldX, oldY, x, y, board)) {
                board.show(board.getGui().getBoardGrid());
                doNextMove(board);
            } else {
                board.show(board.getGui().getBoardGrid());
                doMouseCheck(board);
            }
        }
    }

    public static void markField(Board board, int x, int y) {
        Figure currentFigure = board.getFigure(x, y);
        currentFigure.setChecked(true);
        board.show(board.getGui().getBoardGrid());
    }

    public static void unmarkFields(Board board) {
        List<Figure> currentRow;

        for (int n = 0; n < 8; n++) {
            currentRow = board.getRows().get(n).getCols();
            for (Figure currentFigure : currentRow) {
                currentFigure.setChecked(false);
            }
        }
        board.show(board.getGui().getBoardGrid());
    }

    public static void doNextMove(Board board) {
        if(board.checkGameOver(board.nextMove))
            gameOver(board);
        board.getGui().getGameText().setText(board.getGui().getGameText().getText() + "\nNext move: " + board.nextMove.toString());
        if(board.nextMove == FigureColor.WHITE)
            doMouseCheck(board);
        else
            ComputerMove(board);
    }

    private static void ComputerMove(Board board) {
        board.getGui().getBoardGrid().setOnMouseClicked(null); // human won't be possible to do moves during computer's move
        board.getGui().getGameText().setText("Next move: " + board.nextMove.toString() + "\nComputer is thinking...\nThis may take several minutes\nbecause I'm doing a lot of calculation\n(3 nodes to check).");
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            List<BoardRow> bestBoardRow = board.getComputerAI().getBestBoardRow(board.getRows(), board);
            board.setRows(bestBoardRow);
            board.show(board.getGui().getBoardGrid());
            board.nextMove = FigureColor.WHITE;
            if(board.checkGameOver(board.nextMove))
                gameOver(board);
            board.getGui().getGameText().setText(AI.getNumberOfCombinations() + " combinations analysed.\nComputer moved.\nNext move: " + board.nextMove.toString());
            doMouseCheck(board);
        });
        new Thread(sleeper).start();
    }

    private static void gameOver (Board board) {
        if(board.nextMove == FigureColor.WHITE)
            board.getGui().getGameText().setText("Black won!");
        else
            board.getGui().getGameText().setText("White won!");
        board.getGui().gameOver(board);
    }
}
