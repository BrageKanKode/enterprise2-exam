package trips.db
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/scores/db/UserStatsServiceTest.kt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("FakeData,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class TripsServiceTest{

    @Autowired
    private lateinit var service: TripsService

    @Autowired
    private lateinit var repository: TripsRepository

    @Test
    fun testInit(){
        assertTrue(repository.count() > 10)
    }

    @Test
    fun testCreateTrip(){
        val n = repository.count()
        service.registerNewTrip("Bar001", "Bosnia", 3, 100)
        assertEquals(n+1, repository.count())
    }

    @Test
    fun testAlterTripCost(){
        val tripId = "Bar002"
        val place = "Bosnia"

        service.registerNewTrip(tripId, place, 3, 100)

        val before = repository.findById(tripId).get()

        service.alterTripCost(tripId, 50)

        val after = service.findByIdEager(tripId)!!
        assertTrue(after.cost < before.cost)
        assertEquals(50, after.cost)
    }
    @Test
    fun testAlterTripPlace(){
        val tripId = "BarPlace001"
        val place = "Bosnia"

        service.registerNewTrip(tripId, place, 3, 100)

        val before = repository.findById(tripId).get()

        service.alterTripPlace(tripId, "Oslo")

        val after = service.findByIdEager(tripId)!!
        assertFalse(after.place == before.place)
        assertEquals("Oslo", after.place)
    }
    @Test
    fun testAlterTripDuration(){
        val tripId = "BarDuration002"
        val place = "Bosnia"

        service.registerNewTrip(tripId, place, 3, 100)

        val before = repository.findById(tripId).get()

        service.alterTripDuration(tripId, 5)

        val after = service.findByIdEager(tripId)!!
        assertTrue(after.duration > before.duration)
        assertEquals(5, after.duration)
    }

    @Test
    fun testPage(){

        val n = 5
        val page = service.getNextPage(n)
        assertEquals(n, page.size)

        for(i in 0 until n-1){
            assertTrue(page[i].cost >= page[i+1].cost)
        }
    }

}
