package pacmanlogger

import scala.collection.immutable.HashMap
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

abstract class OptionTable(title: String, optionTuples: List[String], options: List[Boolean], subjectTable: Table, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
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
		val settingsList = settings.toList.sortWith(_._1<_._1)
		tuples = {
			settingsList.toList map { (t: (String, Boolean)) =>
				t match {
					case (s, true) => List("[X]", s)
					case (s, false) => List("[ ]", s)
				}
			}
		}
		rows = updateRows
	}
}

class FilterTable(title: String, tuples: List[String], options: List[Boolean], filterableTable: Filterable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends OptionTable(title, tuples, options, filterableTable, fullScreen, screen, tg) {
	
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
		filterableTable.setFilterFunction(filterFunction)
		filterableTable.updateValues
	}
	
	def filterFunction = {
		t: List[String] =>
			settings.getOrElse(t(2), false)
	}
}

class SortByTable(title: String, var index: Int, sortableTable: Sortable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends OptionTable(title, sortableTable.getTitles, sortableTable.getTitles.zipWithIndex map (t =>
			t match {
				case (_, i) if i == index => true
				case _ => false
			}), sortableTable, fullScreen, screen, tg) {
	val sortingTitles = sortableTable.getTitles
	
	override def switchOption(r: List[String]) = {
		sortingTitles.zipWithIndex foreach {
			case (s,i) if r(1) == s  =>
				index = i
				settings = settings.updated(s, true)
			case (s,_) => ()
				settings = settings.updated(s, false)
		}
		sortableTable.sortByIndex(index)
		sortableTable.updateValues
		updateValues
	}
}
