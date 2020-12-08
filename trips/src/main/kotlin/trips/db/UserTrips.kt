package trips.db

import javax.persistence.*
import javax.validation.constraints.*

@Entity
class UserTrips(

        @get:Id @get:NotBlank
        var tripId: String? = null,

        @get:Min(0) @get:NotNull
        var place : Int = 0,

        @get:Min(0) @get:NotNull
        var duration: Int = 0,

        @get:Min(0) @get:NotNull
        var score: Int = 0
)
