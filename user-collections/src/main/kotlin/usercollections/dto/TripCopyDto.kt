package usercollections.dto

import io.swagger.annotations.ApiModelProperty

class TripCopyDto (
        @get:ApiModelProperty("Id of the card")
        var cardId: String? = null,

        @get:ApiModelProperty("Number of copies of the card that the user owns")
        var numberOfCopies: Int? = null
)
