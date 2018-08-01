package controllers

import javax.inject._
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
      (JsPath \ "authors").read[String]) (CreateBookSchemaJson.apply _)


  def ApiCreateBook = Action(parse.json) { implicit request =>
    val jsonResult = request.body.validate[CreateBookSchemaJson]
    jsonResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "OK", "message" -> JsError.toJson(errors)))
      },
      data => {
        Ok(library.create(data.title, data.year, data.authors))
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
    Ok(library.delete(res.get))
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
    //Ok(library.allAuthor())
    Ok("89")
  }
}



