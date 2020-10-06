package info.sarna.checkersfx;

class Queen implements Figure {
  boolean checked = false;

  private final FigureColor color;

  Queen(FigureColor color) {
    this.color = color;
  }

  @Override
  public FigureColor getColor() {
    return color;
  }

  @Override
  public boolean getChecked () { return checked; }

  @Override
  public void setChecked (boolean checkedValue) { this.checked = checkedValue; }

  @Override
  public String toString() {
    return getColorSymbol() + "Q";
  }

  private String getColorSymbol() {
    return (color == FigureColor.WHITE) ? "w" : "b";
  }
}
