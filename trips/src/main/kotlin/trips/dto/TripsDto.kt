package trips.dto

import io.swagger.annotations.ApiModelProperty

data class TripsDto(

        @get:ApiModelProperty("The id of the trip")
        var tripId: String? = null,

        @get:ApiModelProperty("Where the trip takes place")
        var place : String? = null,

        @get:ApiModelProperty("How many days the trip lasts")
        var duration: Int? = null,

        @get:ApiModelProperty("The cost of the trip")
        var cost: Int? = null
)
