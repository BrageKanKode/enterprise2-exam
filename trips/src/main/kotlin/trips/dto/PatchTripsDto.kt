package trips.dto

import io.swagger.annotations.ApiModelProperty

enum class Command {
    ALTER_TRIP_COST,
    ALTER_TRIP_PLACE,
    ALTER_TRIP_DURATION
}

data class PatchTripsDto(

        @get:ApiModelProperty("Command to execute on a trip")
        var command: Command? = null,

        @get:ApiModelProperty("Card id, if the command requires one")
        var tripId: String? = null,

        @get:ApiModelProperty("Place of the trip, if the command requires one")
        var place: String? = null,

        @get:ApiModelProperty("Duration of the trip, if the command requires one")
        var duration: Int? = null,

        @get:ApiModelProperty("Cost of the trip, if the command requires one")
        var cost: Int? = null
)
