package PacmanLogger

import java.io.{File,PrintWriter,IOException}

object PacmanLogger {
	def main(args: Array[String]) = {
		val src = scala.io.Source.fromFile("/var/log/pacman.log")
		val lines = src.mkString
		val p = new PacmanLoggerParser
		val res = p.parseAll(p.logs, lines)
		res match {
			case p.Success(parsedLogs,_) =>
				val logger = new Logger(parsedLogs.zipWithIndex.map{ case (x,n) => (n.toString)::x})
				logger.start
			case x => println("ERROR: " + x.toString)
		}
		src close
	}
}
