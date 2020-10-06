package info.sarna.checkersfx;

public class Move {
    private final int currentCol;
    private final int currentRow;
    private final int targetCol;
    private final int targetRow;

    public Move(int currentCol, int currentRow, int targetCol, int targetRow) {
        this.currentCol = currentCol;
        this.currentRow = currentRow;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public int getTargetRow() {
        return targetRow;
    }
}
