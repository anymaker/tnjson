package a2u.tn.utils.json;

public class ParseException extends RuntimeException {

  public ParseException(Throwable e) {
    super(e);
  }

  public ParseException(String s) {
    super(s);
  }
}
