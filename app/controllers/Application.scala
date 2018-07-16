package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Library


@Singleton
class Application @Inject()(cc: ControllerComponents, library: Library)
  extends AbstractController(cc) with play.api.i18n.I18nSupport{


  def createBook() = Action { implicit request =>
    BookForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(library.allBooks(), errors)),
      { case (title, year, authors) => library.create(title, year, authors);
        Redirect(routes.Application.getBooks)
      }
    )
  }

  def getBooks = Action { implicit request =>
    Ok(views.html.index(library.allBooks(), BookForm));
  }

  def getAuthors = Action { implicit request =>
    Ok(views.html.authors(library.allAuthors()));
  }


  def deleteBook(title: String, year: String, authors: String) = Action { implicit request =>
    library.delete(title, year, authors);
    Redirect(routes.Application.getBooks)
  }

  def updateBook(title: String, year: String, authors: String) = Action { implicit request =>
    library.update(title, year, authors);
    Redirect(routes.Application.getBooks)
  }

  val BookForm = Form(
    tuple (
      "title" -> nonEmptyText,
      "year" -> nonEmptyText,
      "Authors" -> nonEmptyText
    )
  )

}

