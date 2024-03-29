package trips
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/test/kotlin/org/tsdes/advanced/exercises/cardgame/scores/RestApiTest.kt
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import rest.PageDto
import trips.RestApi.Companion.LATEST
import trips.db.TripsService
import trips.db.TripsRepository
import trips.dto.Command
import trips.dto.PatchTripsDto
import javax.annotation.PostConstruct

@ActiveProfiles("FakeData, test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(Application::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RestApiTest{

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var repository: TripsRepository
    @Autowired
    private lateinit var service: TripsService

    @PostConstruct
    fun init(){
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/trips"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun testGetCollection(){

        given().get("/collection_$LATEST")
                .then()
                .statusCode(200)
                .body("data.trips.size", greaterThan(10))
    }

    val page : Int = 10

    @Test
    fun testGetPage() {

        given().accept(ContentType.JSON)
                .get("/")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(page))
    }

    @Test
    fun testCreatePage() {
        val n = repository.count()
        val id = "admin"
        val tripId = "Bar003"
        val place = "Bosnia"
        val duration = 3
        val cost = 100

        given().auth().basic(id, "admin")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())
    }

    @Test
    fun testCreatePageFail() {
        val n = repository.count()
        val id = "foo"
        val tripId = "Bar004"
        val place = "Bosnia"
        val duration = 3
        val cost = 100

        given().auth().basic(id, "123")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(403)
        assertEquals(n, repository.count())
    }

    @Test
    fun testAlterTripCost() {
        val n = repository.count()
        val id = "admin"
        val tripId = "BarAlterCostRest003"
        val place = "Bosnia"
        val duration = 3
        val cost = 100
        given().auth().basic(id, "admin")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())

        given().auth().basic(id, id)
                .contentType(ContentType.JSON)
                .body(PatchTripsDto(Command.ALTER_TRIP_COST, tripId, place, 3, 50))
                .patch("/$tripId")
                .then()
                .statusCode(201)

        val trip = service.findByIdEager(tripId)!!
        assertEquals(50, trip.cost)
        assertTrue(trip.cost < cost)


    }

    @Test
    fun testAlterTripPlace() {
        val n = repository.count()
        val id = "admin"
        val tripId = "BarAlterPlaceRest003"
        val place = "Bosnia"
        val duration = 3
        val cost = 100

        given().auth().basic(id, "admin")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())

        given().auth().basic(id, id)
                .contentType(ContentType.JSON)
                .body(PatchTripsDto(Command.ALTER_TRIP_PLACE, tripId, "Oslo", 3, 100))
                .patch("/$tripId")
                .then()
                .statusCode(201)

        val trip = service.findByIdEager(tripId)!!
        assertEquals("Oslo", trip.place)
        assertTrue(trip.place != place)
    }

    @Test
    fun testAlterTripDuration() {
        val n = repository.count()
        val id = "admin"
        val tripId = "BarAlterDurationRest003"
        val place = "Bosnia"
        val duration = 3
        val cost = 100
        given().auth().basic(id, "admin")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())

        given().auth().basic(id, id)
                .contentType(ContentType.JSON)
                .body(PatchTripsDto(Command.ALTER_TRIP_DURATION, tripId, place, 5, 50))
                .patch("/$tripId")
                .then()
                .statusCode(201)

        val trip = service.findByIdEager(tripId)!!
        assertEquals(5, trip.duration)
        assertTrue(trip.duration > duration)
    }

    @Test
    fun testAlterTripFail() {
        val n = repository.count()
        val id = "admin"
        val tripId = "BarAlterFailRest003"
        val place = "Bosnia"
        val duration = 3
        val cost = 100
        given().auth().basic(id, "admin")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": $duration, "cost": $cost}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())

        given().auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .body(PatchTripsDto(Command.ALTER_TRIP_DURATION, tripId, place, 5, 50))
                .patch("/$tripId")
                .then()
                .statusCode(403)

        val trip = service.findByIdEager(tripId)!!
        assertEquals(3, trip.duration)
        assertTrue(trip.duration == duration)
    }

    @Test
    fun testGetTripInfo() {
        val n = repository.count()
        val tripId = "Bar006"
        val place = "Bosnia"
        val id = "admin"

        given().auth().basic(id, id)
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": 3, "cost": 100}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(201)
        assertEquals(n+1, repository.count())

        given().accept(ContentType.JSON)
                .get("/$tripId")
                .then()
                .statusCode(200)
    }

    @Test
    fun testAllPages(){

        val read = mutableSetOf<String>()

        var page = given().accept(ContentType.JSON)
                .get("/")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(page))
                .extract().body().jsonPath().getObject("data",object: TypeRef<PageDto<Map<String, Object>>>(){})
        read.addAll(page.list.map { it["tripId"].toString()})

        checkOrder(page)

        while(page.next != null){
            page = given().accept(ContentType.JSON)
                    .get(page.next)
                    .then()
                    .statusCode(200)
                    .extract().body().jsonPath().getObject("data",object: TypeRef<PageDto<Map<String, Object>>>(){})
            read.addAll(page.list.map { it["tripId"].toString()})
            checkOrder(page)
        }

        val total = repository.count().toInt()

        //recall that sets have unique elements
        assertEquals(total, read.size)
    }

    private fun checkOrder(page: PageDto<Map<String, Object>>) {
        for (i in 0 until page.list.size - 1) {
            val atrip = page.list[i]["cost"].toString().toInt()
            val btrip = page.list[i + 1]["cost"].toString().toInt()
            val aid = page.list[i]["tripId"].toString()
            val bid = page.list[i + 1]["tripId"].toString()
            assertTrue(atrip >= btrip)
            if (atrip == btrip) {
                assertTrue(aid > bid)
            }
        }
    }
}
