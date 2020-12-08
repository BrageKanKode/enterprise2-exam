package usercollections.model

import trips.dto.Rarity
import trips.dto.TripDto

data class Trip(
        val cardId : String,
        val rarity: Rarity
){

    constructor(dto: TripDto): this(
            dto.cardId ?: throw IllegalArgumentException("Null cardId"),
            dto.rarity ?: throw IllegalArgumentException("Null rarity"))
}
