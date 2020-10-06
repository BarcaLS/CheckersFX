package info.sarna.checkersfx;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

class Board {
  GUI gui = new GUI(this);
  AI computerAI = new AI();
  private List<BoardRow> rows = new ArrayList<>();
  public FigureColor nextMove = FigureColor.WHITE;
  private int oldX = -1;
  private int oldY = -1;

  public GUI getGui() { return gui; }
  public AI getComputerAI() { return computerAI; }
  public int getOldX() { return oldX; }
  public int getOldY() { return oldY; }
  public List<BoardRow> getRows() { return rows; }
  public void setOldX(int oldX) { this.oldX = oldX; }
  public void setOldY(int oldY) { this.oldY = oldY; }
  public void setRows(List<BoardRow> rows) { this.rows = rows; }

  Board() {
    for (int n = 0; n < 8; n++)
      rows.add(new BoardRow());
  }

  public Figure getFigure(int col, int row) {
    return rows.get(row).getCols().get(col);
  }

  public void setFigure(int col, int row, Figure figure) {
    rows.get(row).getCols().set(col, figure);
  }

  public void populate() {
    for (int col=0; col<8; col=col+1) {
      for (int row=0; row<8; row=row+1) {
        this.setFigure(col, row, new None());
      }
    }
    for (int col=1; col<8; col=col+2) {
      this.setFigure(col, 0, new Pawn(FigureColor.BLACK));
      this.setFigure(col, 2, new Pawn(FigureColor.BLACK));
      this.setFigure(col, 6, new Pawn(FigureColor.WHITE));
    }
    for (int col=0; col<8; col=col+2) {
      this.setFigure(col, 1, new Pawn(FigureColor.BLACK));
      this.setFigure(col, 7, new Pawn(FigureColor.WHITE));
      this.setFigure(col, 5, new Pawn(FigureColor.WHITE));
    }
  }

  public boolean moveFigure(int currentCol, int currentRow, int targetCol, int targetRow, Board board) {

    // common for every figure
    if (checkOutOfBorders(currentCol, currentRow, targetCol, targetRow, board.getGui().getGameText())) return false;
    if (checkWrongColor(currentCol, currentRow, board.getGui().getGameText())) return false;
    if (checkNoMove(currentCol, currentRow, targetCol, targetRow, board.getGui().getGameText())) return false;
    if (checkTargetOccupied(targetCol, targetRow, board.getGui().getGameText())) return false;

    // we're moving pawn
    if(getFigure(currentCol,currentRow) instanceof Pawn) {
      if(!tryMovePawnWithHit(currentCol, currentRow, targetCol, targetRow, board.getGui().getGameText())) {
        if (isMovePawnWithoutHitNotPossible(currentCol, currentRow, targetCol, targetRow, board.getGui().getGameText()))
          return false;
        else
          doMove(currentCol, currentRow, targetCol, targetRow, board);
      }
      CheckBecomeQueen(targetCol, targetRow);
    }

    // we're moving queen
    if(getFigure(currentCol,currentRow) instanceof Queen) {
      if(isMoveQueenNotDiagonal(currentCol, currentRow, targetCol, targetRow, board.getGui().getGameText()))
        return false;
      if (!moveQueen(currentCol, currentRow, targetCol, targetRow, board))
        return false;
    }

    switchPlayer();
    return true;
  }

  private boolean isMoveQueenNotDiagonal(int currentCol, int currentRow, int targetCol, int targetRow, Label gameText) {
    if(!(currentCol - targetCol == currentRow - targetRow || currentCol - targetCol == targetRow - currentRow ||
            targetCol - currentCol == currentRow - targetRow || targetCol - currentCol == targetRow - currentRow)) {
      gameText.setText("Move is not diagonal.");
      return true;
    }
    return false;
  }

  private boolean moveQueen(int currentCol, int currentRow, int targetCol, int targetRow, Board board) {
    int checkedCol = currentCol;
    int checkedRow = currentRow;
    int lastCheckedCol;
    int lastCheckedRow;
    boolean checkFieldAfterOpponentFigure = false;

    boolean horizontalIncrease = isQueenMoveIncrease(currentCol - targetCol);
    boolean verticalIncrease = isQueenMoveIncrease(currentRow - targetRow);

    while(checkedCol != targetCol) {
      lastCheckedCol = checkedCol;
      lastCheckedRow = checkedRow;
      checkedCol = moveQueenNextIteration(checkedCol, horizontalIncrease);
      checkedRow = moveQueenNextIteration(checkedRow, verticalIncrease);

      if (isQueenMoveBlockedByOurFigure(checkedCol, checkedRow, board.getGui().getGameText())) return false;
      if(getFigure(checkedCol,checkedRow).getColor() != FigureColor.NONE) {
        if (checkFieldAfterOpponentFigure) {
          board.getGui().getGameText().setText("There are two figures one after another on your way.");
          return false;
        }
        checkFieldAfterOpponentFigure = true;
        continue;
      }
      if(checkFieldAfterOpponentFigure) { // we are on empty field and after opponent's figure so move with hit
        setFigure(lastCheckedCol, lastCheckedRow, new None());
        setFigure(checkedCol, checkedRow, new Queen(getFigure(currentCol, currentRow).getColor()));
        setFigure(currentCol, currentRow, new None());
        return true;
      }
    }
    doMove(currentCol,currentRow,targetCol,targetRow, board);
    return true;
  }

  private boolean isQueenMoveBlockedByOurFigure(int checkedCol, int checkedRow, Label gameText) {
    if(getFigure(checkedCol,checkedRow).getColor() == nextMove) {
      gameText.setText("Another of your figures is on the way.");
      return true;
    }
    return false;
  }

  private int moveQueenNextIteration(int checked, boolean increase) {
    if (increase)
      checked++;
    else
      checked--;
    return checked;
  }

  private boolean isQueenMoveIncrease(int difference) {
    return difference <= 0;
  }

  private void CheckBecomeQueen(int targetCol, int targetRow) {
    if(targetRow == 0)
      setFigure(targetCol, targetRow, new Queen(getFigure(targetCol, targetRow).getColor()));
  }

  public boolean checkGameOver(FigureColor colorToCheck) {
    for (int a = 0; a < 8; a++) {
      for (int b = 0; b < 8; b++) {
        if (rows.get(a).getCols().get(b).getColor() == colorToCheck)
          return false;
      }
    }
    return true;
  }

  private void switchPlayer() {
    if(nextMove == FigureColor.WHITE)
      nextMove = FigureColor.BLACK;
    else
      nextMove = FigureColor.WHITE;
  }

  private void doMove(int currentCol, int currentRow, int targetCol, int targetRow, Board board) {
    setFigure(targetCol,targetRow,getFigure(currentCol,currentRow));
    setFigure(currentCol,currentRow,new None());
    board.getGui().getGameText().setText("Move done.");
  }

  private boolean isMovePawnWithoutHitNotPossible(int currentCol, int currentRow, int targetCol, int targetRow, Label gameText) {
    if(!(currentCol + 1 == targetCol || currentCol - 1 == targetCol)) {
      gameText.setText("Move is not diagonal\nor more than one field away.");
      return true;
    }
    return checkWrongDirection(currentCol, currentRow, targetRow, currentRow - 1, gameText);
  }

  private boolean checkWrongDirection(int currentCol, int currentRow, int targetRow, int rowToTry, Label gameText) {
    if (getFigure(currentCol, currentRow).getColor() == FigureColor.WHITE && rowToTry != targetRow) {
      gameText.setText("White pawn can move only up.");
      return true;
    }
    return false;
  }

  private boolean tryMovePawnWithHit(int currentCol, int currentRow, int targetCol, int targetRow, Label gameText) {
    boolean result = tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol + 2, currentRow - 2, currentCol + 1, currentRow - 1);
    result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol+2, currentRow+2, currentCol+1, currentRow+1);
    result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol-2, currentRow+2, currentCol-1, currentRow+1);
    result = result || tryToMovePawnWithHitInDirection(currentCol, currentRow, targetCol, targetRow, currentCol-2, currentRow-2, currentCol-1, currentRow-1);
    if(result)
      gameText.setText("Hit done.");
    return result;
  }

  private boolean tryToMovePawnWithHitInDirection(int currentCol, int currentRow, int targetCol, int targetRow, int possibleCol, int possibleRow, int colToHit, int rowToHit) {
    if (possibleCol == targetCol && possibleRow == targetRow &&
            getFigure(colToHit, rowToHit).getColor() != FigureColor.NONE &&
            getFigure(colToHit, rowToHit).getColor() != getFigure(currentCol, currentRow).getColor()) {
      doMovePawnWithHit(currentCol, currentRow, targetCol, targetRow, colToHit, rowToHit);
      return true;
    }
    return false;
  }

  private void doMovePawnWithHit(int currentCol, int currentRow, int targetCol, int targetRow, int toRemoveCol, int toRemoveRow) {
    setFigure(toRemoveCol, toRemoveRow, new None());
    setFigure(targetCol, targetRow, new Pawn(getFigure(currentCol, currentRow).getColor()));
    setFigure(currentCol, currentRow, new None());
  }

  private boolean checkTargetOccupied(int targetCol, int targetRow, Label gameText) {
    if(getFigure(targetCol,targetRow).getColor() != FigureColor.NONE) {
      gameText.setText("Target field is occupied by figure.");
      return true;
    }
    return false;
  }

  private boolean checkNoMove(int currentCol, int currentRow, int targetCol, int targetRow, Label gameText) {
    if(currentCol == targetCol && currentRow == targetRow) {
      gameText.setText("Current field and target field are the same.");
      return true;
    }
    return false;
  }

  private boolean checkWrongColor(int currentCol, int currentRow, Label gameText) {
    if(getFigure(currentCol,currentRow).getColor() == FigureColor.BLACK) {
      gameText.setText("You can't move opponent's figure.");
      return true;
    }
    else if(getFigure(currentCol,currentRow).getColor() == FigureColor.NONE) {
      gameText.setText("There is no figure.");
      return true;
    }
    return false;
  }

  private boolean checkOutOfBorders(int currentCol, int currentRow, int targetCol, int targetRow, Label gameText) {
    if(currentCol < 0 || currentCol > 8 || currentRow < 0 || currentRow > 8 || targetCol < 0 || targetCol > 8 || targetRow < 0 || targetRow > 8) {
      gameText.setText("There's no such field.");
      return true;
    }
    return false;
  }

  public void show(GridPane boardGrid) {
    List<Figure> currentRow;
    String file = "";
    int x;
    boolean emptyBlack;
    Rectangle redRectangle = new Rectangle(50,50);
    redRectangle.setFill(Paint.valueOf("RED"));
    redRectangle.setOpacity(0.4);

    for (int y = 0; y < 8; y++) {
      currentRow = rows.get(y).getCols();
      x = 0;
      for (Figure currentFigure : currentRow) {
        if (currentFigure instanceof None) {
          emptyBlack = ((y==0) && ((x==0) || (x==2) || (x==4) || (x==6)));
          emptyBlack = emptyBlack || ((y==1) && ((x==1) || (x==3) || (x==5) || (x==7)));
          emptyBlack = emptyBlack || ((y==2) && ((x==0) || (x==2) || (x==4) || (x==6)));
          emptyBlack = emptyBlack || ((y==3) && ((x==1) || (x==3) || (x==5) || (x==7)));
          emptyBlack = emptyBlack || ((y==4) && ((x==0) || (x==2) || (x==4) || (x==6)));
          emptyBlack = emptyBlack || ((y==5) && ((x==1) || (x==3) || (x==5) || (x==7)));
          emptyBlack = emptyBlack || ((y==6) && ((x==0) || (x==2) || (x==4) || (x==6)));
          emptyBlack = emptyBlack || ((y==7) && ((x==1) || (x==3) || (x==5) || (x==7)));
          if(emptyBlack)
            file = "file:src/main/resources/field_black.jpg";
          else
            file = "file:src/main/resources/field_empty.jpg";
        }
        if (currentFigure instanceof Pawn) {
          if (currentFigure.getColor() == FigureColor.WHITE) {
            file = "file:src/main/resources/field_pawn_white.jpg";
          } else {
            file = "file:src/main/resources/field_pawn_black.jpg";
          }
        }
        if (currentFigure instanceof Queen) {
          if (currentFigure.getColor() == FigureColor.WHITE) {
            file = "file:src/main/resources/field_queen_white.jpg";
          } else {
            file = "file:src/main/resources/field_queen_black.jpg";
          }
        }
        if(currentFigure.getChecked()) {
          boardGrid.add(redRectangle, x, y, 1, 1);
        } else {
          boardGrid.add(new ImageView(new Image(file, 50, 50, true, true)), x, y, 1, 1);
        }
        x++;
      }
    }
  }
}
