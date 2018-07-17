package models


import anorm._

import com.google.inject.Inject
import play.api.db.{DBApi}

class Library @Inject()(
dbApi: DBApi) {


  val DB = dbApi.database("default")


  def allBooks(): List[Book] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM books LEFT JOIN booksAndAuthors ON books.book_id=booksAndAuthors.book_id LEFT JOIN authors ON booksAndAuthors.author_id=authors.author_id;").as(Book.parser.*)
    }
  }

  def allAuthors(): List[Book] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM authors;").as(Book.parser.*)
    }
  }


  def create(title: String, year: String, authors: String): Unit = {

    DB.withConnection { implicit connection =>

      val book_id = SQL("insert into books SET title={title}, year={year}").on(
        'title -> title,
        'year -> year
      ).executeInsert()


      val namesOfAuthors: Array[String] = authors.split(", ")

      for (i <- 0 until namesOfAuthors.length) {
        val author_id = SQL("insert into authors SET name={name}").on(
          'name -> namesOfAuthors(i)).executeInsert()


        SQL("INSERT INTO booksAndAuthors SET book_id={book_id}, author_id={author_id}").on(
          'book_id -> book_id,
          'author_id -> author_id
        ).execute
      }

    }

  }


  def delete(title: String, year: String, authors: String): Unit = {
    DB.withConnection { implicit connection =>
      SQL("DELETE title, year from books where title={title}, year={year}").on(
        'title -> title,
        'year -> year
      ).execute()

    }
  }

  def update(title: String, year: String, authors: String): Unit = {

  }


  object Library {

  }

}

