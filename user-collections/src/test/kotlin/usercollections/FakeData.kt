package usercollections

import trips.dto.CollectionDto
import trips.dto.Rarity.*
import trips.dto.TripDto

object FakeData {

    fun getCollectionDto() : CollectionDto{

        val dto = CollectionDto()

        dto.prices[BRONZE] = 100
        dto.prices[SILVER] = 500
        dto.prices[GOLD] = 1_000
        dto.prices[PINK_DIAMOND] = 100_000

        dto.prices.forEach { dto.millValues[it.key] = it.value / 4 }
        dto.prices.keys.forEach { dto.rarityProbabilities[it] = 0.25 }

        dto.trips.run {
            add(TripDto(tripId = "c00", rarity = BRONZE))
            add(TripDto(tripId = "c01", rarity = BRONZE))
            add(TripDto(tripId = "c02", rarity = BRONZE))
            add(TripDto(tripId = "c03", rarity = BRONZE))
            add(TripDto(tripId = "c04", rarity = SILVER))
            add(TripDto(tripId = "c05", rarity = SILVER))
            add(TripDto(tripId = "c06", rarity = SILVER))
            add(TripDto(tripId = "c07", rarity = GOLD))
            add(TripDto(tripId = "c08", rarity = GOLD))
            add(TripDto(tripId = "c09", rarity = PINK_DIAMOND))
        }

        return dto

    }

}
