package usercollections.model

import trips.dto.TripDto

data class Trip(
        val tripId : String,
        val cost: Int
){

    constructor(dto: TripDto): this(
            dto.tripId ?: throw IllegalArgumentException("Null tripId"),
            dto.cost ?: throw IllegalArgumentException("Null cost"))
}
