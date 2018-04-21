package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.terminal._

trait AbstractTable {
	val screen: Screen
	val tg: TextGraphics
	var terminalSize: TerminalSize
	var colWidths: Array[Int]
	def getRows: List[List[String]]
	def getAllRows: List[List[String]]
	def updateValues: Unit
	def isLastRow: Boolean
	def scrollRows(n: Int): Unit
	def scrollStart: Unit
	def scrollEnd: Unit
	def getFirstRow: Int
	def draw(offset: Int)
	def drawHeader(offset: Int)
	def drawRow(titles: List[String], column: Int, row: Int)
}
