package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.terminal._

class Table(val titles: List[String], var tuples: List[List[String]], fullScreen: Boolean, val screen: Screen, val tg: TextGraphics)
	extends AbstractTable {

	var colWidths = new Array[Int](titles.length)
	var terminalSize = screen.getTerminalSize
	var firstRow = 0
	var nRows: Int = screen.getTerminalSize.getRows - 2
	var rows: List[List[String]] = updateRows
	var tuplesLength = tuples.length

	def getRows = rows
	def getAllRows = tuples

	def updateRows =
		nRows match {
			case n if n > tuplesLength =>
				tuples
			case n if n > (tuplesLength - firstRow) =>
				tuples.drop(terminalSize.getRows)
			case _ => tuples.drop(firstRow).take(nRows)
		}
	
	def updateSize = {
		terminalSize = screen.getTerminalSize
		nRows = terminalSize.getRows - 2
		rows = updateRows
	}

	def updateValues = {
		nRows = terminalSize.getRows - 2
		rows = updateRows
		tuplesLength = tuples.length
	}

	override def getFirstRow = firstRow

	def isLastRow = firstRow + nRows == tuplesLength + 1

	override def draw(offset: Int) {
		calcColWidths
		drawHeader(offset)
		tg.setForegroundColor(TextColor.ANSI.CYAN)
		tg.setBackgroundColor(TextColor.ANSI.DEFAULT)
		val localRows = rows
		localRows.zipWithIndex foreach {
			case (r, i) => drawRow(r, offset, i + 1)
		}
	}
	
	def drawHeader(offset: Int) {
		tg.setForegroundColor(TextColor.ANSI.BLACK)
		tg.setBackgroundColor(TextColor.ANSI.GREEN)
		drawRow(titles, offset, 0)
	}

	override def drawRow(titles: List[String], column: Int, row: Int) {
		val columns = terminalSize.getColumns
		var offset = column
		titles.zipWithIndex foreach {
			case (title, i) if (offset + colWidths(i) + 1 <= columns) =>
				tg.putString(offset, row, title+" " * (colWidths(i) - title.length + 1))
				offset = offset + colWidths(i)
			case (title, i) if offset <= columns =>
				val width = columns - offset
				tg.putString(offset, row, title+" " * (width + 1))
				offset = offset + colWidths(i)
			case _ => ()
		}
		if (fullScreen) tg.putString(offset, row, " " * (columns - offset))
	}

	override def scrollRows(n: Int) {
		val totalLength = tuplesLength
		val rowsLength = nRows
		if (firstRow + n >= 0 && firstRow + n + rowsLength <= totalLength)
			firstRow += n
		else if (firstRow + n >= 0 && firstRow + rowsLength <= totalLength)
			firstRow = totalLength - rowsLength
		else if (firstRow > 0 && firstRow + n + rowsLength <= totalLength)
			firstRow = 0
	}

	override def scrollStart {
		firstRow = 0
	}

	override def scrollEnd {
		val totalLength = tuplesLength
		val rowsLength = nRows

		firstRow = totalLength - rowsLength
	}

	def calcColWidths {
		colWidths = new Array[Int](titles.length)
		val localRows = titles :: rows
		localRows foreach { row =>
			row.zipWithIndex.foreach {
				case (element, column) if element.length >= colWidths(column) =>
					colWidths.update(column, element.length + 1)
				case _ => ()
			}
		}
	}
}