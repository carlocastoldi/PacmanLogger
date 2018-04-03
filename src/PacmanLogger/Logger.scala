package PacmanLogger

import com.googlecode.lanterna._
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
  
	val titles = List("N  ","Date","Action","Version1","Version2","Packet")
	val mainTable = new FilteredTable(titles, logs, true, screen, textGraphics) with Cursor
	
	val filters = logs.map((l: List[String]) => l(2)).distinct
	val filterTable: OptionCursor = new OptionTable("Filter By", filters, List(true,true,true,true,true), mainTable, false, screen, textGraphics) with OptionCursor
	var focussedTable: Cursor = mainTable
	var mainTableOffset = 0
	var focussedTableOffset = mainTableOffset

	def start {		
		var state: LoggerState = new MainTableState(this, mainTable, screen)
		
   	mainTable.draw(terminalSize, mainTableOffset)
   	var keyStroke = screen.pollInput();
    while (keyStroke == null || KeyType.F3 != keyStroke.getKeyType) {
      val newSize = screen.doResizeIfNecessary
    	if (newSize != null){
    	  terminalSize = newSize
    	  screen.clear()
    	  mainTable.updateSize
    	  filterTable.updateSize
    	}
    	
    	if(keyStroke != null) {
     	  keyStroke.getKeyType match {
        		case KeyType.ArrowDown => focussedTable.moveCursor(1, textGraphics, focussedTableOffset, terminalSize)
        		case KeyType.ArrowUp => focussedTable.moveCursor(-1, textGraphics, focussedTableOffset, terminalSize)
        		case KeyType.PageDown => focussedTable.moveCursor(terminalSize.getRows-3, textGraphics, focussedTableOffset, terminalSize)
        		case KeyType.PageUp => focussedTable.moveCursor(-(terminalSize.getRows-3), textGraphics, focussedTableOffset, terminalSize)
        		case KeyType.F4 => state.f4
        		case KeyType.Escape => state.esc
        		case KeyType.Enter => state.enter
        		case _ => ()
      	}
     	  state = state.getNextState
     	  mainTable.updateValues
     	  filterTable.updateValues
    	}
    	screen.refresh()
    	mainTable.draw(terminalSize,mainTableOffset)
    	if(mainTableOffset > 0)
    	  focussedTable.draw(terminalSize,0)
    	drawFoot(state.getFoot, 0)
    	screen.refresh()
      // keyStroke = screen.pollInput
    	keyStroke = screen.readInput  // forced to use blocking input reading to decrease significally CPU use
   	}
		screen.close
	}
	
	def drawFoot(commands: List[(String,String)], off: Int) {
	  var offset = off
	  val columns = terminalSize.getColumns()
	  val row = terminalSize.getRows-1
	  commands foreach {
	    case (k,c) =>
	      textGraphics.setForegroundColor(TextColor.ANSI.CYAN)
		    textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT)
		    textGraphics.putString(offset, row, k)
		    offset += k.length
		    textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
		    textGraphics.setBackgroundColor(TextColor.ANSI.CYAN)
		    textGraphics.putString(offset, row, c)
		    offset += c.length
	  }
	  textGraphics.putString(offset, row, " "*(columns-offset))
	}
}