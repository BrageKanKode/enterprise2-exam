package usercollections.db

import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import usercollections.TripService
import javax.persistence.LockModeType

@Repository
interface UserRepository : CrudRepository<User, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.userId = :id")
    fun lockedFind(@Param("id") userId: String) : User?

}


@Service
@Transactional
class UserService(
        private val userRepository: UserRepository,
        private val tripService: TripService
) {

    companion object{
        const val CARDS_PER_PACK = 5
    }

    fun findByIdEager(userId: String) : User?{

        val user = userRepository.findById(userId).orElse(null)
        if(user != null){
            user.ownedCards.size
        }
        return user
    }

    fun registerNewUser(userId: String) : Boolean{

        if(userRepository.existsById(userId)){
            return false
        }

        val user = User()
        user.userId = userId
        user.cardPacks = 3
        user.coins = 100
        userRepository.save(user)
        return true
    }

    private fun validateTrip(tripId: String) {
        if (!tripService.isInitialized()) {
            throw IllegalStateException("Card service is not initialized")
        }

        if (!tripService.tripCollection.any { it.tripId == tripId }) {
            throw IllegalArgumentException("Invalid tripId: $tripId")
        }
    }

    private fun validateUser(userId: String) {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User $userId does not exist")
        }
    }

    private fun validate(userId: String, tripId: String) {
        validateUser(userId)
        validateTrip(tripId)
    }

    fun buyTrip(userId: String, tripId: String) {
        validate(userId, tripId)

        val price = tripService.price(tripId)
        val user = userRepository.lockedFind(userId)!!

        if (user.coins < price) {
            throw IllegalArgumentException("Not enough coins")
        }

        user.coins -= price

        addCard(user, tripId)
    }

    private fun addCard(user: User, tripId: String) {
        user.ownedCards.find { it.cardId == tripId }
                ?.apply { numberOfCopies++ }
                ?: TripCopy().apply {
                    this.cardId = tripId
                    this.user = user
                    this.numberOfCopies = 1
                }.also { user.ownedCards.add(it) }
    }

    fun millCard(userId: String, cardId: String) {
        validate(userId, cardId)

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedCards.find { it.cardId == cardId }
        if(copy == null || copy.numberOfCopies == 0){
            throw IllegalArgumentException("User $userId does not own a copy of $cardId")
        }

        copy.numberOfCopies--

        val millValue = tripService.millValue(cardId)
        user.coins += millValue
    }

    fun openPack(userId: String) : List<String> {

        validateUser(userId)

        val user = userRepository.lockedFind(userId)!!

        if(user.cardPacks < 1){
            throw IllegalArgumentException("No pack to open")
        }

        user.cardPacks--

        val selection = tripService.getRandomSelection(CARDS_PER_PACK)

        val ids = mutableListOf<String>()

        selection.forEach {
            addCard(user, it.tripId)
            ids.add(it.tripId)
        }

        return ids
    }
}
