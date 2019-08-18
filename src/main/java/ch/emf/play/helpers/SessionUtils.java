package ch.emf.play.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import play.mvc.Http;

/**
 * Classe d'aide pour gérer les informations d'une session utilisateur.
 *
 * @author jcstritt
 */
public class SessionUtils {

  public final static String SESSION_USER_ID = "user-id";
  public final static String SESSION_USER_NAME = "user-name";
  public final static String SESSION_USER_PROFILE = "user-profile";
  public final static String SESSION_USER_PERSON_ID = "user-person-id";
  public final static String SESSION_TIMESTAMP = "timestamp";

  public final static String SESSION_LANG = "fr";
  public final static String SESSION_DB_ID = "db-id";

  /**
   * Teste si la session est ouverte.
   *
   * @param req la reqête HTTP courante
   * @return true si la session est ouverte
   */
  public static boolean isOpen(Http.Request req) {
    return req.session().getOptional(SESSION_USER_ID).isPresent();
  }

  /**
   * Teste si un timeout de session doit intervenir.
   *
   * @param req       la reqête HTTP courante
   * @param msTimeout le temps en [ms] pour qu'un timeout intervienne
   * @return true si la session peut être fermée
   */
  public static boolean isTimeout(Http.Request req, int msTimeout) {
    boolean timeout = false;
    Optional<String> timestamp = req.session().getOptional(SESSION_TIMESTAMP);
    if (isOpen(req) && timestamp.isPresent()) {
      long cTime = System.currentTimeMillis();
      long sTime = Long.parseLong(req.session().getOptional(SESSION_TIMESTAMP).orElse("0"));
      timeout = (cTime - sTime) >= msTimeout;
//      System.out.println("cTime: " + cTime + " sTime: " + sTime + " diff: " + (cTime - sTime) + ", timeout: " + timeout);
    }
    return timeout;
  }

  /**
   * Récupérer une hashmap avec les principales données d'un login.
   * Utile pour les stocker ensuite dans une session.
   *
   * @param userId       un idenfiant d'utilisateur (pk)
   * @param userName     un nom d'utilisateur
   * @param userProfile  un profil d'utilisateur
   * @param userPersonId un identifiant de personne s'il y a lieu
   * 
   * @return la hashmap avec toutes les données.
   */
  public static Map<String, String> getUserInfo(int userId, String userName, String userProfile, int userPersonId) {
    Map<String, String> map = new HashMap<>();
    map.put(SESSION_USER_ID, "" + userId);
    map.put(SESSION_USER_NAME, userName);
    map.put(SESSION_USER_PROFILE, userProfile);
    map.put(SESSION_USER_PERSON_ID, "" + userPersonId);
    map.put(SESSION_TIMESTAMP, "" + System.currentTimeMillis());
    return map;
  }

  /**
   * Récupérer l'indetifiant (pk) de l'utilisateur logué.
   *
   * @param req la reqête HTTP courante
   * @return l'identifiant de la personne loguée ou 0 si non trouvé
   */
  public static int getUserId(Http.Request req) {
    String userId = req.session().getOptional(SESSION_USER_ID).orElse("0");
    return Integer.parseInt(userId);
  }
  
  /**
   * Récupérer l'indetifiant de l'utilisateur logué dans un objet Optional.
   *
   * @param req la reqête HTTP courante
   * @return l'identifiant de la personne loguée
   */
  public static Optional<String> getOptionalUserId(Http.Request req) {
    return req.session().getOptional(SESSION_USER_ID);
  }

  /**
   * Récupérer le nom d'utilisateur de l'utilisateur logué.
   *
   * @param req la reqête HTTP courante
   * @return le nom de l'utilisateur logué ou "?" si pas trouvé
   */
  public static String getUserName(Http.Request req) {
    return req.session().getOptional(SESSION_USER_NAME).orElse("?");
  }

  /**
   * Récupérer le profil de l'utilisateur logué.
   *
   * @param req la reqête HTTP courante
   * @return le profil de l'utilisateur logué ou "?" si pas trouvé
   */
  public static String getUserProfile(Http.Request req) {
    return req.session().getOptional(SESSION_USER_PROFILE).orElse("?");
  }

  /**
   * Récupérer l'indetifiant (pk) de la personne loguée.
   *
   * @param req la reqête HTTP courante
   * @return l'identifiant de la personne loguée ou 0 si pas trouvé
   */
  public static int getUserPersonId(Http.Request req) {
    String personId = req.session().getOptional(SESSION_USER_PERSON_ID).orElse("0");
    return Integer.parseInt(personId);
  }

  /**
   * Récupérer la langue mémorisée pour la session. Ce n'est pas forcément
   * la langue de l'utilisateur, mais une langue choisie côté client ou la
   * langue par défaut définie dans le navigateur.<br>
   * <br>
   * Si elle n'existe pas dans la session, c'est "fr" qui est retourné par défaut.
   *
   * @param req la reqête HTTP courante
   * @return une langue sur 2 caractères
   */
  public static String getLang(Http.Request req) {
    return req.session().getOptional(SESSION_LANG).orElse("fr");
  }

  /**
   * Dans une approche multi-tenants où l'user-id et le db-id forment un
   * tenant, cette méthode récupére l'identifiant de la db en cours.
   *
   * @param req la reqête HTTP courante
   * @return l'identifiant de la db en cours ou 0 si non trouvé
   */
  public static int getDbId(Http.Request req) {
    String tenantId = req.session().getOptional(SESSION_DB_ID).orElse("0");
    return Integer.parseInt(tenantId);
  }

}
