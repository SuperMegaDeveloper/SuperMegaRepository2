package models

import anorm.SqlParser.get
import anorm.~


case class Book(
     title: String,
     year: String,
     name: String
               ) {


}

trait parserSql {
  val parser = {
    get[String]("title") ~
      get[String]("year") ~
      get[String]("name") map {
      case title ~ year ~ name => Book(title, year, name)
    }


  }

}

object Book extends parserSql