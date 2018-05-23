import jobcrawler.client.Crawler
import jobcrawler.mongo.{Downloader, Uploader}
import jobcrawler.processing.{Clustering, Regression}
import jobcrawler.vacancy.{Converter, VacancyInClearFormat, VacancyInJsonFormat}


object Launcher extends App {
  val dbName = "jobs"
  val collectionName = "test3"
  //  val crawler: Crawler = new Crawler()
  //  val vacancies: Seq[VacancyInJsonFormat] = crawler.getVacancies(crawler.getVacanciesIds())
  //  val uploader: Uploader = new Uploader(dbName)
  //  uploader.upload(Converter.convert(vacancies), collectionName)
  val downloader = new Downloader(dbName)
  val vacancies = downloader.load(collectionName)
  //  val clustering = new Clustering(vacancies)
  //  clustering.kMeans()
  val regression = new Regression(vacancies)
  regression.gradientBoostedTree()
  //  regression.showNotNullInfo()
}
