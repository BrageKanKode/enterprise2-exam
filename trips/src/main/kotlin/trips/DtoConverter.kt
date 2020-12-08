package trips

import trips.db.UserTrips
import trips.dto.UserTripsDto

object DtoConverter {

    fun transform(trips: UserTrips) : UserTripsDto =
            trips.run { UserTripsDto(tripId, place, duration, score)}

    fun transform(scores: Iterable<UserTrips>) : List<UserTripsDto> = scores.map { transform(it) }
}
