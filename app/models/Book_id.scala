package models

import anorm.SqlParser.get
import anorm.~


case class BOOK_id(
                 book_id: Int
               ) {


}

trait parserSqlBOOK_id {
  val parserBookId = {
      get[Int]("book_id") map {
      case book_id => BOOK_id(book_id)
    }


  }

}

object BOOK_id extends parserSqlBOOK_id
