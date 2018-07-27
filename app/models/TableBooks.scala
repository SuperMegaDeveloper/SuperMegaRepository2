package models

import slick.jdbc.H2Profile.api._
class TableBooks(tag: Tag) extends Table[(Int, String, String)](tag, "books") {

    def book_id = column[Int]("book_id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def year = column[String]("year")
    def *  = (book_id, title, year)
}


