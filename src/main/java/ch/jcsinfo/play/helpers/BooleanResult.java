package ch.jcsinfo.play.helpers;

/**
 * Un modèle de données pour un retour Play framework de type boolean.
 *
 * @author jcstritt
 */
public class BooleanResult {
  private boolean ok;
  private String message;

  public BooleanResult() {
  }

  public BooleanResult(boolean ok, String message) {
    this.ok = ok;
    this.message = message;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
