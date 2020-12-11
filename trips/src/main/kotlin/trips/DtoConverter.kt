package trips
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/DtoConverter.kt
import trips.db.Trips
import trips.dto.TripDto

object DtoConverter {

    fun transform(trips: Trips) : TripDto =
            trips.run { TripDto(tripId, place, duration, cost)}

    fun transform(trips: Iterable<Trips>) : List<TripDto> = trips.map { transform(it) }
}
