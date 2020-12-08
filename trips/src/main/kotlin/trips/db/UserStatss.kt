package trips.db

import javax.persistence.*
import javax.validation.constraints.*

@Entity
class UserStatss(

        @get:Id @get:NotBlank
        var userId: String? = null,

        @get:Min(0) @get:NotNull
        var victories : Int = 0,

        @get:Min(0) @get:NotNull
        var defeats: Int = 0,

        @get:Min(0) @get:NotNull
        var draws : Int = 0,

        @get:Min(0) @get:NotNull
        var score: Int = 0
)
