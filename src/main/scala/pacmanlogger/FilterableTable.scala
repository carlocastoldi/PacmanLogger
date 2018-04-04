package pacmanlogger

import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

class FilterableTable(titles: List[String], totalTuples: List[List[String]], fullScreen: Boolean, screen: Screen, tg: TextGraphics)
	extends Table(titles, totalTuples, fullScreen, screen, tg) {
	
	var filter = (t: List[String]) => true
	tuples = totalTuples.filter(filter)
	
	override def updateValues = {
		super.updateValues
		tuples = totalTuples.filter(filter)
	}
	
	def setFilter(f: List[String] => Boolean) = {
		filter = f
		tuples = totalTuples.filter(filter)
	}
}