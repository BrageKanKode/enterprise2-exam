package usercollections.model

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
