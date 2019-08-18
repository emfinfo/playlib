package ch.emf.play.models;

/**
 * Permet de mémoriser les informations sur les données d'une release de l'application.
 * 
 * @author jcstritt
 */
public class ReleaseInfo {
  private String application;
  private String server;
  private String data;

  public ReleaseInfo() {
  }

  public ReleaseInfo(String application, String server, String data) {
    this.application = application;
    this.server = server;
    this.data = data;
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String app) {
    this.application = app;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return  "application=" + application + ", server=" + server + ", data=" + data;
  }
  
}
