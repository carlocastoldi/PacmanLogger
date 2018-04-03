package PacmanLogger

case class PktVersion(v1: String, v2: String) {
  def this(v: String) {
    this(v, "")
  }
  
  override def toString: String = {
    return "%s,%s".format(v1,v2)
  }
}