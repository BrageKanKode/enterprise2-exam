package trips

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
import trips.db.UserTripsRepository
import javax.annotation.PostConstruct

@ActiveProfiles("FakeData, test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [(Application::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RestApiTest{

    @LocalServerPort
    protected var port = 0

    @Autowired
    private lateinit var repository: UserTripsRepository

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


    @Test
    fun testGetCollectionOldVersion(){

        given().get("/collection_v0_002")
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
        given().auth().basic(id, "admin")
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
    }

    @Test
    fun testCreatePageFail() {
        val n = repository.count()
        val id = "foo"
        val tripId = "Bar004"
        val place = "Bosnia"

        given().auth().basic(id, "123")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": 3, "cost": 100}
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
        val id = "foo"
        val tripId = "Bar005"
        val place = "Bosnia"

        given().auth().basic(id, "123")
                .contentType(ContentType.JSON)
                .body(
                        """
                            {"tripId": "$tripId", "place": "$place", "duration": 3, "cost": 100}
                        """.trimIndent()
                )
                .put("/$tripId")
                .then()
                .statusCode(403)
        assertEquals(n, repository.count())
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
            val ascore = page.list[i]["cost"].toString().toInt()
            val bscore = page.list[i + 1]["cost"].toString().toInt()
            val aid = page.list[i]["tripId"].toString()
            val bid = page.list[i + 1]["tripId"].toString()
            assertTrue(ascore >= bscore)
            if (ascore == bscore) {
                assertTrue(aid > bid)
            }
        }
    }
}
