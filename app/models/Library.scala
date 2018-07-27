package models


import java.lang.ProcessBuilder.Redirect
import java.util

import anorm._
import com.google.inject.Inject
import play.api.db.DBApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.shaded.ahc.io.netty.util.concurrent.Future
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import models.TableBooks

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Library @Inject() (dbApi: DBApi) {

  val db = Database.forConfig("slick-h2")

  val tbBooks = TableQuery[TableBooks]
  val tbAuthors = TableQuery[TableAuthors]
  val tbBooksAndAuthors =  TableQuery[TableBooksAndAuthors]
  db.run((tbBooks.schema).create)
  db.run((tbAuthors.schema).create)
  db.run((tbBooksAndAuthors.schema).create)

  def allBooks() = {



    val listbook = Await.result(db.run(tbBooks.map(c => (c.title, c.year, c.book_id)).result), Duration.Inf)
    val listauthors = Await.result(db.run(tbAuthors.map(c => (c.name, c.author_id)).result), Duration.Inf)
    val listid = Await.result(db.run(tbBooksAndAuthors.map(c => (c.book_id, c.author_id)).result), Duration.Inf)

    var list:mutable.MutableList[List[Any]] = new mutable.MutableList;


    for(i <- 0 until listbook.length) {
      var names:String = "";
      list += List(listbook(i)._1, listbook(i)._2, names)


      for(j <- 0 until listid.length) {
        for(l <- 0 until listauthors.length) {
          if(listbook(i)._3 == listid(j)._1 && listauthors(l)._2 == listid(j)._2) {
            names = list.get(i).get(2) + listauthors(l)._1+" "
            list.update(i, List(listbook(i)._1, listbook(i)._2, names))
          }
        }
      }
    }
    list.toList
  }

  def allAuthors() = {
//    DB.withConnection { implicit connection =>
//      SQL("SELECT * FROM authors;").as(Book.parser.*)
//    }
  }



  def create(title: String, year: String, authors: String) = {
    val ListAuthors = authors.split(", ")
    var DBauthors = Await.result(db.run(tbAuthors.map(c => c.name).result), Duration.Inf)
    Await.result(db.run(tbBooks.map(c => (c.title, c.year)) += (title, year)), Duration.Inf)
    val bookId = Await.result(db.run(tbBooks.filter(c => c.title === title).result), Duration.Inf).map(c => c._1)

    for(i <- 0 until ListAuthors.length) {
      if(DBauthors.isEmpty){
        Await.result(db.run(tbAuthors.map(c => c.name) += ListAuthors(i)), Duration.Inf)
        DBauthors = Await.result(db.run(tbAuthors.map(c => c.name).result), Duration.Inf)
      }
        if (DBauthors.contains(ListAuthors(i))) {
          println("author already exists")
        } else {
          Await.result(db.run(tbAuthors.map(c => c.name) += ListAuthors(i)), Duration.Inf)
        }
        val authorId = Await.result(db.run(tbAuthors.filter(c => c.name === ListAuthors(i)).result), Duration.Inf).map(c => c._1).head

        val insertID = DBIO.seq(tbBooksAndAuthors.map(c => (c.book_id, c.author_id)) += (bookId(0), authorId))
        Await.result(db.run(insertID.transactionally), Duration.Inf)


    }
  }



  def delete(title: String): Unit = {

  val bookId = Await.result(db.run(tbBooks.filter(c => c.title === title).result), Duration.Inf).map(c => c._1)
  Await.result(db.run(tbBooks.filter(c => c.title === title).delete), Duration.Inf)
  Await.result(db.run(tbBooksAndAuthors.filter(c => c.book_id === bookId(0)).delete), Duration.Inf)

  }


  def update(titleOld: String, authorOld: String, title: String, year: String, authors: String): Unit = {

    val names = authors.split(", ")
    val oldNames = authorOld.split(", ")


    for (i <- 0 until names.length) {

      Await.result(db.run(tbBooks.filter(_.title === titleOld).map(c => (c.title, c.year)).update((title, year))), Duration.Inf)
      val authorId = Await.result(db.run(tbAuthors.filter(c => c.name === names(i)).result), Duration.Inf).map(c => c._1)
      val bookId = Await.result(db.run(tbBooks.filter(c => c.title === title).result), Duration.Inf).map(c => c._1)
      val oldAuthorId = Await.result(db.run(tbAuthors.filter(c => c.name === oldNames(i)).result), Duration.Inf).map(c => c._1)
      if (authorId.isEmpty) {
          Await.result(db.run(tbAuthors.map(c => c.name) += names(i)), Duration.Inf)

          val newAuthorId = Await.result(db.run(tbAuthors.filter(c => c.name === names(i)).result), Duration.Inf).map(c => c._1).head


          Await.result(db.run(tbBooksAndAuthors.filter(c => c.author_id === oldAuthorId(i) && c.book_id === bookId(0)).map(c => (c.book_id, c.author_id)).update((bookId(0), newAuthorId))), Duration.Inf)

      } else {

        val insertID = DBIO.seq(tbBooksAndAuthors.filter(c => c.author_id === oldAuthorId(i) && c.book_id === bookId(0)).map(c => (c.book_id, c.author_id)).update((bookId(0), authorId(0))))
        Await.result(db.run(insertID.transactionally), Duration.Inf)
      }
    }
  }


  object Library {

  }

}




