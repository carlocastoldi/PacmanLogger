package pacmanlogger

import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._

trait Filterable extends Table {
	val totalTuples = tuples
	var filter = (t: List[String]) => true
	tuples = totalTuples.filter(filter)
	
	override def updateValues = { // Filterable must be the first to be called on a updateValues call right after Table
		super.updateValues
		tuples = totalTuples.filter(filter)
	}
	
	def setFilterFunction(f: List[String] => Boolean) = {
		filter = f
		tuples = totalTuples.filter(filter)
	}
}