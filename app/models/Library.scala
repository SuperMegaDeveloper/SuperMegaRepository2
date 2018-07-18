package models


import anorm._

import com.google.inject.Inject
import play.api.db.{DBApi}

class Library @Inject()(
dbApi: DBApi) {


  val DB = dbApi.database("default")


  def allBooks(): List[Book] = {

    DB.withConnection { implicit connection =>
      SQL("SELECT title, year, name FROM (SELECT books.title, books.year, GROUP_CONCAT(name) as name FROM books LEFT JOIN booksAndAuthors ON books.book_id=booksAndAuthors.book_id LEFT JOIN authors ON booksAndAuthors.author_id=authors.author_id GROUP BY title) GROUP BY title;").as(Book.parser.*)

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


  def delete(title: String, year: String): Unit = {
    DB.withConnection { implicit connection =>
      SQL("DELETE from books where title={title} AND year={year}").on(
        'title -> title,
        'year -> year
      ).execute()
    }
  }


  def update(titleOld: String, title: String, year: String, authors: String): Unit = {
    DB.withConnection { implicit connection =>
      SQL("UPDATE books SET title={title}, year={year} WHERE title={titleOld}")
        .on(
          'title -> title,
          'year -> year,
          'titleOld -> titleOld
        ).execute()


      val BOOKId = SQL("SELECT book_id FROM books WHERE title={title}").on(
        'title -> title
      ).as(BOOK_id.parserBookId. *)


      val AUTHORId = SQL("SELECT author_id FROM authors WHERE name={name}").on(
        'name -> authors
      ).as(Author_id.parserAuthorId.*)

      if (AUTHORId.isEmpty) {
        val AUTHORId =
          SQL("INSERT INTO authors SET name={name}")
            .on(
              'name -> authors
            ).executeInsert();

        SQL("UPDATE booksAndAuthors SET author_id={AUTHOR_id} WHERE book_id={BOOK_id}").on(
          'AUTHOR_id -> AUTHORId,
          'BOOK_id -> BOOKId(0).book_id
        ).execute()
      }

      else {
        SQL("UPDATE booksAndAuthors SET author_id={AUTHOR_id} WHERE book_id={BOOK_id}").on(
          'AUTHOR_id -> AUTHORId(0).author_id,
          'BOOK_id -> BOOKId(0).book_id
        ).execute()
      }
    }
  }


  object Library {

  }

}




