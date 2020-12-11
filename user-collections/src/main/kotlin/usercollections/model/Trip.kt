package usercollections.model
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/model/Card.kt
import trips.dto.TripDto

data class Trip(
        val tripId : String,
        val cost: Int
){

    constructor(dto: TripDto): this(
            dto.tripId ?: throw IllegalArgumentException("Null tripId"),
            dto.cost ?: throw IllegalArgumentException("Null cost"))
}
