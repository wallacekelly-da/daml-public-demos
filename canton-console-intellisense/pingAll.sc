import $cp.lib.`canton-open-source-2.8.3.jar`
import $file.cantonEnv, cantonEnv._

import com.digitalasset.canton.logging.NamedLoggerFactory
import com.typesafe.scalalogging.Logger

val loggerFactory = consoleEnvironment.environment.loggerFactory
val logger = Logger("pingAll")

logger.info("Pinging all the participants.")
for (p1 <- participants.remote) {
  for (p2 <- participants.remote) {
    logger.info("Pinging " + p2.id + " from " + p1.id)
    p1.health.ping(p2.id)
  }
}
