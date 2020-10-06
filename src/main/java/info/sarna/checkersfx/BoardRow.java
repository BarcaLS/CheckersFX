package info.sarna.checkersfx;

import java.util.ArrayList;
import java.util.List;

class BoardRow {
  private final List<Figure> cols = new ArrayList<>();

  BoardRow() {
    for (int n = 0; n < 8; n++)
      cols.add(new None());
  }

  List<Figure> getCols() {
    return cols;
  }

  void setFigure(int column, Figure figure) { this.cols.set(column, figure); }
}
