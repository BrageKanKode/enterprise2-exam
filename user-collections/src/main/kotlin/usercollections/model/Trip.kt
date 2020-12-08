package usercollections.model

import trips.dto.Rarity
import trips.dto.TripDto

data class Trip(
        val tripId : String,
        val rarity: Rarity
){

    constructor(dto: TripDto): this(
            dto.tripId ?: throw IllegalArgumentException("Null cardId"),
            dto.rarity ?: throw IllegalArgumentException("Null rarity"))
}
