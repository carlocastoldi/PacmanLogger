package PacmanLogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.screen._

trait LoggerState {
  val table: AbstractTable
  def getFoot: List[(String,String)]
  def f4: Unit
  def esc: Unit
  def enter: Unit
  def getNextState: LoggerState
}

class MainTableState(logger: Logger, val table: AbstractTable, screen: Screen) extends LoggerState {
  var nextState: LoggerState = this
  
  override def getNextState = nextState
  override def getFoot = List(("F3","Quit"),("F4","Search"),("F5","Filter"),("F6","SortBy"))
  override def f4 = {
    nextState = new FilterTableState(logger, logger.filterTable, screen)
    logger.focussedTable = logger.filterTable
    logger.mainTableOffset = 18
    logger.focussedTableOffset = 0
    screen.clear()
  }
  override def esc = ()
  override def enter = ()
}

class FilterTableState(logger: Logger, val table: OptionCursor, screen: Screen) extends LoggerState {
  var nextState: LoggerState = this
  //val filters = table.map(r => r)
  
  override def getNextState = nextState
  override def getFoot = List(("Enter","Enable"),("ESC","Accept"))
  override def f4 = ()
  override def esc = {
    nextState = new MainTableState(logger, logger.filterTable, screen)
    logger.focussedTable = logger.mainTable
    logger.mainTableOffset = 0
    logger.focussedTableOffset = 0
    screen.clear()
  }
  override def enter {
    table.switchOption(table.getSelectedRow)
    table.updateValues
    screen.clear()
  }
}