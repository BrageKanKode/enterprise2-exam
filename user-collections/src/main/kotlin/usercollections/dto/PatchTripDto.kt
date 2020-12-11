package usercollections.dto

import io.swagger.annotations.ApiModelProperty

enum class Command {
    CANCEL_TRIP,
    BUY_TRIP,
    ALTER_PEOPLE
}

data class PatchTripDto(

        @get:ApiModelProperty("Command to execute on a user's collection")
        var command: Command? = null,

        @get:ApiModelProperty("Optional trip id, if the command requires one")
        var tripId: String? = null,

        @get:ApiModelProperty("Amount of people, if the command requires one")
        var people: Int? = null
)
