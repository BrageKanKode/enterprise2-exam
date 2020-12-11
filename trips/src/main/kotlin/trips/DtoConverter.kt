package trips

import trips.db.Trips
import trips.dto.TripDto

object DtoConverter {

    fun transform(trips: Trips) : TripDto =
            trips.run { TripDto(tripId, place, duration, cost)}

    fun transform(trips: Iterable<Trips>) : List<TripDto> = trips.map { transform(it) }
}
