package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Library


@Singleton
class Application @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) with play.api.i18n.I18nSupport{

  def createBook() = Action { implicit request =>
    BookForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(Library.allBooks(), errors)),
      { case (title, year, authors) => Library.create(title, year, authors);
        Redirect(routes.Application.getBook)
      }
    )
  }

  def getBooks = Action { implicit request =>
    Ok(views.html.index(Library.allBooks(), BookForm));
  }

  def getAuthors = Action { implicit request =>
    Ok(views.html.authors(Library.allAuthors()));
  }


  def deleteBook(title: String, year: String, authors: String) = Action { implicit request =>
    Library.delete(title, year, authors);
    Redirect(routes.Application.getBook)
  }

  def updateBook(title: String, year: String, authors: String) = Action { implicit request =>
    Library.update(title, year, authors);
    Redirect(routes.Application.getBook)
  }

  val BookForm = Form(
    tuple (
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
      "Authors" -> nonEmptyText
    )
  )

}
