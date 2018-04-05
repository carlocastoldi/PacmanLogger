package pacmanlogger

import com.googlecode.lanterna._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.googlecode.lanterna.terminal._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.input._

class Logger(var logs: List[List[String]]) {

	val terminal = new DefaultTerminalFactory().createTerminal
	val screen = new TerminalScreen(terminal)
	screen.startScreen
	screen.setCursorPosition(null)

	val textGraphics = screen.newTextGraphics
	var terminalSize = screen.getTerminalSize

	val titles = List("N  ", "Date", "Action", "Version1", "Version2", "Packet")
	val mainTable = new Table(titles, logs, true, screen, textGraphics) with Filterable with Sortable with Cursor
	val filters = logs.map((l: List[String]) => l(2)).distinct.sortWith(_<_)
	val filterTable = new FilterTable("Filter By", filters, List(true, true, true, true, true), mainTable, false, screen, textGraphics) with OptionCursor
	val sortByTable = new SortByTable("Sort By", 1, mainTable, false, screen, textGraphics) with OptionCursor
	
	var focussedTable: Cursor = mainTable
	var mainTableOffset = 0
	var focussedTableOffset = mainTableOffset

	def start {
		var state: LoggerState = new MainTableState(this, mainTable, screen)

		val f = Future {
			while (true) {
				val newSize = screen.doResizeIfNecessary
				if (newSize != null) {
					terminalSize = newSize
					screen.clear()
					mainTable.updateSize
					filterTable.updateSize
					sortByTable.updateSize
					draw(state)
					screen.refresh()
				}
				Thread.sleep(100)
			}
		}
		mainTable.draw(terminalSize, mainTableOffset)
		var keyStroke = screen.pollInput()
		while (keyStroke == null || (KeyType.F3 != keyStroke.getKeyType && 'q' != keyStroke.getCharacter) || focussedTable != mainTable) {
			if (keyStroke != null) {
				keyStroke.getKeyType match {
					case KeyType.ArrowDown => focussedTable.moveCursor(1, textGraphics, focussedTableOffset, terminalSize)
					case KeyType.ArrowUp => focussedTable.moveCursor(-1, textGraphics, focussedTableOffset, terminalSize)
					case KeyType.PageDown => focussedTable.moveCursor(terminalSize.getRows - 2, textGraphics, focussedTableOffset, terminalSize)
					case KeyType.Home => focussedTable.moveCursorStart(textGraphics, focussedTableOffset, terminalSize)
					case KeyType.End => focussedTable.moveCursorEnd(textGraphics, focussedTableOffset, terminalSize)
					case KeyType.PageUp => focussedTable.moveCursor(-(terminalSize.getRows - 2), textGraphics, focussedTableOffset, terminalSize)
					case KeyType.F4 => state.f4
					case KeyType.F5 => state.f5
					case KeyType.Escape => state.esc
					case KeyType.Character => handleCharacter(keyStroke, state)
					case _ => ()
				}
				state = state.getNextState
			}
			draw(state)
			screen.refresh()
			keyStroke = screen.readInput
		}
		screen.close
	}

	def draw(state: LoggerState) {
		mainTable.draw(terminalSize, mainTableOffset)
		if (mainTableOffset > 0)
			focussedTable.draw(terminalSize, 0)
		drawFoot(state.getFoot, 0)
	}

	def drawFoot(commands: List[(String, String)], off: Int) {
		var offset = off
		val columns = terminalSize.getColumns()
		val row = terminalSize.getRows - 1
		var total = "Total "+mainTable.getAllRows.length
		commands foreach {
			case (k, c) =>
				textGraphics.setForegroundColor(TextColor.ANSI.CYAN)
				textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT)
				textGraphics.putString(offset, row, k)
				offset += k.length
				textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
				textGraphics.setBackgroundColor(TextColor.ANSI.CYAN)
				textGraphics.putString(offset, row, c)
				offset += c.length
		}

		textGraphics.putString(offset, row, " " * (columns - offset))
		textGraphics.putString(offset + columns - offset - total.length, row, total)
	}

	def handleCharacter(keyStroke: KeyStroke, state: LoggerState) {
		keyStroke.getCharacter.toChar match {
			case ' ' => state.space
			case 'f' => state.f
			case 's' => state.s
			case _ => ()
		}
	}
}
