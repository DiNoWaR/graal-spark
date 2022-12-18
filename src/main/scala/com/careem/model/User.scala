package com.careem.model
import faker._

import java.time.ZoneId
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Locale
import scala.util.Random

trait StreamingSource extends Serializable with Product
case class User(id:Long, name:String, lastName: String, email:String, regDate:String) extends StreamingSource
case class Address(id: Long, city: String, updatedAt:String) extends StreamingSource

object User {
  def generateUser():User = {
    User(id = Common.rand.nextInt(1000),
      name = Faker.default.firstName(),
      lastName = Faker.default.lastName(),
      email = Faker.default.emailAddress(),
      regDate = Common.formatter.format(Faker.default.pastInstant())
    )
  }
}

object Address {
  def generateAddress(): Address ={
    Address(id = Common.rand.nextInt(1000),
      city = Faker.default.city(),
      updatedAt = Common.formatter.format(Faker.default.pastInstant())
    )
  }
}

object Common {
  private[model] val formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
    .withLocale( Locale.UK )
    .withZone( ZoneId.systemDefault() )

  private[model] val rand = new Random()
}