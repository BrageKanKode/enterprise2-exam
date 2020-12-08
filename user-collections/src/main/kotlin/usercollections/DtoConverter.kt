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
            cardPacks = user.cardPacks
            ownedCards = user.ownedCards.map { transform(it) }.toMutableList()
        }
    }

    fun transform(cardCopy: TripCopy) : TripCopyDto {
        return TripCopyDto().apply {
            cardId = cardCopy.cardId
            numberOfCopies = cardCopy.numberOfCopies
        }
    }
}
