package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Library


@Singleton
class Application @Inject()(cc: ControllerComponents, library: Library)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {


  def createBook = Action { implicit request =>
    BookForm.bindFromRequest.fold(

      formWithErrors => {
        BadRequest(views.html.index(formWithErrors, DeleteBookForm, UpdateBookForm, library.allBooks()))
      }, userData => {
        library.create(userData.title, userData.year, userData.authors)
        Redirect(routes.Application.getBooks())
      }
    )

  }


  def getBooks = Action { implicit request =>
    Ok(views.html.index(BookForm, DeleteBookForm, UpdateBookForm, library.allBooks()));
  }

  def getAuthors = Action { implicit request =>
    Ok(views.html.index(BookForm, DeleteBookForm, UpdateBookForm, library.allBooks()));
  }


  def deleteBook = Action { implicit request =>
    DeleteBookForm.bindFromRequest.fold(
      formWithErrorss => {
        BadRequest(views.html.index(BookForm, formWithErrorss, UpdateBookForm, library.allBooks()))
      }, userData => {
        library.delete(userData.title)
      }
    )
    Redirect(routes.Application.getBooks())
  }

  def updateBook = Action { implicit request =>
    UpdateBookForm.bindFromRequest.fold(
      formWithErrorss => {
        BadRequest(views.html.index(BookForm, DeleteBookForm, formWithErrorss, library.allBooks()))
      }, userData => {
        library.update(userData.titleOld, userData.authorOld, userData.title, userData.year, userData.authors)
      }
    )
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


