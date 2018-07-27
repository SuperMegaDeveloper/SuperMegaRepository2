package models
import slick.jdbc.H2Profile.api._
class TableAuthors(tag: Tag) extends Table[(Int, String)](tag, "authors") {
    def author_id = column[Int]("author_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (author_id, name)
}
