package trips.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/db/UserStats.kt
import javax.persistence.*
import javax.validation.constraints.*

@Entity
class Trips(

        @get:Id @get:NotBlank
        var tripId: String? = null,

        @get:NotBlank
        var place : String? = null,

        @get:Min(0) @get:NotNull
        var duration: Int = 0,

        @get:Min(0) @get:NotNull
        var cost: Int = 0
)
