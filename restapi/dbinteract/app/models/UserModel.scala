package models
import slick.jdbc.H2Profile.api._
//import slick.api._

//case class Usertable(username: String, id: LongInt)

// object TestTable {
    
//     def validateData(username: String, id: Int): Boolean {
//         true
//             } 
// }
object UserModel {

case class Users(id: Int, username: String)
class UsersTable(tag:Tag) extends Table[Users](tag, "USERS") {
    def id = column[Int]("id", O.PrimaryKey)
    def username = column[String]("username")
    def * = (id, username) <> (Users.tupled, Users.unapply)
}

val users = TableQuery[UsersTable]
}


