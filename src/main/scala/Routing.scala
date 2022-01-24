package laminarDemo

import upickle.default.{read, write}
import upickle.default.{macroRW, ReadWriter as RW, *}
import urldsl.errors.DummyError
import urldsl.language.QueryParameters
import com.raquo.laminar.api.L.*

def renderMyPage($loginPage: Signal[DependencyExplorerPage], fullAppData: AppDataAndEffects) =
  div(???)

sealed private trait Page

case class DependencyExplorerPage(
                                   targetProject: Option[String],
                                   filterUpToDateProjects: Boolean
                                 ) extends Page:
  def changeTarget(newTarget: String) = copy(targetProject = Some(newTarget))

object DependencyExplorerRouting:
  import upickle.default.{macroRW, ReadWriter as RW, *}
  import com.raquo.waypoint.{param, Route, root, Router, endOfSegments, SplitRender}
  import com.raquo.laminar.api.L

  implicit private val explorerRW: RW[DependencyExplorerPage] = macroRW
  implicit private val rw: RW[Page]                           = macroRW

  private val encodePage
  : DependencyExplorerPage => (Option[String], Option[Boolean]) =
    page => (page.targetProject, Some(page.filterUpToDateProjects))

  private val decodePage
  : ((Option[String], Option[Boolean])) => DependencyExplorerPage = {
    case (targetProject, filterUpToDateProjects) =>
      DependencyExplorerPage(
        targetProject = targetProject,
        filterUpToDateProjects = filterUpToDateProjects.getOrElse(false)
      )
  }

  val params: QueryParameters[(Option[String], Option[Boolean]), DummyError] =
    param[String]("targetProject").? &
      param[Boolean]("filterUpToDateProjects").?

  private val prodRoute =
    Route.onlyQuery[DependencyExplorerPage, (Option[String], Option[Boolean])](
      encode = encodePage,
      decode = decodePage,
      pattern = (root / endOfSegments) ? params
    )

  val router =
    new Router[Page](
      routes =
        List(
          prodRoute
        ),
      getPageTitle = _.toString, // mock page title (displayed in the browser tab next to favicon)
      serializePage = page => write(page)(rw), // serialize page data for storage in History API log
      deserializePage = pageStr => read(pageStr)(rw), // deserialize the above
      routeFallback =
        _ =>
          DependencyExplorerPage(
            targetProject = None,
            filterUpToDateProjects = false
          ),
    )(
      $popStateEvent =
        L.windowEvents.onPopState, // this is how Waypoint avoids an explicit dependency on Laminar
      owner = L.unsafeWindowOwner  // this router will live as long as the window
    )

  import com.raquo.laminar.api.L.HtmlElement

  def splitter(fullAppData: AppDataAndEffects) =
    SplitRender[Page, HtmlElement](router.$currentPage)
      .collectSignal[DependencyExplorerPage](renderMyPage(_, fullAppData))

end DependencyExplorerRouting
import com.raquo.laminar.api.L.Signal
case class AppDataAndEffects(dataSignal: Signal[Option[FullAppData]])
