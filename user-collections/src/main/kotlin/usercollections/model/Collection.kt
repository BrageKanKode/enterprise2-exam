package usercollections.model

import trips.dto.CollectionDto
import trips.dto.Rarity
import kotlin.math.abs


data class Collection(

        val trips : List<Trip>

//        val prices: Map<Rarity, Int>,
//
//        val millValues: Map<Rarity, Int>,
//
//        val rarityProbabilities: Map<Rarity, Double>
){

    constructor(dto: CollectionDto) : this(
            dto.trips.map { Trip(it) }
//            dto.prices.toMap(),
//            dto.millValues.toMap(),
//            dto.rarityProbabilities.toMap()
    )


    init{
        if(trips.isEmpty()){
            throw IllegalArgumentException("No cards")
        }
//        Rarity.values().forEach {
//            requireNotNull(prices[it])
//            requireNotNull(millValues[it])
//            requireNotNull(rarityProbabilities[it])
//        }
//
//        val p = rarityProbabilities.values.sum()
//        if(abs(1 - p) > 0.00001){
//            throw IllegalArgumentException("Invalid probability sum: $p")
//        }
    }
}
