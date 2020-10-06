package info.sarna.checkersfx;

class None implements Figure {
  boolean checked = false;

  @Override
  public FigureColor getColor() {
    return FigureColor.NONE;
  }

  @Override
  public boolean getChecked () { return checked; }

  @Override
  public void setChecked (boolean checkedValue) { this.checked = checkedValue; }

  @Override
  public String toString() {
    return "  ";
  }
}
