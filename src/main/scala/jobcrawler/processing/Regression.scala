package jobcrawler.processing

import jobcrawler.vacancy.VacancyInClearFormat
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.regression.GBTRegressor
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class Regression(vacancies: Seq[VacancyInClearFormat]) {

  val spark = SparkSession.builder()
    .appName("Crawler")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._
  import org.apache.spark.ml.feature._

  {
    spark.sparkContext.setLogLevel("ERROR")
  }


  def gradientBoostedTree(): Unit = {
    val vacanciseDS: DataFrame = getVacanciesDS()
    val trainingData = vacanciseDS.filter("salaryFrom is not null")
    val dataWithEmptySalaryFrom = vacanciseDS.filter("salaryFrom is null")

    val gbt = new GBTRegressor()
      .setLabelCol("salaryFrom")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)


    val model = gbt.fit(trainingData)

    val predictions = model.transform(dataWithEmptySalaryFrom)

    predictions.show()

    dataWithEmptySalaryFrom.show()

    //    predictions.select("prediction", "label", "features").show(5)
  }

  def showNotNullInfo(): Unit = {
    val vacanciseDS: DataFrame = getVacanciesDS()
    println(vacanciseDS.select("*").where("salaryFrom is not null").count())
    vacanciseDS.filter("salaryFrom is not null").show()
    println(vacanciseDS.select("*").where("salaryFrom is null").count())
    vacanciseDS.filter("salaryFrom is null").show()
  }

  private def getVacanciesDS(): DataFrame = {
    val df = vacancies.toDF("id", "name", "salaryFrom", "salaryTo", "currency",
      "description", "specializations", "employer", "area").where("currency = 'RUR'")
      .as[VacancyInClearFormat]

    val descriptionTokens = new RegexTokenizer()
      .setToLowercase(true)
      .setInputCol("description")
      .setOutputCol("descriptionTokens")
      .setPattern("\\s")

    val descriptionRemover = new StopWordsRemover()
      .setStopWords(scala.io.Source.fromFile("stop_words").getLines().toArray)
      .setInputCol("descriptionTokens")
      .setOutputCol("filteredDescriptionTokens")

    val descriptionVec = new Word2Vec()
      .setInputCol("filteredDescriptionTokens")
      .setOutputCol("descriptionFeatures")

    val nameTokens = new RegexTokenizer()
      .setToLowercase(true)
      .setInputCol("name")
      .setOutputCol("nameTokens")
      .setPattern("\\s")

    val nameRemover = new StopWordsRemover()
      .setStopWords(scala.io.Source.fromFile("stop_words").getLines().toArray)
      .setInputCol("nameTokens")
      .setOutputCol("filteredNameTokens")

    val name2Vec = new Word2Vec()
      .setInputCol("filteredNameTokens")
      .setOutputCol("nameFeatures")

    val features = new VectorAssembler()
      .setInputCols(Array("descriptionFeatures", "nameFeatures"))
      .setOutputCol("indexedFeatures")

    val vectorizedDF: DataFrame = new Pipeline()
      .setStages(Array(descriptionTokens, descriptionRemover, descriptionVec,
        nameTokens, nameRemover, name2Vec))
      .fit(df).transform(df)

    features.transform(vectorizedDF)
  }
}
