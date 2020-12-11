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

    fun findByIdEager(userId: String) : User?{

        val user = userRepository.findById(userId).orElse(null)
        if(user != null){
            user.ownedTrips.size
        }
        return user
    }

    fun deleteUser(userId: String): Boolean {
        validateUser(userId)
        userRepository.deleteById(userId)

        return true
    }

    fun registerNewUser(userId: String) : Boolean{

        if(userRepository.existsById(userId)){
            return false
        }

        val user = User()
        user.userId = userId
        user.coins = 100
        userRepository.save(user)
        return true
    }

    private fun validateTrip(tripId: String) {
        if (!tripService.isInitialized()) {
            throw IllegalStateException("Trip service is not initialized")
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

    fun alterPeople(userId: String, tripId: String, people: Int) {
        validate(userId, tripId)
        if (people <= 0) {
            throw java.lang.IllegalArgumentException("Can't change amount of people to under 1")
        }

        val user = userRepository.lockedFind(userId)!!
        val copy = user.ownedTrips.find { it.tripId == tripId }
        val price = tripService.price(tripId)

        if(copy == null || copy.numberOfCopies == 0){
            throw IllegalArgumentException("User $userId does not own a copy of $tripId")
        }

        if (copy.people < people ) {
            // add people
            user.coins = user.coins - (price * people)
        }
        else if (copy.people > people) {
            // subtract people
            user.coins = user.coins + (price * people)
        } else {
            // It's the same
        }

        user.ownedTrips.find { it.tripId == tripId }
                ?.apply { numberOfCopies = people }.also {
                    user.ownedTrips.add(it!!)
                }
    }

    fun buyTrip(userId: String, people: Int, tripId: String) {
        validate(userId, tripId)

        val price = tripService.price(tripId)
        val user = userRepository.lockedFind(userId)!!

        if (user.coins < price * people) {
            throw IllegalArgumentException("Not enough coins")
        }

        user.coins -= price * people

        addTrip(user, people, tripId)
    }

    private fun addTrip(user: User, people: Int, tripId: String) {
        user.ownedTrips.find { it.tripId == tripId }
                ?.apply { numberOfCopies = people }
                ?: TripCopy().apply {
                    this.tripId = tripId
                    this.user = user
                    this.numberOfCopies = 1
                    this.people = people
                }.also { user.ownedTrips.add(it) }
    }

    fun sellTrip(userId: String, tripId: String) {
        validate(userId, tripId)

        val user = userRepository.lockedFind(userId)!!

        val copy = user.ownedTrips.find { it.tripId == tripId }
        if(copy == null || copy.numberOfCopies == 0){
            throw IllegalArgumentException("User $userId does not own a copy of $tripId")
        }

        copy.numberOfCopies--

        val millValue = tripService.sellValue(tripId)
        user.coins += millValue
    }
}
