package trips.db

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.TypedQuery

@Repository
interface UserTripsRepository : CrudRepository<Trips, String>

@Service
@Transactional
class TripsService(
        val repository: UserTripsRepository,
        val em: EntityManager
) {

    fun registerNewTrip(tripId: String, place: String, duration: Int, cost: Int) : Boolean{

        if(repository.existsById(tripId)){
            return false
        }

        val stats = Trips(tripId = tripId, place = place, duration = duration, cost = cost)
        repository.save(stats)
        return true
    }

    fun alterTripPlace(tripId: String) {

    }
    fun alterTripDuration(tripId: String) {

    }
    fun alterTripCost(tripId: String) {

    }

    fun getNextPage(size: Int, keysetId: String? = null, keysetScore: Int? = null): List<Trips>{

        if (size < 1 || size > 1000) {
            throw IllegalArgumentException("Invalid size value: $size")
        }

        if((keysetId==null && keysetScore!=null) || (keysetId!=null && keysetScore==null)){
            throw IllegalArgumentException("keysetId and keysetScore should be both missing, or both present")
        }

        val query: TypedQuery<Trips>
        if (keysetId == null) {
            query = em.createQuery(
                    "select s from Trips s order by s.cost DESC, s.tripId DESC"
                    , Trips::class.java)
        } else {
            query = em.createQuery(
                    "select s from Trips s where s.cost<?2 or (s.cost=?2 and s.tripId<?1) order by s.cost DESC, s.tripId DESC"
                    , Trips::class.java)
            query.setParameter(1, keysetId)
            query.setParameter(2, keysetScore)
        }
        query.maxResults = size

        return query.resultList
    }
}
