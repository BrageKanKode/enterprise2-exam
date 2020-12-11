package usercollections.model
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/model/Collection.kt
import trips.dto.CollectionDto


data class Collection(

        val trips : List<Trip>
){

    constructor(dto: CollectionDto) : this(
            dto.trips.map { Trip(it) }
    )


    init{
        if(trips.isEmpty()){
            throw IllegalArgumentException("No trips")
        }
    }
}
