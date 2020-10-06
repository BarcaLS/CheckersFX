package info.sarna.checkersfx;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AI {
    private static int numberOfCombinations;

    public AI() {
    }

    public static int getNumberOfCombinations() { return numberOfCombinations; }

    public List<BoardRow> getBestBoardRow(List<BoardRow> inputRows, Board board) {
        List<Move> allPossibleMovesLevel1;
        List<Move> allPossibleMovesLevel2;
        List<Move> allPossibleMovesLevel3;
        List<BoardRow> hypotheticRow1;
        List<BoardRow> hypotheticRow2;
        List<BoardRow> hypotheticRow3;
        List<List<BoardRow>> hypotheticRowsLevel1 = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsLevel2 = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsLevel1WithWhiteMove;
        List<List<BoardRow>> hypotheticRowsLevel1WithoutWhiteMove = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsLevel2WithWhiteMove;
        List<List<BoardRow>> hypotheticRowsLevel3WithWhiteMove;
        Map<Integer,Integer> hypotheticRowsLevel1WithoutWhiteMoveMap = new HashMap<>();
        Map<Integer,Integer> hypotheticRowsLevel2Map = new HashMap<>();
        Map<Integer,Integer> hypotheticRowsLevel3Map = new HashMap<>();
        int scoreHypotheticRow;
        List<Integer> scoreHypotethicRows = new ArrayList<>();
        int IndexOfBestMoveAtThirdLevel;
        int counter1 = 0;
        int counter2 = 0;
        numberOfCombinations = 0;

        allPossibleMovesLevel1 = listAllPossibleMoves(copyListofBoardRow(inputRows), FigureColor.BLACK);
        for (Move currentMove : allPossibleMovesLevel1) {
            hypotheticRow1 = doMoveAndBuildHypotheticRows(currentMove, copyListofBoardRow(inputRows));
            hypotheticRowsLevel1WithoutWhiteMove.add(hypotheticRow1);
            hypotheticRowsLevel1WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticRow1);
            for (List<BoardRow> currentBoardRow : hypotheticRowsLevel1WithWhiteMove) {
                hypotheticRowsLevel1.add(currentBoardRow);
                hypotheticRowsLevel1WithoutWhiteMoveMap.put(counter1,counter2);
                counter1++;
                numberOfCombinations++;
            }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow1 : hypotheticRowsLevel1) {
            allPossibleMovesLevel2 = listAllPossibleMoves(copyListofBoardRow(currentBoardRow1), FigureColor.BLACK);
            for (Move currentMove : allPossibleMovesLevel2) {
                hypotheticRow2 = doMoveAndBuildHypotheticRows(currentMove, copyListofBoardRow(currentBoardRow1));
                hypotheticRowsLevel2WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticRow2);
                for (List<BoardRow> currentBoardRow2 : hypotheticRowsLevel2WithWhiteMove) {
                    hypotheticRowsLevel2.add(currentBoardRow2);
                    hypotheticRowsLevel2Map.put(counter1,counter2);
                    counter1++;
                    numberOfCombinations++;
                }
            }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow : hypotheticRowsLevel2) {
            allPossibleMovesLevel3 = listAllPossibleMoves(copyListofBoardRow(currentBoardRow), FigureColor.BLACK);
            for (Move currentMove : allPossibleMovesLevel3) {
                hypotheticRow3 = doMoveAndBuildHypotheticRows(currentMove, copyListofBoardRow(currentBoardRow));
                hypotheticRowsLevel3WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticRow3);
                for (List<BoardRow> currentBoardRow2 : hypotheticRowsLevel3WithWhiteMove) {
                    hypotheticRowsLevel3Map.put(counter1,counter2);
                    scoreHypotheticRow = scoreHypotheticRows(currentBoardRow2);
                    scoreHypotethicRows.add(scoreHypotheticRow);
                    counter1++;
                    numberOfCombinations++;
                }
            }
            counter2++;
        }

        if (scoreHypotethicRows.size() == 0) { // computer can't move
            board.getGui().getGameText().setText("Computer can't move.\nGame ended.");
            board.getGui().gameOver(board);
        }

        IndexOfBestMoveAtThirdLevel = chooseIndexOfBestMoveAtThirdLevel(scoreHypotethicRows);
        counter1 = hypotheticRowsLevel3Map.get(IndexOfBestMoveAtThirdLevel);
        counter2 = hypotheticRowsLevel2Map.get(counter1);
        counter1 = hypotheticRowsLevel1WithoutWhiteMoveMap.get(counter2);

        return hypotheticRowsLevel1WithoutWhiteMove.get(counter1);
    }

    private List<Move> listAllPossibleMoves(List<BoardRow> rows, FigureColor colorToCheck) {
        List<Move> allPossibleMoves = new ArrayList<>();
        List<Figure> currentRow;
        int x;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getCols();
            x = 0;
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    x++;
                    continue;
                }
                if (currentFigure.getColor() != colorToCheck) {
                    x++;
                    continue;
                }
                allPossibleMoves.addAll(listAllPossibleMovesForFigure(rows, x, y, currentFigure));
                x++;
            }
        }
        return allPossibleMoves;
    }

    private List<Move> listAllPossibleMovesForFigure(List<BoardRow> rows, int currentCol, int currentRow, Figure currentFigure) {
        List<Move> allPossibleMovesForFigure = new ArrayList<>();

        for (int targetRow = 0; targetRow < 8; targetRow++) {
            for (int targetCol = 0; targetCol < 8; targetCol++) {
                if (moveFigure(currentCol, currentRow, targetCol, targetRow, rows, currentFigure.getColor()))
                    allPossibleMovesForFigure.add(new Move(currentCol, currentRow, targetCol, targetRow));
            }
        }
        return allPossibleMovesForFigure;
    }

    private boolean moveFigure(int currentCol, int currentRow, int targetCol, int targetRow, List<BoardRow> rows, FigureColor colorToCheck) {

        // common for every figure
        if (checkNoMove(currentCol, currentRow, targetCol, targetRow)) return false;
        if (checkTargetOccupied(targetCol, targetRow, rows)) return false;

        // we're moving pawn
        if (getFigure(currentCol, currentRow, rows) instanceof Pawn) {
            if (!tryMovePawnWithHit(currentCol, currentRow, targetCol, targetRow, rows, false)) {
                return !isMovePawnWithoutHitNotPossible(currentCol, currentRow, targetCol, targetRow, rows, colorToCheck);
            }
        }

        // we're moving queen
        if (getFigure(currentCol, currentRow, rows) instanceof Queen) {
            if (isMoveQueenNotDiagonal(currentCol, currentRow, targetCol, targetRow))
                return false;
            return moveQueen(currentCol, currentRow, targetCol, targetRow, rows, false);
        }

        return true;
    }

    private Figure getFigure(int targetCol, int targetRow, List<BoardRow> rows) {
        return rows.get(targetRow).getCols().get(targetCol);
    }

    private void setFigure(int targetCol, int targetRow, Figure figure, List<BoardRow> rows) {
        rows.get(targetRow).getCols().set(targetCol, figure);
    }

    private boolean checkNoMove(int currentCol, int currentRow, int targetCol, int targetRow) {
        return currentCol == targetCol && currentRow == targetRow;
    }

    private boolean checkTargetOccupied(int targetCol, int targetRow, List<BoardRow> rows) {
        return getFigure(targetCol, targetRow, rows).getColor() != FigureColor.NONE;
    }

    private boolean tryMovePawnWithHit(int currentCol, int currentRow, int targetCol, int targetRow, List<BoardRow> rows, boolean doHit) {
        boolean result = tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol + 2, currentRow - 2, currentCol + 1, currentRow - 1, rows, doHit);
        result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol + 2, currentRow + 2, currentCol + 1, currentRow + 1, rows, doHit);
        result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol - 2, currentRow + 2, currentCol - 1, currentRow + 1, rows, doHit);
        result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol - 2, currentRow - 2, currentCol - 1, currentRow - 1, rows, doHit);
        return result;
    }

    private boolean tryToMovePawnWithHitInDirection(int currentCol, int currentRow, int targetCol, int targetRow, int possibleCol, int possibleRow, int colToHit, int rowToHit, List<BoardRow> rows, boolean doHit) {
        if (possibleCol == targetCol && possibleRow == targetRow &&
                getFigure(colToHit, rowToHit, rows).getColor() != FigureColor.NONE &&
                getFigure(colToHit, rowToHit, rows).getColor() != getFigure(currentCol, currentRow, rows).getColor()) {
            if (doHit) {
                doMovePawnWithHit(currentCol, currentRow, targetCol, targetRow, colToHit, rowToHit, rows);
            }
            return true;
        }
        return false;
    }

    private void doMovePawnWithHit(int currentCol, int currentRow, int targetCol, int targetRow, int toRemoveCol, int toRemoveRow, List<BoardRow> rows) {
        setFigure(toRemoveCol, toRemoveRow, new None(), rows);
        setFigure(targetCol, targetRow, new Pawn(getFigure(currentCol, currentRow, rows).getColor()), rows);
        setFigure(currentCol, currentRow, new None(), rows);
    }

    private boolean isMovePawnWithoutHitNotPossible(int currentCol, int currentRow, int targetCol, int targetRow, List<BoardRow> rows, FigureColor colorToCheck) {
        if (!(currentCol + 1 == targetCol || currentCol - 1 == targetCol)) {
            return true;
        }
        if(colorToCheck == FigureColor.BLACK) {
            return checkWrongDirection(currentCol, currentRow, targetRow, FigureColor.BLACK, currentRow + 1, rows);
        } else {
            return checkWrongDirection(currentCol, currentRow, targetRow, FigureColor.WHITE, currentRow - 1, rows);
        }
    }

    private boolean checkWrongDirection(int currentCol, int currentRow, int targetRow, FigureColor currentColor, int rowToTry, List<BoardRow> rows) {
        return getFigure(currentCol, currentRow, rows).getColor() == currentColor && rowToTry != targetRow;
    }

    private boolean isMoveQueenNotDiagonal(int currentCol, int currentRow, int targetCol, int targetRow) {
        return !(currentCol - targetCol == currentRow - targetRow || currentCol - targetCol == targetRow - currentRow ||
                targetCol - currentCol == currentRow - targetRow || targetCol - currentCol == targetRow - currentRow);
    }

    private boolean moveQueen(int currentCol, int currentRow, int targetCol, int targetRow, List<BoardRow> rows, boolean doHit) {
        int checkedCol = currentCol;
        int checkedRow = currentRow;
        int lastCheckedCol;
        int lastCheckedRow;
        boolean checkFieldAfterOpponentFigure = false;

        boolean horizontalIncrease = isQueenMoveIncrease(currentCol - targetCol);
        boolean verticalIncrease = isQueenMoveIncrease(currentRow - targetRow);

        while (checkedCol != targetCol) {
            lastCheckedCol = checkedCol;
            lastCheckedRow = checkedRow;
            checkedCol = moveQueenNextIteration(checkedCol, horizontalIncrease);
            checkedRow = moveQueenNextIteration(checkedRow, verticalIncrease);

            if (isQueenMoveBlockedByOurFigure(checkedCol, checkedRow, rows)) return false;
            if (getFigure(checkedCol, checkedRow, rows).getColor() != FigureColor.NONE) {
                if (checkFieldAfterOpponentFigure)
                    return false;
                checkFieldAfterOpponentFigure = true;
                continue;
            }
            if (checkFieldAfterOpponentFigure) {
                if (doHit) {
                    setFigure(lastCheckedCol, lastCheckedRow, new None(), rows);
                    setFigure(checkedCol, checkedRow, new Queen(getFigure(currentCol, currentRow, rows).getColor()), rows);
                    setFigure(currentCol, currentRow, new None(), rows);
                }
                return true;
            }
        }
        if (doHit)
            doMove(currentCol, currentRow, targetCol, targetRow, rows);
        return true;
    }

    private boolean isQueenMoveIncrease(int difference) {
        return difference <= 0;
    }

    private void CheckBecomeQueen(int targetCol, int targetRow, List<BoardRow> rows) {
        if (targetRow == 7)
            setFigure(targetCol, targetRow, new Queen(getFigure(targetCol, targetRow, rows).getColor()), rows);
    }

    private int moveQueenNextIteration(int checked, boolean increase) {
        if (increase)
            checked++;
        else
            checked--;
        return checked;
    }

    private boolean isQueenMoveBlockedByOurFigure(int checkedCol, int checkedRow, List<BoardRow> rows) {
        return getFigure(checkedCol, checkedRow, rows).getColor() == FigureColor.BLACK;
    }

    private List<BoardRow> doMoveAndBuildHypotheticRows(Move currentMove, List<BoardRow> rows) {
        if (getFigure(currentMove.getCurrentCol(), currentMove.getCurrentRow(), rows) instanceof Pawn) {
            if (!tryMovePawnWithHit(currentMove.getCurrentCol(), currentMove.getCurrentRow(), currentMove.getTargetCol(), currentMove.getTargetRow(), rows, true)) {
                doMove(currentMove.getCurrentCol(), currentMove.getCurrentRow(), currentMove.getTargetCol(), currentMove.getTargetRow(), rows);
            }
            CheckBecomeQueen(currentMove.getTargetCol(), currentMove.getTargetRow(), rows);
        }
        if (getFigure(currentMove.getCurrentCol(), currentMove.getCurrentRow(), rows) instanceof Queen) {
            moveQueen(currentMove.getCurrentCol(), currentMove.getCurrentRow(), currentMove.getTargetCol(), currentMove.getTargetRow(), rows, true);
        }
        return rows;
    }

    private void doMove(int currentCol, int currentRow, int targetCol, int targetRow, List<BoardRow> rows) {
        setFigure(targetCol, targetRow, getFigure(currentCol, currentRow, rows), rows);
        setFigure(currentCol, currentRow, new None(), rows);
    }

    private List<List<BoardRow>> moveWhiteFiguresAndBuildBoard(List<BoardRow> inputBoard) {
        List<List<BoardRow>> ListOfBoardsWithWhiteMove = new ArrayList<>();
        List<Move> allPossibleMovesWhite;
        List<BoardRow> hypotheticRowWhite;

        allPossibleMovesWhite = listAllPossibleMoves(copyListofBoardRow(inputBoard), FigureColor.WHITE);
        for (Move currentMove : allPossibleMovesWhite) {
            hypotheticRowWhite = doMoveAndBuildHypotheticRows(currentMove, copyListofBoardRow(inputBoard));
            ListOfBoardsWithWhiteMove.add(hypotheticRowWhite);
        }
        return ListOfBoardsWithWhiteMove;
    }

    private int scoreHypotheticRows(List<BoardRow> rows) {
        List<Figure> currentRow;
        int queenValue = 3;
        int score;
        int scoreComputer = 0;
        int scoreHuman = 0;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getCols();
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    continue;
                }
                if (currentFigure.getColor() == FigureColor.WHITE) {
                    if (currentFigure instanceof Queen)
                        scoreHuman = scoreHuman + queenValue;
                    else
                        scoreHuman++;
                } else {
                    if (currentFigure instanceof Queen)
                        scoreComputer = scoreComputer + queenValue;
                    else
                        scoreComputer++;
                }
            }
        }
        score = scoreComputer - scoreHuman;
        return score;
    }

    private int chooseIndexOfBestMoveAtThirdLevel(List<Integer> scoreHypotethicRows) {
        int bestBoardRow = 0;
        boolean pickRandom = true;
        int random;

        while (pickRandom) {
            random = ThreadLocalRandom.current().nextInt(0, scoreHypotethicRows.size());
            if (scoreHypotethicRows.get(random).equals(Collections.max(scoreHypotethicRows))) {
                bestBoardRow = random;
                pickRandom = false;
            }
        }
        return bestBoardRow;
    }

    private static List<BoardRow> copyListofBoardRow(List<BoardRow> inputList) {
        List<BoardRow> outputList = new ArrayList<>();
        for (int counter = 0; counter < 8; counter++)
            outputList.add(new BoardRow());
        for (int counter1 = 0; counter1 < 8; counter1++)
            for (int counter2 = 0; counter2 < 8; counter2++) {
                Figure figure = createCopy(inputList.get(counter1).getCols().get(counter2));
                outputList.get(counter1).setFigure(counter2, figure);
            }
        return outputList;
    }

    private static Figure createCopy(Figure figure) {
        if(figure instanceof Pawn)
            return new Pawn(figure.getColor());
        else if (figure instanceof Queen)
            return new Queen(figure.getColor());
        else
            return new None();
    }
}
