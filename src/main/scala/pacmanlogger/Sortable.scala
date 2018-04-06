package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

trait Sortable extends Table {
	var sortingIndex = 0
	tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	
	override def updateValues = {
		super.updateValues
		tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	}
	
	override def drawHeader(offset: Int) = {
		super.drawHeader(offset)
		val totalOffset = offset + colWidths.take(sortingIndex).foldLeft(0)((x,y) => x+y)
		val columnTitle = titles(sortingIndex)+" "
		tg.setForegroundColor(TextColor.ANSI.BLACK)
		tg.setBackgroundColor(TextColor.ANSI.CYAN)
		tg.putString(totalOffset, 0, columnTitle)
	}
	
	def sortByIndex(i: Int) = {
		sortingIndex = i
		tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	}
	
	def getTitles = titles
}