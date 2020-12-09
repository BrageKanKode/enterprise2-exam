package usercollections.db

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import usercollections.FakeData
import usercollections.TripService
import usercollections.model.Collection


@Profile("UserServiceTest")
@Primary
@Service
class FakeCardService : TripService(RestTemplate(), Resilience4JCircuitBreakerFactory()){

    override fun fetchData() {
        val dto = FakeData.getCollectionDto()
        super.collection = Collection(dto)
    }
}



@ActiveProfiles("UserServiceTest,test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class UserServiceTest{

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun initTest(){
        userRepository.deleteAll()
    }


    @Test
    fun testCreateUser(){
        val id = "foo"
        assertTrue(userService.registerNewUser(id))
        assertTrue(userRepository.existsById(id))
    }

    @Test
    fun testFailCreateUserTwice(){
        val id = "foo"
        assertTrue(userService.registerNewUser(id))
        assertFalse(userService.registerNewUser(id))
    }

    @Test
    fun testBuyCard(){

        val userId = "foo"
        val tripId = "c00"
        val people = 1

        userService.registerNewUser(userId)
        userService.buyTrip(userId, people, tripId)

        val user = userService.findByIdEager(userId)!!
        assertTrue(user.ownedTrips.any { it.tripId == tripId})
    }

    @Test
    fun testBuyCardFailNotEnoughMoney(){

        val userId = "foo"
        val tripId = "c00"
        val people = 2
        userService.registerNewUser(userId)

        val e = assertThrows(IllegalArgumentException::class.java){
            userService.buyTrip(userId, people, tripId)
        }
        assertTrue(e.message!!.contains("coin"), "Wrong error message: ${e.message}")
    }

    @Test
    fun testAlterPeople(){
        val userId = "foo"
        val tripId = "c08"
        var people = 2

        userService.registerNewUser(userId)
        userService.buyTrip(userId, people, tripId)

        val user = userService.findByIdEager(userId)!!
        assertTrue(user.ownedTrips.any { it.tripId == tripId})

        val before = userRepository.findById(userId).get()
        val coins = before.coins

        people = 1

        userService.alterPeople(userId, tripId, people)
        val after = userService.findByIdEager(userId)!!
        assertTrue(after.coins > coins)
    }

    @Test
    fun testMillCard(){

        val userId = "foo"
        val tripId = "c00"
        val people = 1

        userService.registerNewUser(userId)


        userService.buyTrip(userId, people, tripId)

        val before = userRepository.findById(userId).get()
        val coins = before.coins

        val between = userService.findByIdEager(userId)!!
        val n = between.ownedTrips.sumBy { it.numberOfCopies }
        userService.millCard(userId, between.ownedTrips[0].tripId!!)


        val after = userService.findByIdEager(userId)!!
        assertTrue(after.coins > coins)
        assertEquals(n-1, after.ownedTrips.sumBy { it.numberOfCopies })
    }
}
