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
            ownedTrips = user.ownedTrips.map { transform(it) }.toMutableList()
        }
    }

    fun transform(tripCopy: TripCopy) : TripCopyDto {
        return TripCopyDto().apply {
            tripId = tripCopy.tripId
            numberOfCopies = tripCopy.numberOfCopies
        }
    }
}
