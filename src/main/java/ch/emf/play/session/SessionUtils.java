package ch.emf.play.session;

import play.Logger;
import static play.mvc.Controller.session;

/**
 * Classe d'aide pour gérer les informations d'une session utilisateur.
 *
 * @author jcstritt
 */
public class SessionUtils {
  public final static String SESSION_USER_ID = "user-id";
  public final static String SESSION_USER_NAME = "user-name";
  public final static String SESSION_USER_PROFILE = "user-profile";
  public final static String SESSION_TIMESTAMP = "timestamp";

  public final static String SESSION_LANG = "fr";
  public final static String SESSION_DB_ID = "db-id";

  /**
   * Convertit un string contenant un nombre entier en nombre entier de type "int".
   *
   * @param sValue la valeur string représentant un nombre entier
   * @return la valeur convertie en entier de type "int"
   */
  private static int stringToInt(String sValue) {
    int value = 0;
    try {
      value = Integer.parseInt(sValue);
    } catch (NumberFormatException ex) {
    }
    return value;
  }

  /**
   * Convertit un string contenant un nombre entier en nombre entier de type "long".
   *
   * @param sValue la valeur string représentant un nombre entier
   * @return la valeur convertie en entier de type "long"
   */
  private static long stringToLong(String sValue) {
    long value = 0;
    try {
      value = Long.parseLong(sValue);
    } catch (NumberFormatException ex) {
    }
    return value;
  }

  /**
   * Efface le contenu de la session en cours.
   */
  public static void clear() {
    String name = getUserName();
    session().clear();
    if (!name.equals("?name?")) {
      Logger.info("SESSION CLEAR (" + name + ")");
    }
  }

  /**
   * Teste si la session est ouverte.
   *
   * @return true si la session est ouverte
   */
  public static boolean isOpen() {
    return session().get(SESSION_USER_ID) != null;
  }

  /**
   * Teste si la session est ouverte et en même temps l'efface
   * si le timeout de session est dépassé.
   *
   * @param ms le temps en [ms] pour qu'un timeout intervienne
   * @return true si une session est ouverte
   */
  public static boolean isOpen(int ms) {
    boolean ok = isOpen();
    if (ok && isTimeout(ms)) {
      clear();
      ok = false;
    }
    return ok;
  }

  /**
   * Teste si un timeout de session doit intervenir.
   *
   * @param ms le temps en [ms] pour qu'un timeout intervienne
   * @return true si la session peut être fermée
   */
  public static boolean isTimeout(int ms) {
    long cTime = System.currentTimeMillis();
    long sTime = stringToLong(session().get(SESSION_TIMESTAMP));
//    System.out.println("cTime: " + cTime + " sTime: " + sTime + " diff: " + (cTime-sTime));
    return (cTime-sTime) >= ms;
  }

  /**
   * Reset la propriété "timestamp" dans le cookie avec le temps actuel.
   */
  public static void resetTimeout() {
    boolean ok = session().get(SESSION_USER_ID) != null;
    if (ok) {
      session(SESSION_TIMESTAMP, "" + System.currentTimeMillis());
    }
  }


  /**
   * Sauve les informations essentielles de l'utilisateur qui veut se loguer.
   * Ces informations proviennent généralement d'une base de données où ces
   * informations sont stockées. Un timestamp est aussi mis-à-jour dans la session.
   *
   * @param userId l'identifiant de login
   * @param userName le nom de login
   * @param userProfile le profil de login
   */
  public static void saveUserInfo(int userId, String userName, String userProfile) {
    session(SESSION_USER_ID, "" + userId);
    session(SESSION_USER_NAME, userName);
    session(SESSION_USER_PROFILE, userProfile);
    session(SESSION_TIMESTAMP, "" + System.currentTimeMillis());
  }


  /**
   * Récupérer l'indetifiant (pk) de l'utilisateur logué.
   *
   * @return l'identifiant de la personne loguée ou 0 si non trouvé
   */
  public static int getUserId() {
    String userId = session().get(SESSION_USER_ID);
    return stringToInt(userId);
  }

  /**
   * Mémoriser l'identifiant (pk) de l'utilisateur logué.
   *
   * @param userId un identifiant (pk) de l'utilisateur logué
   */
  public static void setUserId(int userId) {
    session(SESSION_USER_ID, "" + userId);
  }


  /**
   * Récupérer le nom d'utilisateur de l'utilisateur logué.
   *
   * @return le nom en question
   */
  public static String getUserName() {
    String name = session().get(SESSION_USER_NAME);
    if (name == null || name.isEmpty()) {
      name = "?name?";
    }
    return name;
  }

  /**
   * Mémoriser le nom de l'utilisateur logué.
   *
   * @param userName le nom de l'utilisateur logué
   */
  public static void setUserName(String userName) {
    session(SESSION_USER_NAME, userName);
  }


  /**
   * Récupérer le profil de l'utilisateur logué.
   *
   * @return le profil en question
   */
  public static String getUserProfile() {
    String profile = session().get(SESSION_USER_PROFILE);
    if (profile == null || profile.isEmpty()) {
      profile = "?profil?";
    }
    return profile;
  }

  /**
   * Mémoriser le profile de l'utilisateur logué.
   *
   * @param profile le profil de l'utilisateur logué
   */
  public static void setUserProfile(String profile) {
    session(SESSION_USER_PROFILE, profile);
  }


  /**
   * Récupérer la langue mémorisée pour la session. Ce n'est pas forcément
   * la langue de l'utilisateur, mais une langue choisie côté client ou la
   * langue par défaut définie dans le navigateur.<br>
   * <br>
   * Si elle n'existe pas dans la session, c'est "fr" qui est retourné par défaut.
   *
   * @return une langue sur 2 caractères
   */
  public static String getLang() {
    String lang = session().get(SESSION_LANG);
    if (lang == null || lang.isEmpty()) {
      lang = "fr";
    }
    return lang;
  }

  /**
   * Mémoriser la langue choisie pour la session. Ce n'est pas forcément
   * la langue de l'utilisateur, mais une langue choisie côté client ou la
   * langue par défaut définie dans le navigateur.<br>
   * <br>
   * Devrait être sur 2 caractères pour respecter l'i18N (par exemple "fr")
   *
   * @param lang la langue choisie pour la session
   */
  public static void setLang(String lang) {
    session(SESSION_LANG, lang);
  }

  /**
   * Dans une approche multi-tenants où l'user-id et le db-id forment un
   * tenant, cette méthode récupére l'identifiant de la db en cours.
   *
   * @return l'identifiant de la db en cours ou 0 si non trouvé
   */
  public static int getDbId() {
    String tenantId = session().get(SESSION_DB_ID);
    return stringToInt(tenantId);
  }

  /**
   * Dans une approche multi-tenants où l'user-id et le db-id forment un
   * tenant, cette méthode permet de préciser l'identifiant de la db.
   *
   * @param comptaId un identifiant numérique
   */
  public static void setDbId(int comptaId) {
    session(SESSION_DB_ID, "" + comptaId);
  }


}
