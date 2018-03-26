package a2u.tn.utils.json;

/**
 * Exception on parse error
 */
public class ParseException extends RuntimeException {

  private int position;


  public ParseException(String s, int position) {
    super(s);
    this.position = position;
  }

  /**
   * Position in jsp where occur error
   * @return position of invalid symbol
   */
  public int getPosition() {
    return position;
  }

}
