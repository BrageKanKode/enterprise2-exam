package trips.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/db/FakeDataService.kt
import com.github.javafaker.Faker
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional
import kotlin.random.Random

@Profile("FakeData")
@Service
@Transactional
class FakeDataService(
        val repository: TripsRepository
) {
    private val faker = Faker()
    @PostConstruct
    fun init(){
        for(i in 0..49){
            createRandomTrips("Foo" + i.toString().padStart(2, '0'))
        }
    }

    fun createRandomTrips(userId: String){
        val trip = Trips(userId,
                faker.country().capital(),
                Random.nextInt(50),
                Random.nextInt(30))
        repository.save(trip)
    }
}
