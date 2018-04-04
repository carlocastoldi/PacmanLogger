package pacmanlogger

import com.googlecode.lanterna._
import com.googlecode.lanterna.screen._
import com.googlecode.lanterna.graphics._
import com.googlecode.lanterna.terminal._

class FilteredTable(titles: List[String], tuples: List[List[String]], fullScreen: Boolean, screen: Screen, tg: TextGraphics)
  extends AbstractTable {
  
  var colWidths = new Array[Int](titles.length)
	var terminalSize = screen.getTerminalSize
	var firstRow = 0
  var filter = (t: List[String]) => true
	var filteredRows: List[List[String]] = tuples
	var nRows: Int = screen.getTerminalSize.getRows-2
	var rows: List[List[String]] =  nRows match {
	    case n if n > filteredRows.length =>
	      filteredRows
	    case n if n > (filteredRows.length-firstRow) =>
	      filteredRows.drop(terminalSize.getRows)
	    case _ => filteredRows.drop(firstRow).take(nRows)
	  }
	  
  def getRows = rows
  def getAllRows = filteredRows

  def updateRows =
    rows = nRows match {
	    case n if n > filteredRows.length =>
	      filteredRows
	    case n if n > (filteredRows.length-firstRow) =>
	      filteredRows.drop(terminalSize.getRows)
	    case _ => filteredRows.drop(firstRow).take(nRows)
	  }
  def updateSize = {
    terminalSize = screen.getTerminalSize
    updateRows
  }
  
	def updateValues = {
	  nRows = screen.getTerminalSize.getRows-2
	  filteredRows = tuples.filter(filter)
	  updateRows
	}
	
	override def getScreen = screen
	override def getTextGraphics = tg
        override def getFirstRow = firstRow
	
	def isLastRow = firstRow+nRows == filteredRows.length+1

	override def draw(terminalSize: TerminalSize, offset: Integer) {
		calcColWidths
		tg.setForegroundColor(TextColor.ANSI.BLACK)
		tg.setBackgroundColor(TextColor.ANSI.GREEN)
		drawRow(titles, offset, 0)
		tg.setForegroundColor(TextColor.ANSI.CYAN)
		tg.setBackgroundColor(TextColor.ANSI.DEFAULT)
		val localRows = rows
		localRows.zipWithIndex foreach {
			case(r,i) => drawRow(r, offset, i+1)
		}
	}

	override def drawRow(titles: List[String], column: Int, row: Int) {
	  val columns = terminalSize.getColumns
		var offset = column
		titles.zipWithIndex foreach{
			case(title,i) if(offset+colWidths(i)+1 <= columns) => tg.putString(offset, row, title+" "*(colWidths(i)-title.length+1))
				offset = offset+colWidths(i)
			case(title,i) if offset <= columns =>
				val width = columns-offset
				tg.putString(offset, row, title+" "*(width+1))
				offset = offset+colWidths(i)
			case _ => ()
		}
	  if (fullScreen) tg.putString(offset, row, " "*(columns-offset))
	}
	
	override def scrollRows(n: Int) {
	  val totalLength =  filteredRows.length
	  val rowsLength = nRows
	  if(firstRow+n >= 0 && firstRow+n+rowsLength <= totalLength)
	    firstRow += n
	  else if (firstRow+n >= 0 && firstRow+rowsLength <= totalLength)
	    firstRow = totalLength - rowsLength
	  else if (firstRow > 0 && firstRow+n+rowsLength <= totalLength)
	    firstRow = 0
	}

        override def scrollStart {
          firstRow = 0
        }

        override def scrollEnd {
          val totalLength =  filteredRows.length
          val rowsLength = nRows

          firstRow = totalLength - rowsLength
        }

	def calcColWidths {
	  colWidths = new Array[Int](titles.length)
		val localRows = titles::rows
		localRows foreach{ row =>
         row.zipWithIndex.foreach{
            case(element,column) if element.length >= colWidths(column) =>
               colWidths.update(column,element.length+1)
            case _ => ()
         }
      }
	}
}

import scala.collection.immutable.HashMap

class OptionTable(titles: String, tuples: List[String], options: List[Boolean], filteredT: FilteredTable, fullScreen: Boolean, screen: Screen, tg: TextGraphics)
  extends FilteredTable(List("",titles),
    tuples.zip(options).map(t =>
      t match {
        case (s, true) => List("[X]",s)
        case (s, false) => List("[ ]",s)
      }
    ), fullScreen, screen, tg) {
  
  var settings: HashMap[String,Boolean] = { 
    var m = new HashMap[String,Boolean]
    tuples.zip(options) foreach {
      case (action,option) => m = m.updated(action,option)
    }
    m
  }
  
  override def updateValues = {
    nRows = terminalSize.getRows-2
    filteredRows = {
      settings.toList map { (t: (String,Boolean)) =>
        t match {
        case (s,true) => List("[X]",s)
        case (s,false) => List("[ ]",s)
        }
      }
    }
    updateRows
  }
  
  def switchOption(r: List[String]) = {
    settings.toList foreach {
      case (s,true) if r(1) == s =>
        val values: List[Boolean] = settings.toList.unzip._2
          if(values.indexOf(true) != values.lastIndexOf(true))
            settings = settings.updated(s,false)
      case (s,false) if r(1) == s =>
        settings = settings.updated(s,true)
      case _ => ()
    }
    filteredT.filter = setFilterFunction
  }
  
  def setFilterFunction = {
    t: List[String] =>
      settings.getOrElse(t(2),false)
  }
}
