package models
import slick.jdbc.H2Profile.api._
class TableBooksAndAuthors(tag: Tag) extends Table[(Int, Int, Int)](tag, "booksAndAuthors") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def book_id = column[Int]("book_id")
  def author_id = column[Int]("author_id")
  def * = (id, book_id, author_id)
}
