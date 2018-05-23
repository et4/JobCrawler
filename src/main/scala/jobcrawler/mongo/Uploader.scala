package jobcrawler.mongo

import jobcrawler.vacancy._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}


import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Uploader(dbName: String = "jobs", uri: String = "mongodb://127.0.0.1:27017") {

  def upload(vacancies: Seq[VacancyInClearFormat], collectionName: String): Int = {
    val codecRegistry = fromRegistries(fromProviders(
      classOf[VacancyInClearFormat],
      classOf[Salary], classOf[Employer], classOf[Area], classOf[ProfArea], classOf[KeySkill]
    ), DEFAULT_CODEC_REGISTRY)

    val mongoClient: MongoClient = MongoClient(uri)

    val database: MongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(codecRegistry)

    val collection: MongoCollection[VacancyInClearFormat] = database.getCollection(collectionName)

    val totalItemsInCollection = for {
      a <- collection.insertMany(vacancies)
      inCollection <- collection.count()
    } yield inCollection

    val res = Await.result(totalItemsInCollection.toFuture(), Duration.Inf)
    println(s"\n Total: ${res.head}")
    mongoClient.close()
    res.length
  }
}
