package pacmanlogger

import java.io.{File, PrintWriter, IOException}

object PacmanLogger {
	def main(args: Array[String]) = {
		val src = scala.io.Source.fromFile("/var/log/pacman.log")
		val lines = src.mkString
		src close
		val p = new PacmanLoggerParser
		val res = p.parseAll(p.logs, lines)
		res match {
			case p.Success(parsedLogs, _) =>
				val logger = new Logger(parsedLogs)
				logger.start
			case x => System.err.println("ERROR: "+x.toString)
		}
	}
}
