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
    val perPage: Int = 50
    var currentPage: Int = 0
    var count: Int = perPage
    val list = scala.collection.mutable.ArrayBuffer.empty[Int]

    while (count == perPage) {
      val path = s"https://api.hh.ru/vacancies?per_page=$perPage&date_from=$dateFrom&date_to=$dateTo&page=$currentPage"
      val result: String = scala.io.Source.fromURL(path).mkString

      val obj: JSONObject = new JSONObject(result)
      val items: JSONArray = obj.getJSONArray("items")

      count = items.length()
      currentPage += 1

      (0 until items.length()).foreach(i => list += items.getJSONObject(i).getInt("id"))
    }
    list
  }

  def getVacancies(ids: Seq[Int]): Seq[VacancyInJsonFormat] = {
    println("Vacancy downloading ")
    ids.map { id =>
      val path = s"https://api.hh.ru/vacancies/$id"
      val result: String = scala.io.Source.fromURL(path).mkString
      val vacancy = parse(result).extract[VacancyInJsonFormat]
      if (vacancy.salary == null) {
        vacancy.salary = Salary(None, None, "")
      }
      print("." * Random.nextInt(5))
      vacancy
    }
  }
}