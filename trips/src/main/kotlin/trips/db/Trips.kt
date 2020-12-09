package trips.db

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
