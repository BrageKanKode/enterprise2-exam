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

        dto.cards.run {
            add(TripDto(cardId = "c00", rarity = BRONZE))
            add(TripDto(cardId = "c01", rarity = BRONZE))
            add(TripDto(cardId = "c02", rarity = BRONZE))
            add(TripDto(cardId = "c03", rarity = BRONZE))
            add(TripDto(cardId = "c04", rarity = SILVER))
            add(TripDto(cardId = "c05", rarity = SILVER))
            add(TripDto(cardId = "c06", rarity = SILVER))
            add(TripDto(cardId = "c07", rarity = GOLD))
            add(TripDto(cardId = "c08", rarity = GOLD))
            add(TripDto(cardId = "c09", rarity = PINK_DIAMOND))
        }

        return dto

    }

}
