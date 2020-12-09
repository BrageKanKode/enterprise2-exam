package trips

import trips.db.Trips
import trips.dto.TripsDto

object DtoConverter {

    fun transform(trips: Trips) : TripsDto =
            trips.run { TripsDto(tripId, place, duration, cost)}

    fun transform(scores: Iterable<Trips>) : List<TripsDto> = scores.map { transform(it) }
}
