package trips.db

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional
import kotlin.random.Random

@Profile("FakeData")
@Service
@Transactional
class FakeDataService(
        val repository: UserTripsRepository
) {

    @PostConstruct
    fun init(){
        for(i in 0..49){
            createRandomUser("Foo" + i.toString().padStart(2, '0'))
        }
    }

    fun createRandomUser(userId: String){
        val userWithTrip = UserTrips(userId,
                Random.nextInt(50),
                Random.nextInt(50),
                Random.nextInt(30))
        repository.save(userWithTrip)
    }
}
