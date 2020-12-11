package usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/PatchUserDto.kt
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
