package PacmanLogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.terminal._

trait AbstractTable {
  def getRows: List[List[String]]
  def updateValues: Unit
  def isLastRow: Boolean
  def scrollRows(n: Int): Unit
  def getScreen: Screen
  def getTextGraphics: TextGraphics
  def draw(terminalSize: TerminalSize, offset: Integer)
  def drawRow(titles: List[String], column: Int, row: Int)
}