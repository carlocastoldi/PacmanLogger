package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.screen._

abstract class LoggerState {
	val table: AbstractTable
	def getFoot: List[(String, String)]
	def getNextState: LoggerState
	def f4: Unit = ()
	def f5: Unit = ()
	def f: Unit = ()
	def s: Unit = ()
	def esc: Unit = ()
	def space: Unit = ()
}

class MainTableState(logger: Logger, val table: AbstractTable, screen: Screen) extends LoggerState {
	var nextState: LoggerState = this

	override def getNextState = nextState
	override def getFoot = List(("F3", "Quit  "), ("F4", "Filter"), ("F5","SortBy")/*,("F6","Search")*/ )
	override def f4 = {
		nextState = new FilterTableState(logger, logger.filterTable, screen)
		logger.focussedTable = logger.filterTable
		logger.mainTableOffset = 18
		logger.focussedTableOffset = 0
		screen.clear()
	}
	override def f5 = {
		nextState = new SortByTableState(logger, logger.sortByTable, screen)
		logger.focussedTable = logger.sortByTable
		logger.mainTableOffset = 15
		logger.focussedTableOffset = 0
		screen.clear()
	}
	override def f = f4
	override def s = f5
}

class FilterTableState(logger: Logger, val table: OptionCursor, screen: Screen) extends LoggerState {
	var nextState: LoggerState = this

	override def getNextState = nextState
	override def getFoot = List(("Space ", "Enable"), ("Esc", "Done  "))
	override def f = esc
	override def esc = {
		nextState = new MainTableState(logger, logger.mainTable, screen)
		logger.focussedTable = logger.mainTable
		logger.mainTableOffset = 0
		logger.focussedTableOffset = 0
		screen.clear()
	}
	override def space {
		table.click
		screen.clear()
	}
}

class SortByTableState(logger: Logger, val table: OptionCursor, screen: Screen) extends LoggerState {
	var nextState: LoggerState = this

	override def getNextState = nextState
	override def getFoot = List(("Space", "Sort  "), ("Esc", "Done  "))
	override def s = esc
	override def esc = {
		nextState = new MainTableState(logger, logger.mainTable, screen)
		logger.focussedTable = logger.mainTable
		logger.mainTableOffset = 0
		logger.focussedTableOffset = 0
		screen.clear()
	}
	override def space {
		table.click
		screen.clear()
	}
}