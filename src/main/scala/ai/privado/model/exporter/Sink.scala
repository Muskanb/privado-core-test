package ai.privado.model.exporter

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import ai.privado.model.exporter.DataFlowEncoderDecoder._ //Required for DataFlowSubCategoryPathExcerptModel

case class SinkModel(sourceType: String, id: String, name: String, domains: Array[String], apiUrl: Array[String])

case class SinkProcessingModel(sinkId: String, occurrences: List[DataFlowSubCategoryPathExcerptModel])

object SinkEncoderDecoder {

  implicit val sinkModelDecoder: Decoder[SinkModel] = deriveDecoder[SinkModel]
  implicit val sinkModelEncoder: Encoder[SinkModel] = deriveEncoder[SinkModel]

  implicit val sinkProcessingModelDecoder: Decoder[SinkProcessingModel] = deriveDecoder[SinkProcessingModel]
  implicit val sinkProcessingModelEncoder: Encoder[SinkProcessingModel] = deriveEncoder[SinkProcessingModel]

}
