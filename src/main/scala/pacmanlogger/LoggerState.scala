package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.screen._

abstract class LoggerState {
	val table: AbstractTable
	def getFoot: List[(String, String)]
	def f4: Unit = ()
	def f5: Unit = ()
	def f: Unit = ()
	def s: Unit = ()
	def esc: Unit = ()
	def space: Unit = ()
}

class MainTableState(logger: Logger, val table: AbstractTable, screen: Screen) extends LoggerState {
	override def getFoot = List(("F3", "Quit  "), ("F4", "Filter"), ("F5","SortBy")/*,("F6","Search")*/ )
	override def f4 = {
		logger.focussedTable = logger.filterTable
		logger.mainTableOffset = 18
		logger.focussedTableOffset = 0
		logger.state = logger.filterTableState
		screen.clear()
	}
	override def f5 = {
		logger.focussedTable = logger.sortByTable
		logger.mainTableOffset = 15
		logger.focussedTableOffset = 0
		logger.state = logger.sortByTableState
		screen.clear()
	}
	override def f = f4
	override def s = f5
}

class FilterTableState(logger: Logger, val table: OptionCursor, screen: Screen) extends LoggerState {
	override def getFoot = List(("Space ", "Enable"), ("Esc", "Done  "))
	override def f = esc
	override def esc = {
		logger.focussedTable = logger.mainTable
		logger.mainTableOffset = 0
		logger.focussedTableOffset = 0
		logger.state = logger.mainTableState
		screen.clear()
	}
	override def space {
		table.click
		screen.clear()
	}
}

class SortByTableState(logger: Logger, val table: OptionCursor, screen: Screen) extends LoggerState {
	override def getFoot = List(("Space", "Sort  "), ("Esc", "Done  "))
	override def s = esc
	override def esc = {
		logger.focussedTable = logger.mainTable
		logger.mainTableOffset = 0
		logger.focussedTableOffset = 0
		logger.state = logger.mainTableState
		screen.clear()
	}
	override def space {
		table.click
		screen.clear()
	}
}