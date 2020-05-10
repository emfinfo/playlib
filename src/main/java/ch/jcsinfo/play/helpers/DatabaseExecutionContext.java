package ch.jcsinfo.play.helpers;

import akka.actor.ActorSystem;
import javax.inject.Inject;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

/**
 * Contexte d'exécution personnalisé lié au thread-pool "database.dispatcher".<br>
 * Voir l'exemple original sous https://github.com/playframework/play-java-jpa-example.
 *
 * @author Jean-Claude Stritt
 */
public class DatabaseExecutionContext implements ExecutionContextExecutor {
  private final ExecutionContext dbExCtx;
  private static final String NAME = "database.dispatcher";

  @Inject
  public DatabaseExecutionContext(ActorSystem actorSystem) {
    this.dbExCtx = actorSystem.dispatchers().lookup(NAME);
  }

  @Override
  public void execute(Runnable command) {
    dbExCtx.execute(command);
  }

  @Override
  public void reportFailure(Throwable cause) {
    dbExCtx.reportFailure(cause);
  }
}
