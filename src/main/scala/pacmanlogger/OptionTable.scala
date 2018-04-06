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
	
	var settings: List[(String, Boolean)] = {
		var m = List[(String, Boolean)]()
		optionTuples.zip(options) foreach {
			case (action, option) => m = m :+(action, option)
		}
		m
	}

	override def updateValues = {
		nRows = terminalSize.getRows - 2
		tuples = {
			settings map { (t: (String, Boolean)) =>
				t match {
					case (s, true) => List("[X]", s)
					case (s, false) => List("[ ]", s)
				}
			}
		}
		rows = updateRows
	}
}

class FilterTable(title: String, tuples: List[String], index: Int, options: List[Boolean], filterableTable: Filterable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends OptionTable(title, tuples, options, filterableTable, fullScreen, screen, tg) {
	
	def this(title: String, tuples: List[String], index: Int, filterableTable: Filterable, fullScreen: Boolean, screen: Screen, tg: TextGraphics) {
		this(title, tuples, index, List(true, true, true, true, true), filterableTable, fullScreen, screen, tg)
	}
	
	override def switchOption(r: List[String]) = {
		settings.zipWithIndex foreach {
			case ((s, true),i) if r(1) == s =>
				val values: List[Boolean] = settings.unzip._2
				if (values.indexOf(true) != values.lastIndexOf(true))
					settings = settings.updated(i,(s, false))
			case ((s, false),i) if r(1) == s =>
				settings = settings.updated(i,(s, true))
			case _ => ()
		}
		filterableTable.setFilterFunction(filterFunction)
		filterableTable.updateValues
	}
	
	def filterFunction = {
		val settingsMap = settings.toMap
		t: List[String] =>
			settingsMap.getOrElse(t(index), false)
	}
}

class SortByTable(title: String, var index: Int, sortableTable: Sortable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends OptionTable(title, sortableTable.getTitles, sortableTable.getTitles.zipWithIndex map (t =>
			t match {
				case (_, i) if i == index => true
				case _ => false
			}), sortableTable, fullScreen, screen, tg) {
	val sortingTitles = sortableTable.getTitles
	sortableTable.sortByIndex(index)
	
	override def switchOption(r: List[String]) = {
		sortingTitles.zipWithIndex foreach {
			case (s,i) if r(1) == s  =>
				index = i
				settings = settings.updated(i, (s,true))
			case (s,i) =>
				settings = settings.updated(i, (s,false))
		}
		sortableTable.sortByIndex(index)
		sortableTable.updateValues
		updateValues
	}
}
