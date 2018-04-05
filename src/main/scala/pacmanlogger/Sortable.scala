package pacmanlogger

import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

trait Sortable extends Table {
	var sortingIndex = 0
	tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	
	override def updateValues = {
		super.updateValues
		tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	}
	
	def sortByIndex(i: Int) = {
		sortingIndex = i
		tuples = tuples.sortWith{_(sortingIndex)<_(sortingIndex)}
	}
	
	def getTitles = titles
}