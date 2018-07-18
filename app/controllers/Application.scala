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
        BadRequest(views.html.index(library.allBooks(), formWithErrors, DeleteBookForm, UpdateBookForm))
      }, userData => {
        library.create(userData.title, userData.year, userData.authors)
        Redirect(routes.Application.getBooks())
      }
    )

  }


  def getBooks = Action { implicit request =>
    Ok(views.html.index(library.allBooks(), BookForm, DeleteBookForm, UpdateBookForm));
  }

  def getAuthors = Action { implicit request =>
    Ok(views.html.authors(library.allAuthors()));
  }


  def deleteBook = Action { implicit request =>
    DeleteBookForm.bindFromRequest.fold(
      formWithErrorss => {
        BadRequest(views.html.index(library.allBooks(), BookForm, formWithErrorss, UpdateBookForm))
      }, userData => {
        library.delete(userData.title, userData.year)
        Redirect(routes.Application.getBooks())
      }
    )
  }

  def updateBook = Action { implicit request =>
    UpdateBookForm.bindFromRequest.fold(
      formWithErrorss => {
        BadRequest(views.html.index(library.allBooks(),BookForm, DeleteBookForm, formWithErrorss))
      }, userData => {
<<<<<<< HEAD
=======
        println("---------------"+"------------------")
>>>>>>> 7fadd3c348aba0275c02ff55e41576a725bd3510
        library.update(userData.titleOld, userData.title, userData.year, userData.authors)
        Redirect(routes.Application.getBooks())
      }
    )

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
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
    )(deleteform.apply)(deleteform.unapply)
  )

  val UpdateBookForm = Form(
    mapping (
      "titleOld" -> nonEmptyText,
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
      "authors" -> nonEmptyText
    )(updateform.apply)(updateform.unapply)
    )



}

case class form(title: String, year:String, authors: String)

case class deleteform(title: String, year:String)

case class updateform(titleOld: String, title:String, year:String, authors: String)


