package laminarDemo

import org.scalajs.dom
import com.raquo.laminar.api.L.*

import scala.concurrent.Future

object LaminarPresentation {
  def main(args: Array[String]): Unit = {
    def rootElement(fullAppData: AppDataAndEffects) =
      div("Fresh Laminar app",
        child <-- fullAppData.dataSignal.map {
          case Some(value) => "We have project data: " + value
          case None => "No project data yet"
        }
      )
    val containerNode = dom.document.querySelector("#laminarAppContainer")
    containerNode.innerHTML = ""
    render(containerNode, rootElement(AppDataAndEffects(Signal.fromFuture(projectDataFromServer()))))
    println("Mounted application")
  }
}

case class Project(
                  name: String
                  )

case class ProjectMetaData(
                      project: Project,
                      dependencies: List[Project]
                      )

case class FullAppData(
                      projects: Seq[ProjectMetaData]
                      )

def projectDataFromServer(): Future[FullAppData] =
  Future.successful(
    FullAppData(
    Seq(
      ProjectMetaData(
        Project("zio"),
        dependencies = List.empty
      ),
      ProjectMetaData(
        Project("zio-json"),
        dependencies = List(
          Project("zio")
        )
      ),
    )
    )
  )


