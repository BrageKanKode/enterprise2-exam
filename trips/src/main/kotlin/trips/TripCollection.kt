package trips

import trips.dto.CollectionDto
import trips.dto.Rarity.*
import trips.dto.TripDto

object TripCollection {

    fun get() : CollectionDto {
        val dto = CollectionDto()


        addCards(dto)

        return dto
    }

    private fun addCards(dto: CollectionDto) {

        dto.trips.run {
            add(TripDto("c000", "Green Mold", 3, 100))
            add(TripDto("c001", "Opera Singer", 1, 100))
            add(TripDto("c002", "Not Stitch", 3, 100))
            add(TripDto("c003", "Assault Hamster", 2, 100))
            add(TripDto("c004", "WTF?!?", 4, 100))
            add(TripDto("c005", "Stupid Lump", 5, 100))
            add(TripDto("c006", "Sad Farter", 5, 100))
            add(TripDto("c007", "Smelly Tainter", 2, 100))
            add(TripDto("c008", "Hårboll", 6, 100))
            add(TripDto("c009", "Blue Horned", 7, 100))
            add(TripDto("c010", "Børje McTrumf", 7, 100))
            add(TripDto("c011", "Exa Nopass", 7, 100))
            add(TripDto("c012", "Dick Tracy", 14, 100))
            add(TripDto("c013", "Marius Mario", 14, 100))
            add(TripDto("c014", "Patrick Stew", 7, 100))
            add(TripDto("c015", "Fluffy The Hugger of Death", 14, 100))
            add(TripDto("c016", "Gary The Wise", 14, 100))
            add(TripDto("c017", "Grump-Grump The Grumpy One", 7, 100))
            add(TripDto("c018", "Bert-ho-met The Polite Guy", 7, 100))
            add(TripDto("c019", "Bengt The Destroyer", 7, 100))
        }

        assert(dto.trips.size == dto.trips.map { it.tripId }.toSet().size)
        assert(dto.trips.size == dto.trips.map { it.place }.toSet().size)
//        assert(dto.trips.size == dto.trips.map { it.cost }.toSet().size)
    }
}
