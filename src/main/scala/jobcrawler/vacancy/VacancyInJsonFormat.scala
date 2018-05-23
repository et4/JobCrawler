package jobcrawler.vacancy

case class VacancyInJsonFormat(id: String,
                               name: String,
                               var salary: Salary,
                               description: String,
                               specializations: List[ProfArea],
                               key_skills: Seq[KeySkill],
                               employer: Employer,
                               area: Area)

case class Salary(from: Option[Double], to: Option[Double], currency: String)

case class Employer(name: String)

case class Area(name: String)

case class ProfArea(profarea_name: String)

case class KeySkill(name: String)