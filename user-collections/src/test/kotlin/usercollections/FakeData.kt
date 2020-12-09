package usercollections

import trips.dto.CollectionDto
import trips.dto.Rarity.*
import trips.dto.TripDto

object FakeData {

    fun getCollectionDto() : CollectionDto{

        val dto = CollectionDto()

        dto.trips.run {
            add(TripDto(tripId = "c00", cost = 100))
            add(TripDto(tripId = "c01", cost = 100))
            add(TripDto(tripId = "c02", cost = 100))
            add(TripDto(tripId = "c03", cost = 100))
            add(TripDto(tripId = "c04", cost = 100))
            add(TripDto(tripId = "c05", cost = 100))
            add(TripDto(tripId = "c06", cost = 100))
            add(TripDto(tripId = "c07", cost = 100))
            add(TripDto(tripId = "c08", cost = 100))
            add(TripDto(tripId = "c09", cost = 100))
        }

        return dto

    }

}
