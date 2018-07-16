package models
import java.util

import anorm._
import anorm.SqlParser._
import com.google.inject.Inject
import play.db.{DBApi, Database}

class Library @Inject()(
dbApi: DBApi) {



  val DB = dbApi.getDatabase("test1")

  val book = {
    get[String]("title") ~
      get[String]("year") ~
      get[String]("name") map {
      case title ~ year ~ name => Parser(title, year, name)
    }
  }

  def allBooks() = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM (books JOIN booksAndAuthors USING (book_id)) JOIN authors USING (author_id);").as(book *)
    }
  }

  def allAuthors() : List[Parser] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM authors;").as(book *)
    }
  }



    def create(title: String, year: String, authors: String): Unit = {

      DB.withConnection { implicit connection =>
        SQL("insert into books (title,year) values ({title},{year})").on(
          'title -> title,
          'year -> year
        ).executeUpdate()
      }

      DB.withConnection { implicit connection =>
        SQL("insert into authors (name) values ({author})").on(
          'authors -> authors).executeUpdate()
      }
    }

    def delete(title: String, year: String, authors: String) {}

    def update(title: String, year: String, authors: String) {}




}

object Library {

}

