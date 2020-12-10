package trips.dto

import io.swagger.annotations.ApiModelProperty

enum class Command {
    DELETE_TRIP,
    CREATE_TRIP,
    ALTER_TRIP
}

data class PatchTripsDto(

        @get:ApiModelProperty("Command to execute on a user's collection")
        var command: Command? = null,

        @get:ApiModelProperty("Optional card id, if the command requires one")
        var tripId: String? = null,

        @get:ApiModelProperty("Place of the trip, if the command requires one")
        var place: String? = null,

        @get:ApiModelProperty("Duration of the trip, if the command requires one")
        var duration: Int? = null,

        @get:ApiModelProperty("Cost of the trip, if the command requires one")
        var cost: Int? = null
)
