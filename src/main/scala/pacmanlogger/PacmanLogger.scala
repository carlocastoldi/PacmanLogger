package pacmanlogger

import java.io.{File, PrintWriter, IOException}

object PacmanLogger {
	def main(args: Array[String]) = {
		val lines = """[2018-04-07 11:43] [ALPM] installed l-smash (2.14.5-1)
[2018-03-07 11:43] [ALPM] installed x264 (1:152.20171224-1)
[2018-05-07 12:25] [ALPM] removed libva-mesa-driver (18.0.0-2)
"""
		val p = new PacmanLoggerParser
		val res = p.parseAll(p.logs, lines)
		res match {
			case p.Success(parsedLogs, _) =>
				val logger = new Logger(parsedLogs)
				logger.start
			case x => println("ERROR: "+x.toString)
		}
	}
}

//package pacmanlogger
//
//import java.io.{File, PrintWriter, IOException}
//
//object PacmanLogger {
//	def main(args: Array[String]) = {
//		val src = scala.io.Source.fromFile("/var/log/pacman.log")
//		val lines = src.mkString
//		val p = new PacmanLoggerParser
//		val res = p.parseAll(p.logs, lines)
//		res match {
//			case p.Success(parsedLogs, _) =>
//				val logger = new Logger(parsedLogs)
//				logger.start
//			case x => println("ERROR: "+x.toString)
//		}
//		src close
//	}
//}

