package jobcrawler.vacancy

object Converter {
  def convert(vacancyInJsonFormat: Seq[VacancyInJsonFormat]): Seq[VacancyInClearFormat] = {
    vacancyInJsonFormat.map { jsonVacancy =>
      VacancyInClearFormat(jsonVacancy.id,
        jsonVacancy.name,
        jsonVacancy.salary.from,
        jsonVacancy.salary.to,
        jsonVacancy.salary.currency,
        jsonVacancy.description
          .replaceAll("<[^>]*>", "")
          .replaceAll("[^А-Яа-яA-Za-z0-9 ]", "")
          .replaceAll(" +", " ").toLowerCase,
        jsonVacancy.specializations.map(p => p.profarea_name).mkString(";"),
        jsonVacancy.employer.name,
        jsonVacancy.area.name)
    }
  }
}
