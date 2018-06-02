package jobcrawler.client

import java.time.{Duration, LocalDateTime, ZoneId}

import jobcrawler.vacancy.{Salary, VacancyInJsonFormat}
import org.json.{JSONArray, JSONObject}
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Random

class Crawler {

  implicit val formats = DefaultFormats

  def getVacanciesIds(dateFrom: LocalDateTime = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minus(Duration.ofMinutes(10)),
                      dateTo: LocalDateTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"))): Seq[Int] = {

    val minutes = 10
    val perPage: Int = 100
    val list = scala.collection.mutable.ArrayBuffer.empty[Int]
    var i = 1
    var currDateTimeFrom = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minus(Duration.ofMinutes(i * 10))
    var currDateTimeTo = LocalDateTime.now(ZoneId.of("Europe/Moscow"))

    while (dateFrom.compareTo(currDateTimeFrom) <= 0) {
      var currentPage: Int = 0
      var count: Int = perPage

      while (count == perPage) {
        val path = s"https://api.hh.ru/vacancies?per_page=$perPage&date_from=$currDateTimeFrom&date_to=$currDateTimeTo&page=$currentPage"
        val result: String = scala.io.Source.fromURL(path).mkString

        val obj: JSONObject = new JSONObject(result)
        val items: JSONArray = obj.getJSONArray("items")

        count = items.length()
        currentPage += 1

        (0 until items.length()).foreach(i => list += items.getJSONObject(i).getInt("id"))
      }
      i += 1
      currDateTimeFrom = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minus(Duration.ofMinutes(i * 10))
      currDateTimeTo = LocalDateTime.now(ZoneId.of("Europe/Moscow")).minus(Duration.ofMinutes((i - 1) * 10))
    }
    list
  }

  def getVacancies(ids: Seq[Int]): Seq[VacancyInJsonFormat] = {
    println("Vacancy downloading ")
    ids.par.map { id =>
      val path = s"https://api.hh.ru/vacancies/$id"
      val result: String = scala.io.Source.fromURL(path).mkString
      val vacancy = parse(result).extract[VacancyInJsonFormat]
      if (vacancy.salary == null) {
        vacancy.salary = Salary(None, None, "")
      }
      println("." * Random.nextInt(5))
      vacancy
    }.seq
  }
}