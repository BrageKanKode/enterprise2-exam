package usercollections

import usercollections.db.TripCopy
import usercollections.db.User
import usercollections.dto.TripCopyDto
import usercollections.dto.UserDto

object DtoConverter {

    fun transform(user: User) : UserDto {
        return UserDto().apply {
            userId = user.userId
            coins = user.coins
            ownedCards = user.ownedTrips.map { transform(it) }.toMutableList()
        }
    }

    fun transform(cardCopy: TripCopy) : TripCopyDto {
        return TripCopyDto().apply {
            tripId = cardCopy.tripId
            numberOfCopies = cardCopy.numberOfCopies
        }
    }
}
