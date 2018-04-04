package pacmanlogger

import scala.collection.immutable.HashMap
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

abstract class OptionTable(title: String, var optionTuples: List[String], options: List[Boolean], subjectTable: FilterableTable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends Table(List("", title),
		optionTuples.zip(options).map(t =>
			t match {
				case (s, true) => List("[X]", s)
				case (s, false) => List("[ ]", s)
			}), fullScreen, screen, tg) {
	
	def switchOption(r: List[String]): Unit
	
	var settings: HashMap[String, Boolean] = {
		var m = new HashMap[String, Boolean]

		optionTuples.zip(options) foreach {
			case (action, option) => m = m.updated(action, option)
		}
		m
	}

	override def updateValues = {
		nRows = terminalSize.getRows - 2
		tuples = {
			settings.toList map { (t: (String, Boolean)) =>
				t match {
					case (s, true) => List("[X]", s)
					case (s, false) => List("[ ]", s)
				}
			}
		}
		rows = updateRows
	}
}

class FilterTable(title: String, tuples: List[String], options: List[Boolean], filteredTable: FilterableTable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends OptionTable(title, tuples, options, filteredTable, fullScreen, screen, tg) {
	
	override def switchOption(r: List[String]) = {
		settings.toList foreach {
			case (s, true) if r(1) == s =>
				val values: List[Boolean] = settings.toList.unzip._2
				if (values.indexOf(true) != values.lastIndexOf(true))
					settings = settings.updated(s, false)
			case (s, false) if r(1) == s =>
				settings = settings.updated(s, true)
			case _ => ()
		}
		filteredTable.filter = setFilterFunction
	}
	
	def setFilterFunction = {
		t: List[String] =>
			settings.getOrElse(t(2), false)
	}
}
