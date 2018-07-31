package controllers

import javax.inject._
import play.api.data._
import play.api.data.Forms._
import models.Library
import play.api.libs.json.JsPath
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

@Singleton
class Application @Inject()(cc: ControllerComponents, library: Library)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {




  case class CreateBookSchemaJson(title: String, year: String, authors: String)
  object CreateBookSchemaJson {
    var list: List[CreateBookSchemaJson] = {
      List(
        CreateBookSchemaJson("", "", "")
      )
    }
  }



  implicit val createBookReads: Reads[CreateBookSchemaJson] = (
    (JsPath \ "title").read[String] and
      (JsPath \ "year").read[String] and
      (JsPath \ "authors").read[String])(CreateBookSchemaJson.apply _)




  def ApiCreateBook = Action(parse.json) { implicit request =>
    val jsonResult = request.body.validate[CreateBookSchemaJson]
    jsonResult.fold(
      errors => {
        BadRequest(Json.obj("status" ->"OK", "message" -> JsError.toJson(errors)))
      },
      data => {
        library.create(data.title, data.year, data.authors)
        Ok("status code 200")
      }
    )
  }




  def ApiGetBook = Action {
    Ok(library.allBooks())
  }


def ApiDeleteBook = Action(parse.json) { implicit request =>

  val res = for {
    title <- (request.body \ "title").asOpt[String]
  } yield title
  library.delete(res.get)
  Ok("status code 200")
}


  def ApiUpdateBook = Action(parse.json) { implicit request =>
    val res = for {
      old_title <- (request.body \ "Old_title").asOpt[String]
      old_authors <- (request.body \ "Old_author").asOpt[String]
      new_title <- (request.body \ "New_title").asOpt[String]
      new_year <- (request.body \ "New_year").asOpt[String]
      new_authors <- (request.body \ "New_authors").asOpt[String]
    } yield (old_title, old_authors, new_title, new_year, new_authors)
    library.update(res.map(c => c._1).get, res.map(c => c._2).get, res.map(c => c._3).get, res.map(c => c._4).get, res.map(c => c._1).get)
    Ok("status code 200")
  }


def ApiGetAuthors = Action {
  Ok(library.allAuthor())
}
















  def createBook = Action { implicit request =>
//    BookForm.bindFromRequest.fold(
//
//      formWithErrors => {
//        BadRequest(views.html.index(formWithErrors, DeleteBookForm, UpdateBookForm, library.allBooks()))
//      }, userData => {
//        library.create(userData.title, userData.year, userData.authors)
//
//      }
//    )
    Redirect(routes.Application.getBooks())

  }



  def getBooks = Action { implicit request =>
    //Ok(views.html.index(BookForm, DeleteBookForm, UpdateBookForm, library.allBooks()));
    Ok("324")
  }

  def getAuthors = Action { implicit request =>
    //Ok(views.html.index(BookForm, DeleteBookForm, UpdateBookForm, library.allBooks()));
    Ok("234")
  }


  def deleteBook = Action { implicit request =>
//    DeleteBookForm.bindFromRequest.fold(
//      formWithErrorss => {
//        BadRequest(views.html.index(BookForm, formWithErrorss, UpdateBookForm, library.allBooks()))
//      }, userData => {
//        library.delete(userData.title)
//      }
//    )
    Redirect(routes.Application.getBooks())
  }

  def updateBook = Action { implicit request =>
//    UpdateBookForm.bindFromRequest.fold(
//      formWithErrorss => {
//        BadRequest(views.html.index(BookForm, DeleteBookForm, formWithErrorss, library.allBooks()))
//      }, userData => {
//        library.update(userData.titleOld, userData.authorOld, userData.title, userData.year, userData.authors)
//      }
//    )
    Redirect(routes.Application.getBooks())
  }

  val BookForm = Form(
    mapping (
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
      "authors" -> nonEmptyText
    )(form.apply)(form.unapply)
  )

  val DeleteBookForm = Form(
    mapping (
      "title" -> nonEmptyText
    )(deleteform.apply)(deleteform.unapply)
  )

  val UpdateBookForm = Form(
    mapping (
      "Old_title" -> nonEmptyText,
      "Old_author" -> nonEmptyText,
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
      "authors" -> nonEmptyText
    )(updateform.apply)(updateform.unapply)
  )



}

case class form(title: String, year:String, authors: String)

case class deleteform(title: String)

case class updateform(titleOld: String, authorOld: String, title:String, year:String, authors: String)


