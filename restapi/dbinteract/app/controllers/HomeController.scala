package controllers

import models.UserModel.{UsersTable, users}
import javax.inject._
import play.api._
import play.api.mvc._
import slick.jdbc.H2Profile.api._
import slick.dbio.DBIOAction
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import slick.lifted.TableQuery
import models.UserModel.Users
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json._
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Api
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */


  implicit val db = Database.forConfig("h2mem1")
  implicit val writes = Json.writes[Users]
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def read10(tablename: String) = Action { implicit request: Request[AnyContent] => {
    
    Ok(f"${tablename} is selected")

  }
  }

  def insertUserData(id: Int, username: String) = Action { implicit request: Request[AnyContent] => {

    try{  
      val insert = users += Users(id, username)
      val insertAction = db.run(insert)
      val rowCount = Await.result(insertAction, 2.seconds)
      Ok(f"Inserted ${rowCount} rows")
    } catch {
      case e:Exception => Ok(f"Inserted 0 rows")
    }
  }
}

def insertUserPostData = Action { implicit request: Request[AnyContent] => {
    implicit val myValueReads = new Reads[Int] {
      override def reads(json: JsValue) = {
        JsSuccess(json.as[Int])
      }
    }
    try{  
      val postVals = request.body.asJson
      postVals.map {js =>
      val id = (js.\("id")).as[Int]
      val userName = (js.\("username")).as[String]
      val insert = users += Users(id, userName)
      val insertAction = db.run(insert)
      val rowCount = Await.result(insertAction, 2.seconds)
      
      Ok(f"User ${userName} Created")
      }.getOrElse
       {Ok("id and usename must be in request body")}
    } catch {
      case e:Exception => Ok(f"Error:: ${e.toString()} Inserted 0 rows")
    }
  }
}

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid user supplied"),
    new ApiResponse(code = 404, message = "User not found")))
  def getUsersTableData(
    @ApiParam(value = "username") userName: String) = Action { implicit request: Request[AnyContent] => {
    try{
      val result = db.run(users.filter(_.username === userName).result)
      val r = Await.result(result, 2.seconds)
      Ok(Json.toJson(r))
    } catch {
      case e: Exception => Ok(e.toString())
    }
  } 
}
  
  def createSchema() = Action { implicit request: Request[AnyContent] => {
    val action = users.schema.create
    val f = db.run(action)
    val result = Await.result(f, 2.seconds)
    Ok(f"Users is selected")

  }
}
}
