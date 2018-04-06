package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.graphics._

trait Cursor extends AbstractTable {
	var cursorAbsolutePos = 0
	var cursorRelativePos = 0
	var rows_ = getRows
	var nRows_ = rows_.length

	abstract override def draw(offset: Int) {
		super.draw(offset)
		if (terminalSize.getRows - 3 < cursorRelativePos)
			cursorRelativePos = terminalSize.getRows - 3
		drawCursor(offset)
	}

	def moveCursor(n: Int, offset: Int) {
		rows_ = getRows
		nRows_ = rows_.length
		(cursorRelativePos + n) match {
			case i if i >= 0 && i < nRows_ =>
				delCursor(offset)
				cursorRelativePos += n
				drawCursor(offset)
			case i if !isLastRow =>
				var firstRow_ = getFirstRow
				scrollRows(n)

				delCursor(offset)
				cursorRelativePos += n - (getFirstRow - firstRow_)

				if (cursorRelativePos < 0) {
					cursorRelativePos = 0
				}

				if (cursorRelativePos > nRows_ - 1) {
					cursorRelativePos = nRows_ - 1
				}

				drawCursor(offset)
				draw(offset)
			case _ => ()
		}
	}

	def moveCursorStart(offset: Int) {
		delCursor(offset)
		cursorRelativePos = 0
		drawCursor(offset)
		scrollStart
		draw(offset)
	}

	def moveCursorEnd(offset: Int) {
		rows_ = getRows
		nRows_ = rows_.length
		delCursor(offset)
		cursorRelativePos = nRows_ - 1
		drawCursor(offset)
		scrollEnd
		draw(offset)
	}

	def delCursor(offset: Int) {
		updateValues
		rows_ = getRows
		nRows_ = rows_.length
		tg.setForegroundColor(TextColor.ANSI.CYAN)
		tg.setBackgroundColor(TextColor.ANSI.DEFAULT)
		nRows_ match {
//			case 0  => // Crashes!
			case n if n <= cursorRelativePos => cursorRelativePos = n-1
			case _ => ()
		}
		drawRow(rows_(cursorRelativePos), offset, cursorRelativePos + 1)
	}

	def drawCursor(offset: Int) {
		updateValues
		rows_ = getRows
		nRows_ = rows_.length
		tg.setForegroundColor(TextColor.ANSI.BLACK)
		tg.setBackgroundColor(TextColor.ANSI.CYAN)
		nRows_ match {
//			case 0  => // Crashes!
			case n if n <= cursorRelativePos => cursorRelativePos = n-1
			case _ => ()
		}
		drawRow(rows_(cursorRelativePos), offset, cursorRelativePos + 1)
	}
}

trait OptionCursor extends OptionTable with Cursor {
	def click = switchOption(rows_(cursorRelativePos))
}
