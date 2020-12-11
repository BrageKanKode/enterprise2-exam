package usercollections.dto
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/dto/CardCopyDto.kt
import io.swagger.annotations.ApiModelProperty

class TripCopyDto (
        @get:ApiModelProperty("Id of the trip")
        var tripId: String? = null,

        @get:ApiModelProperty("Number of copies of the trip that the user owns")
        var numberOfCopies: Int? = null
)
