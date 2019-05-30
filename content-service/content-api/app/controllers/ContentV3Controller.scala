package controllers

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.routing.FromConfig
import javax.inject.{Inject, Singleton}
import org.ekstep.actor.ContentActor
import org.ekstep.common.dto.Response
import org.ekstep.commons.{APIIds, Request}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class ContentV3Controller @Inject()(system: ActorSystem) extends BaseController {

  val contentActor = system.actorOf(Props(ContentActor).withRouter(FromConfig), name = "contentActor")


  def read(identifier: String, mode: Option[String], fields: Option[List[String]]): Action[AnyContent] = Action.async {
    implicit request =>
      val result = ask(contentActor, Request(APIIds.READ_CONTENT, None, Some(Map("identifier" -> identifier, "objectType" ->
        "Content", "mode" -> mode.getOrElse(""), "fields" -> fields.getOrElse(List())))))
        .mapTo[Response]
      result.map(response => sendResponse(response))
  }
}

