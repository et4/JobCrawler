package jobcrawler.vacancy

case class VacancyInClearFormat(id: String,
                                name: String,
                                salaryFrom: Option[Double],
                                salaryTo: Option[Double],
                                currency: String,
                                description: String,
                                specializations: String,
                                employer: String,
                                area: String)
