package pacmanlogger

// import java.time.OffsetDateTime
// import java.time.format.DateTimeFormatter

import scala.util.parsing.combinator._
//import java.util.GregorianCalendar

class PacmanLoggerParser extends JavaTokenParsers {
	override protected val whiteSpace = "[ \t]+".r

	def logs = opt(otherLogs) ~> repsep(alpmLog, otherLogs) <~ opt(otherLogs)
	def alpmLog = ("[" ~> time <~ "]") ~ ("[ALPM]" ~> action) ~ pktName ~ pktVers <~ ("\r\n" | "\n") ^^ {
		case time ~ action ~ pkt ~ vers => List(time, action, vers.v1, vers.v2, pkt)
	}
	def otherLogs = rep(otherLog)
	def otherLog = (fakeLog | fs) <~ ("\r\n" | "\n")
	def fakeLog = notAlpm | fakeAlpm
	def notAlpm = (("[" ~> time <~ "]") ~> ("[" ~> ("PAMAC" | "ALPM-SCRIPTLET" | "PACMAN" | "PACKAGEKIT") <~ "]") <~ ".*".r) //^^ { _ => ""}
	def fakeAlpm = ("[" ~> time <~ "]") ~> "[ALPM]" <~ notAction <~ ".*".r

	//def notAction = "^(?!upgraded$|installed$|removed$).*".r
	//def notAction = "^((?!installed|removed|upgraded)[\\s\\S])*$".r
	def notAction = "transaction" | "running" | "warning"

	def fs = "filesystem:" <~ "[0-9]+".r <~ "package:" <~ "[0-9]+".r

	def time = timezoneLess | timezone

	def timezoneLess = (fourDig <~ "-") ~ (twoDig <~ "-") ~ twoDig ~ twoDig ~ (":" ~> twoDig) ^^ {
		case year ~ month ~ day ~ hour ~ minute => year+"-"+month+"-"+day+" "+hour+":"+minute+":00" //new GregorianCalendar(year.toInt, month.toInt, day.toInt, hour.toInt, minute.toInt)
	}
	def timezone = (fourDig <~ "-") ~ (twoDig <~ "-") ~ twoDig ~ ("T" ~> twoDig <~ ":") ~ (twoDig <~ ":") ~
		twoDig ~ ("+"|"-") ~ fourDig ^^ {
		case year ~ month ~ day ~ hour ~ minute ~ second ~ sign ~ offset =>
			val offsetHour: String = offset.substring(0,2)
			val offsetMinute: String = offset.substring(2,4)
			f"$year-$month-$day"+f"T$hour:$minute:$second$sign$offsetHour:$offsetMinute"
			// val date = OffsetDateTime.parse(f"$year-$month-$day"+f"T$hour:$minute:$second$sign$offsetHour:$offsetMinute")
			// date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
	}
	def action = ("downgraded" | "installed" | "removed" | "reinstalled" | "upgraded") ^^ { _ toUpperCase }
	def pktName = "[a-zA-Z0-9\\+-_.]+".r <~ "("
	def pktVers = "[^()\\s]+".r ~ opt("->" ~> "[^()\\s]+".r) <~ ")" ^^ { v =>
		v match {
			case v1 ~ Some(v2) => PktVersion(v1, v2)
			case v1 ~ None => new PktVersion(v1)
		}
	}

	def fourDig = "[0-9]{4}".r
	def twoDig = "[0-9]{2}".r
}