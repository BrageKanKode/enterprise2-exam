package trips.db

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface UserTripsRepository : CrudRepository<UserTrips, String>

@Service
@Transactional
class UserTripsService(
        val repository: UserTripsRepository,
        val em: EntityManager
) {

    fun registerNewUser(tripId: String) : Boolean{

        if(repository.existsById(tripId)){
            return false
        }

        val stats = UserTrips(tripId, 0, 0, 0)
        repository.save(stats)
        return true
    }

    fun getNextPage(size: Int, keysetId: String? = null, keysetScore: Int? = null): List<UserTrips>{

        if (size < 1 || size > 1000) {
            throw IllegalArgumentException("Invalid size value: $size")
        }

        if((keysetId==null && keysetScore!=null) || (keysetId!=null && keysetScore==null)){
            throw IllegalArgumentException("keysetId and keysetScore should be both missing, or both present")
        }

        val query: TypedQuery<UserTrips>
        if (keysetId == null) {
            query = em.createQuery(
                    "select s from UserTrips s order by s.score DESC, s.tripId DESC"
                    , UserTrips::class.java)
        } else {
            query = em.createQuery(
                    "select s from UserTrips s where s.score<?2 or (s.score=?2 and s.tripId<?1) order by s.score DESC, s.tripId DESC"
                    , UserTrips::class.java)
            query.setParameter(1, keysetId)
            query.setParameter(2, keysetScore)
        }
        query.maxResults = size

        return query.resultList
    }
}
