package jobcrawler.mongo

import jobcrawler.vacancy._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Downloader(dbName: String = "jobs", uri: String = "mongodb://127.0.0.1:27017") {

  def load(collectionName: String): Seq[VacancyInClearFormat] = {
    val codecRegistry = fromRegistries(fromProviders(
      classOf[VacancyInClearFormat], classOf[Salary],
      classOf[Employer], classOf[Area], classOf[ProfArea], classOf[KeySkill]
    ), DEFAULT_CODEC_REGISTRY)

    val mongoClient: MongoClient = MongoClient(uri)

    val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)

    val collection: MongoCollection[VacancyInClearFormat] = database.getCollection(collectionName)

    val res = Await.result(collection.find().toFuture(), Duration.Inf)
    mongoClient.close()
    res
  }
}
