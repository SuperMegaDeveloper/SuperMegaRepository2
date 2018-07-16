package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Library(title: String, year: String, authors: String)

object Library {

  def allBooks(): List[Library] = {
    implicit c =>
      SQL("SELECT * FROM (books JOIN booksAndAuthors USING (book_id)) JOIN authors USING (author_id);").as(task *)
  }

  def allAuthors(): List[Library] = {
  }

  def create(title: String, year: String, authors: String): Unit = {

    DB.withConnection { implicit c =>
      SQL("insert into books (title,year) values ({title},{year})").on(
        'title -> title,
        'year -> year
      ).executeUpdate()
    }

    DB.withConnection { implicit c =>
      SQL("insert into authors (name) values ({author})").on(
        'authors -> authors
      ).executeUpdate()
    }
  }

  def delete(title: String, year: String, authors: String) {}

  def update(title: String, year: String, authors: String) {}

  val task = {
    get[Long]("title") ~
      get[String]("year") ~
      get[String]("name") map {
      case id~label~who~mytime~ready => Task(id, label, who, mytime, ready)
    }
  }

}