package models
import com.google.inject.Inject
import play.api.db.DBApi
import slick.jdbc.H2Profile.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.json.{JsValue, Json}



class Library @Inject() (dbApi: DBApi) {

  val db = Database.forConfig("slick-h2")

  val tbBooks = TableQuery[TableBooks]
  val tbAuthors = TableQuery[TableAuthors]
  val tbBooksAndAuthors =  TableQuery[TableBooksAndAuthors]
  db.run(tbBooks.schema.create)
  db.run(tbAuthors.schema.create)
  db.run(tbBooksAndAuthors.schema.create)

  case class Book(id: Int, title: String, year: String)
  case class Author(id: Int, name: String)
  case class Link(bookId: Int, authorId: Int)




  trait Jsons {

    def getValuesFromDB = {
      val listbook = Await.result(db.run(tbBooks.map(c => (c.title, c.year, c.book_id)).result), Duration.Inf)
      val listauthors = Await.result(db.run(tbAuthors.map(c => (c.name, c.author_id)).result), Duration.Inf)
      val listid = Await.result(db.run(tbBooksAndAuthors.map(c => (c.book_id, c.author_id)).result), Duration.Inf)

      val books:List[Book] = listbook.flatMap {
        case (title, year, bookId) => Some(Book(bookId, title, year))
        case _ => None
      }.toList

      val authors: List[Author] = listauthors.flatMap {
        case (name, authorId) => Some(Author(authorId, name))
        case _ => None
      }.toList

      val links: List[Link] = listid.flatMap {
        case (bookId, authorId) => Some(Link(bookId, authorId))
        case _ => None
      }.toList
      List(books, authors, links)
    }

    def allBooks(books: List[Book], authors: List[Author], links: List[Link]): JsValue = {

      val jsonList = books.map(
        book => {
          Json.obj(
            "title" -> book.title,
            "year" -> book.year,
            "authors" -> Json.toJson({
              val authorsIds = links.filter(_.bookId == book.id).map(_.authorId)
              authors.filter(
                author => authorsIds.contains(author.id)
              ).map(
                author => Json.obj("name" -> author.name)
              )
            }
            )
          )
        }
      )
      Json.toJson("books" -> jsonList)
    }


    def allAuthors(books: List[Book], authors: List[Author], links: List[Link]): JsValue = {
      val jsonList = authors.map(
        author => {
          Json.obj(
          "name" -> author.name,
          "book" -> Json.toJson({
            val bookIds = links.filter(_.authorId == author.id).map(_.bookId)
            books.filter(
              book => bookIds.contains(book.id)
            ).map(book => Json.obj("title" -> book.title))
          })
        )
        }
      )
      Json.toJson("authors" -> jsonList)
    }
  }

  object Jsons extends Jsons


  def allBooks() = {

    val b: List[Book] = Jsons.getValuesFromDB(0).asInstanceOf[List[Book]]
    val a: List[Author] = Jsons.getValuesFromDB(1).asInstanceOf[List[Author]]
    val l: List[Link] = Jsons.getValuesFromDB(2).asInstanceOf[List[Link]]

     Jsons.allBooks(b, a, l)
  }



   def allAuthor() = {
     val b: List[Book] = Jsons.getValuesFromDB(0).asInstanceOf[List[Book]]
     val a: List[Author] = Jsons.getValuesFromDB(1).asInstanceOf[List[Author]]
     val l: List[Link] = Jsons.getValuesFromDB(2).asInstanceOf[List[Link]]
     Jsons.allAuthors(b, a, l)
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
        val insertID = DBIO.seq(tbBooksAndAuthors.map(c => (c.book_id, c.author_id)) += (bookId.head, authorId))
        Await.result(db.run(insertID.transactionally), Duration.Inf)
    }
    Json.obj("created" -> title)
  }


  def delete(title: String) = {

  val bookId = Await.result(db.run(tbBooks.filter(c => c.title === title).result), Duration.Inf).map(c => c._1)
  Await.result(db.run(tbBooks.filter(c => c.title === title).delete), Duration.Inf)
  Await.result(db.run(tbBooksAndAuthors.filter(c => c.book_id === bookId.head).delete), Duration.Inf)
  Json.obj("Deleted" -> title)

  }


  def update(titleOld: String, authorOld: String, title: String, year: String, authors: String): Unit = {

    val names = authors.split(", ")
    val oldNames = authorOld.split(", ")


    for (i <- 0 until names.length) {
      val oldAuthorId = Await.result(db.run(tbAuthors.filter(c => c.name === oldNames(i)).result), Duration.Inf).map(c => c._1)
      Await.result(db.run(tbBooks.filter(_.title === titleOld).map(c => (c.title, c.year)).update((title, year))), Duration.Inf)
      val authorId = Await.result(db.run(tbAuthors.filter(c => c.name === names(i)).result), Duration.Inf).map(c => c._1)
      val bookId = Await.result(db.run(tbBooks.filter(c => c.title === title).result), Duration.Inf).map(c => c._1)

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



}


object Library




