package usercollections.dto

import io.swagger.annotations.ApiModelProperty

class UserDto (
        @get:ApiModelProperty("Id of the user")
        var userId: String? = null,

        @get:ApiModelProperty("The amount of coins owned by the user")
        var coins: Int? = null,

        @get:ApiModelProperty("List of trips owned by the host")
        var ownedTrips: MutableList<TripCopyDto> = mutableListOf()
)
