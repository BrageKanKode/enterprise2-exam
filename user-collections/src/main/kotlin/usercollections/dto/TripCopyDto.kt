package usercollections.dto

import io.swagger.annotations.ApiModelProperty

class TripCopyDto (
        @get:ApiModelProperty("Id of the trip")
        var tripId: String? = null,

        @get:ApiModelProperty("Number of copies of the trip that the user owns")
        var numberOfCopies: Int? = null
)
