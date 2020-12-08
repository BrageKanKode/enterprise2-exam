package trips.dto

import io.swagger.annotations.ApiModelProperty

data class UserTripsDto(

        @get:ApiModelProperty("The id of the player")
        var tripId: String? = null,

        @get:ApiModelProperty("How many victories the player had so far")
        var place : Int? = null,

        @get:ApiModelProperty("How many defeats the player had so far")
        var duration: Int? = null,

        @get:ApiModelProperty("The current score of the player")
        var score: Int? = null
)
