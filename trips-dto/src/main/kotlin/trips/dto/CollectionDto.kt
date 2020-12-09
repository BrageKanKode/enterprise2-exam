package trips.dto

import io.swagger.annotations.ApiModelProperty

class CollectionDto(

        @get:ApiModelProperty("Lists of all the trips available")
        var trips: MutableList<TripDto> = mutableListOf(),

        @get:ApiModelProperty("Cost if each ticket")
        var prices: MutableList<Int> = mutableListOf(),

        var millValues: MutableMap<Rarity, Int> = mutableMapOf(),

        var rarityProbabilities: MutableMap<Rarity, Double> = mutableMapOf()

)
