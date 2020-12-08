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
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @Test
    fun testGetCollection(){

        given().get("/api/cards/collection_$LATEST")
                .then()
                .statusCode(200)
                .body("data.trips.size", greaterThan(10))
    }


    @Test
    fun testGetCollectionOldVersion(){

        given().get("/api/cards/collection_v0_002")
                .then()
                .statusCode(200)
                .body("data.trips.size", greaterThan(10))
    }

    val page : Int = 10

    @Test
    fun testGetPage() {

        given().accept(ContentType.JSON)
                .get("/api/cards")
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(page))
    }

    @Test
    fun testAllPages(){

        val read = mutableSetOf<String>()

        var page = given().accept(ContentType.JSON)
                .get("/api/cards")
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
            val ascore = page.list[i]["score"].toString().toInt()
            val bscore = page.list[i + 1]["score"].toString().toInt()
            val aid = page.list[i]["tripId"].toString()
            val bid = page.list[i + 1]["tripId"].toString()
            assertTrue(ascore >= bscore)
            if (ascore == bscore) {
                assertTrue(aid > bid)
            }
        }
    }
}
