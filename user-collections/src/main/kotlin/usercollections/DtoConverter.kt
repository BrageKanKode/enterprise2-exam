package usercollections
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/DtoConverter.kt
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
