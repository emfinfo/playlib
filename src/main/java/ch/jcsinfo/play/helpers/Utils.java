package ch.jcsinfo.play.helpers;

import ch.jcsinfo.play.models.BooleanResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.ok;

/**
 * Méthodes statiques d'aide pour les contrôleurs de Play framework.
 *
 * @author jcstritt
 */
public class Utils {

  // nouvelle façon d'utiliser un logger (JCS / 8.8.2019)
  static Logger logger = LoggerFactory.getLogger(Utils.class);

  
  
  /*
   *  METHODES D'AFFIChAGE DANS LE FICHIER DE LOG 
   */
  
  /**
   * Permet d'afficher des informations de log dans la console.
   *
   * @param req un objet représentant une requête HTTP
   * @param timestamp un timestamp en [ms] depuis le départ d'une requête
   */
  public static void logInfo(Http.Request req, long timestamp) {
    
    // on récupère le nom de la personne loguée et la route demandée
    String name = SessionUtils.getUserName(req);
    String route = req.path();

    // si c'est un login, il faut extraire le nom depuis des données encryptées    
    if (route.contains("/session/login")) {
      String data = route.substring(route.lastIndexOf("/")+1);
      name = ch.jcsinfo.cypher.AesUtil.extractName(data);
      route = route.substring(0, route.lastIndexOf("/"));
    }
    int p = route.indexOf("?_=");
    if (p >= 0) {
      route = route.substring(0, p);
    }
    
    // si ce n'est pas la page d'accuei, on affiche le résultat
    if (!route.endsWith("/")) {
      
      // on ajoute le nom de la personne loguée après la route
      String msg = route + " (" + name + ")";

      // on calcule le temps écoulé pour la requête terminée
      if (timestamp >= 0) {
        msg += ", " + (System.currentTimeMillis() - timestamp) + " ms";
      }
      logger.info(msg);
    }
  }

  /**
   * Affiche un message d'erreur dans le fichier de log et renvoie
   * une réponse htpp "bad request".
   *
   * @param ex une exception à gérer
   * @return un résultat de type "bad request"
   */
  public static Result logError(Exception ex) {
    logger.error(ex.getLocalizedMessage());
    return badRequest(ex.getLocalizedMessage());
  }

  
  /*
   * METHODES DE CONVERSION JSON ET AUTRES
   */
  
  /**
   * Transforme un objet quelconque en JSON.
   *
   * @param object l'objet à serialiser en JSON
   * @return un résultat HTTP avec le JSON
   */
  public static Result toJson(Object object) {
    Result result;
    try {
      if (object != null) {
        result = ok(Json.toJson(object)).as("application/json");
      } else {
        result = internalServerError("NULL_OBJECT_ERROR");
      }
    } catch (Exception e) {
      result = logError(e);
    }
    return result;
  }

  /**
   * Transforme une propriété de type clé-valeur en JSON.
   *
   * @param key   la clé de la propriété
   * @param value la valeur de la propriété
   *
   * @return un résultat HTTP avec le JSON
   */
  public static Result toJson(String key, Object value) {
    ObjectNode jsonObj = Json.newObject();
    jsonObj.putPOJO(key, value);
    return ok(jsonObj).as("application/json");
  }

  /**
   * Transforme deux propriétés de type clé-valeur en JSON.
   *
   * @param key1   la clé de la propriété 1
   * @param value1 la valeur de la propriété 1
   * @param key2   la clé de la propriété 2
   * @param value2 la valeur de la propriété 2
   *
   * @return un résultat HTTP avec le JSON
   */
  public static Result toJson(String key1, Object value1, String key2, Object value2) {
    ObjectNode jsonObj = Json.newObject();
    jsonObj.putPOJO(key1, value1);
    jsonObj.putPOJO(key2, value2);
    return ok(jsonObj).as("application/json");
  }

  /**
   * Transforme une valeur booléenne en propriété JSON.
   *
   * @param ok      la variable booléenne à transformer
   * @param message le message associé
   *
   * @return un résultat HTTP avec le JSON
   */
  public static Result toJson(boolean ok, String message) {
    BooleanResult bResult = new BooleanResult(ok, message);
    return ok(Json.toJson(bResult)).as("application/json");
  }

  /**
   * Transforme un objet quelconque en XML.
   *
   * @param object  un objet à transformer en XML
   * @param objects [0]=aliasName, [1]aliasClass
   * @return un résultat HTTP avec du XML
   */
  public static Result toXml(Object object, Object... objects) {
    XStream xstream = new XStream();

    // ajout d'un alias éventuel pour une classe donnée
    if (objects.length >= 2) {
      xstream.alias((String) objects[0], (Class) objects[1]);
    }

    // présentation des dates en ISO
    String dateFormat = "yyyy-MM-dd";
    String[] acceptableFormats = {dateFormat};
    xstream.registerConverter(new DateConverter(dateFormat, acceptableFormats));

//    xstream.setMode(XStream.SINGLE_NODE_XPATH_RELATIVE_REFERENCES);
//    xstream.setMode(XStream.SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES);
//    xstream.setMode(XStream.ID_REFERENCES);
//    xstream.omitField(Compta.class, "login");
    xstream.setMode(XStream.NO_REFERENCES);
    return ok(xstream.toXML(object)).as("application/xml");
  }

  /**
   * Convertit un objet JSON en String.
   *
   * @param node un noeud JSON
   * @param prop le nom d'une propriété à extractName
   *
   * @return un string avec le contenu de la propriété
   */
  public static String toString(JsonNode node, String prop) {
    String result = "";
    JsonNode obj = node.get(prop);
    if (obj != null) {
      result = obj.textValue();
    }
    return result;
  }

  /**
   * Convertit un objet JSON contenu dans un corps de requête POST en objet.
   *
   * @param <T>  le type de l'objet
   * @param req  la requête HTTP de type POST
   * @param type le type de l'objet
   *
   * @return un objet de type T
   */
  public static <T> T toObject(final Request req, final TypeReference<T> type) {
    T result = null;
    ObjectMapper om = new ObjectMapper();
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      String json = req.body().asJson().toString();
//      System.out.println("  >>>toObject json: " + json);
      result = om.<T>readValue(json, type);
    } catch (IOException ex) {
      logError(ex);
    }
    return result;
  }
  
  /**
   * Trnsforme un objet JSON stringifié en objet Java.
   *
   * @param <T>  le type de la réponse
   * @param json on objet JSON stringifié à transformer en objet Java
   * @param type le type de référence pour T
   * @return un objet d'après la requête HTTP fournie
   */
  public static <T> T toObject(final String json, final TypeReference<T> type) {
    T result = null;
    ObjectMapper om = new ObjectMapper();
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      result = om.<T>readValue(json, type);
    } catch (IOException ex) {
      logError(ex);
    }
    return result;
  }

  
  
  /*
   * VALDATION CROSS-DOMAINE
   */
  
  /**
   * Valide le contexte "cross-domain" d'une requête.
   *
   * @param request une requête HTTP
   * @param result  le résultat de la requête HTTP
   */
  public static void validCrossDomainRequest(Http.Request request, Result result) {
    Optional<String> origin = request.header("Origin");
//    Set<String> whiteList = Sets.newHashSet();
//    if (origin != null && whiteList.contains(origin)) {
    boolean ok = origin.isPresent()
      && (origin.get().contains("localhost")
      || origin.get().contains("127.0.0.1")
      || origin.get().contains("vps617676.ovh.net")
      || origin.get().contains("192.168")
      || origin.get().contains("emf-informatique.ch")
      || origin.get().contains("homepage.hispeed.ch"));
    if (ok) {
      result.withHeader("Access-Control-Allow-Origin", origin.get());
      result.withHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
      result.withHeader("Access-Control-Allow-Credentials", "true");
      result.withHeader("Access-Control-Allow-Headers", "Origin, Content-Type");
    }
//    System.out.println("  >>> validCrossDomainContext origin: " + origin + ", ok:" + ok);
  }
  
  
  
  /*
   * MESSAGES
   */
  public static String getMessage(Http.Request req, MessagesApi messagesApi, String key) {
    String sessionLang = SessionUtils.getLang(req);
    Lang lang = new Lang(Lang.forCode(sessionLang));
    return messagesApi.get(lang, key);
  }

  public static List<String> getMessagesList(Http.Request req, MessagesApi messagesApi, String key) {
    List<String> messages = new ArrayList<>();
    String oneMsg;
    int i = 0;
    do {
      oneMsg = getMessage(req, messagesApi, key + i);
      if (!oneMsg.isEmpty()) {
        messages.add(oneMsg);
      }
      i++;
    } while (!oneMsg.isEmpty());
    return messages;
  }

  public static String[] getMessagesArray(Http.Request req, MessagesApi messagesApi, String key) {
    List<String> messages = getMessagesList(req, messagesApi, key);
    return (String[]) messages.toArray();
  }

  public static String[] getMessagesArray(Http.Request req, MessagesApi messagesApi, String key, String regex) {
    String messages[] = getMessage(req, messagesApi, key).split(regex);
    return messages;
  }
}
