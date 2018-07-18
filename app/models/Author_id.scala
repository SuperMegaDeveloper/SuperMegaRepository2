package models

import anorm.SqlParser.get
import anorm.~


case class Author_id(
                    author_id: Int
                  ) {


}

trait parserSqlAuthorId {
  val parserAuthorId = {
    get[Int]("author_id") map {
      case author_id => Author_id(author_id)
    }


  }

}

object Author_id extends parserSqlAuthorId

