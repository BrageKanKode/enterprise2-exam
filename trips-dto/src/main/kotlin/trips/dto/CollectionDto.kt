package trips.dto

class CollectionDto(

        var cards: MutableList<TripDto> = mutableListOf(),

        var prices: MutableMap<Rarity, Int> = mutableMapOf(),

        var millValues: MutableMap<Rarity, Int> = mutableMapOf(),

        var rarityProbabilities: MutableMap<Rarity, Double> = mutableMapOf()

)
