package jobcrawler.processing

import jobcrawler.vacancy.VacancyInClearFormat
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.ml.clustering._

class Clustering(vacancies: Seq[VacancyInClearFormat]) {

  val spark = SparkSession.builder()
    .appName("Crawler")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._
  import org.apache.spark.ml.feature._

  {
    spark.sparkContext.setLogLevel("ERROR")
  }

  def kMeans(): Unit = {
    val vacanciesDS = getVacanciesDS()

    val tok = new RegexTokenizer()
      .setInputCol("description")
      .setOutputCol("tokens")
      .setPattern("\\W+")

    val remover = new StopWordsRemover()
      .setStopWords(Array[String]("quot"))
      .setInputCol("tokens")
      .setOutputCol("filtered")

    val hashTF = new HashingTF()
      .setInputCol("filtered")
      .setOutputCol("vectorized")

    //    val idf = new IDF().
    //      setInputCol("vectorized").
    //      setOutputCol("features")

    val word2Vec = new Word2Vec()
      .setInputCol("filtered")
      .setOutputCol("features")

    val pipeline = new Pipeline()
      .setStages(Array(tok, remover, hashTF, word2Vec))

    val features = pipeline.fit(vacanciesDS).transform(vacanciesDS)
    features.select('description, 'features).show(false)
    val bestPair = findBestSilhouettes(features)
    val predictions = getKMeansPredictions(features, bestPair._1)
    println(s"Best number of clusters: ${bestPair._1}")
    println(s"Silhouette metric for this number of clusters: ${bestPair._2}")
    (0 until bestPair._1).foreach(k => showElementsFromCluster(predictions, k))
  }

  private def findBestSilhouettes(features: DataFrame): (Int, Double) = {
    val silhouettes = new collection.mutable.ArrayBuffer[(Int, Double)]

    for (k <- 2 to 15) {
      val predictions = getKMeansPredictions(features, k)
      val evaluator = new ClusteringEvaluator()
      val silhouette = evaluator.evaluate(predictions)
      silhouettes.append((k, silhouette))
    }

    silhouettes.maxBy(_._2)
  }

  private def getVacanciesDS(): Dataset[VacancyInClearFormat] = {
    vacancies.toDF("id", "name", "salaryFrom", "salaryTo", "currency",
      "description", "specializations", "employer", "area").as[VacancyInClearFormat]
  }

  private def getKMeansPredictions(features: DataFrame, k: Int): DataFrame = {
    val kMeans = new KMeans().setK(k)
    val kmModel = kMeans.fit(features)
    kmModel.transform(features)
  }

  private def showElementsFromCluster(predictions: DataFrame, k: Int): Unit = {
    predictions.createOrReplaceTempView("predictions")
    println(s"Elements in $k cluster")
    predictions.select("description", "prediction").where(s"prediction = $k").show(false)
  }
}
