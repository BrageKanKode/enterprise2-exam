package trips.dto

import io.swagger.annotations.ApiModelProperty

class CollectionDto(

        @get:ApiModelProperty("Lists of all the trips available")
        var trips: MutableList<TripDto> = mutableListOf()

)
