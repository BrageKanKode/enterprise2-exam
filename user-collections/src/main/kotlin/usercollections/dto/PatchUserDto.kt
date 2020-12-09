package usercollections.dto

import io.swagger.annotations.ApiModelProperty

enum class Command {
    MILL_CARD,
    BUY_CARD
}

data class PatchUserDto(

        @get:ApiModelProperty("Command to execute on a user's collection")
        var command: Command? = null,

        @get:ApiModelProperty("Optional card id, if the command requires one")
        var tripId: String? = null,

        @get:ApiModelProperty("Amount of people, if the command requires one")
        var people: Int? = null
)
