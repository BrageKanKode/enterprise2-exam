package trips

import trips.db.Trips
import trips.dto.TripDto

object DtoConverter {

    fun transform(trips: Trips) : TripDto =
            trips.run { TripDto(tripId, place, duration, cost)}

    fun transform(scores: Iterable<Trips>) : List<TripDto> = scores.map { transform(it) }
}
