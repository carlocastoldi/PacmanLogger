package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.graphics._

trait Cursor extends AbstractTable {
  var cursorAbsolutePos = 0
	var cursorRelativePos = 0
	val screen = getScreen
	val tg = getTextGraphics
	var rows_ = getRows
	var nRows_ = rows_.length

	abstract override def draw(terminalSize: TerminalSize, offset: Integer) {
    super.draw(terminalSize, offset)
    if(terminalSize.getRows-3 < cursorRelativePos) 
      cursorRelativePos = terminalSize.getRows-3
    drawCursor(offset)
  }
	
  def moveCursor(n: Int, tg: TextGraphics, offset: Int, terminalSize: TerminalSize) {
    rows_ = getRows
    nRows_ = rows_.length
    (cursorRelativePos+n) match {
      case i if i >= 0 && i < nRows_ =>
        delCursor(offset)
    		cursorRelativePos += n
    		drawCursor(offset)
      case i if !isLastRow =>
        scrollRows(n)
        draw(terminalSize, offset)
      case _ => ()
    }
  }
  
  def delCursor(offset: Int) {
    updateValues
    rows_ = getRows
    nRows_ = rows_.length
    tg.setForegroundColor(TextColor.ANSI.CYAN)
    tg.setBackgroundColor(TextColor.ANSI.DEFAULT)
    drawRow(rows_(cursorRelativePos), offset, cursorRelativePos+1)
  }
  
  def drawCursor(offset: Int) {
    updateValues
    rows_ = getRows
    nRows_ = rows_.length
    tg.setForegroundColor(TextColor.ANSI.BLACK)
    tg.setBackgroundColor(TextColor.ANSI.CYAN)
    drawRow(rows_(cursorRelativePos), offset, cursorRelativePos+1)
  }
}

trait OptionCursor extends OptionTable with Cursor {
  def getSelectedRow = rows_(cursorRelativePos)
}