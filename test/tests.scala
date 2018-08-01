import org.scalatestplus.play.PlaySpec
import models.Library
import org.scalatest.mockito.MockitoSugar
import play.api.db.DBApi
import play.api.libs.json.Json
class tests extends PlaySpec with MockitoSugar {

  val library = new Library(mock[DBApi])
  val books = List(library.Book(1, "book", "1999"))
  val authors = List(library.Author(1, "sam"))
  val links = List(library.Link(1, 1))

  "get authors" should {
    "return valid json" in {
      val result = library.Jsons.allAuthors(books, authors, links)
      result mustBe Json.arr(
        "authors",
        Json.arr(
          Json.obj(
            "name" -> "sam",
            "book" -> Json.arr(
              Json.obj(
                "title" -> "book"
              )
            )
          )
        )
      )
    }
  }

  "get books" should{
    "return valid json" in {
      val result = library.Jsons.allBooks(books, authors, links)
      result mustBe Json.arr(
        "books",
        Json.arr(
          Json.obj(
            "title" -> "book",
            "year" -> "1999",
            "authors" -> Json.arr(
              Json.obj(
                "name" -> "sam"
              )
            )
          )
        )
      )
    }
  }

//  "delete books" should {
//    "return title of book" in {
//      val result = library.delete()
//    }
//  }





}



