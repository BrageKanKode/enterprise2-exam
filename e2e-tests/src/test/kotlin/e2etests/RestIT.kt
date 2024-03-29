package e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit

//@Disabled
@Testcontainers
class RestIT {


    companion object {

        init {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
            RestAssured.port = 80
        }

        class KDockerComposeContainer(id: String, path: File) : DockerComposeContainer<KDockerComposeContainer>(id, path)

        @Container
        @JvmField
        val env = KDockerComposeContainer("enterprise2-exam", File("../docker-compose.yml"))
                .withExposedService("discovery", 8500,
                        Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(240)))
                .withLogConsumer("trips_0") { print("[TRIP_0] " + it.utf8String) }
                .withLogConsumer("trips_1") { print("[TRIP_1] " + it.utf8String) }
                .withLogConsumer("user-collections") { print("[USER_COLLECTIONS] " + it.utf8String) }
                .withLocalCompose(true)


        @BeforeAll
        @JvmStatic
        fun waitForServers() {

            Awaitility.await().atMost(240, TimeUnit.SECONDS)
                    .pollDelay(Duration.ofSeconds(20))
                    .pollInterval(Duration.ofSeconds(10))
                    .ignoreExceptions()
                    .until {

                        given().baseUri("http://${env.getServiceHost("discovery", 8500)}")
                                .port(env.getServicePort("discovery", 8500))
                                .get("/v1/agent/services")
                                .then()
                                .body("size()", equalTo(4))

                        true
                    }
        }
    }

    @Test
    fun testGetCollection() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    given().get("/api/trips/collection_v1_000")
                            .then()
                            .statusCode(200)
                            .body("data.trips.size", greaterThan(10))
                    true
                }
    }

    @Test
    fun testGetTrips() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    given().accept(ContentType.JSON)
                            .get("/api/trips")
                            .then()
                            .statusCode(200)
                            .body("data.list.size()", greaterThan(0))
                    true
                }
    }

    @Test
    fun testCreateUser() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {

                    val id = "foo_testCreateUser_" + System.currentTimeMillis()

                    given().get("/api/user-collections/$id")
                            .then()
                            .statusCode(401)


                    val password = "123456"

                    val cookie = given().contentType(ContentType.JSON)
                            .body("""
                                {
                                    "userId": "$id",
                                    "password": "$password"
                                }
                            """.trimIndent())
                            .post("/api/auth/signUp")
                            .then()
                            .statusCode(201)
                            .header("Set-Cookie", CoreMatchers.not(equalTo(null)))
                            .extract().cookie("SESSION")

                    given().cookie("SESSION", cookie)
                            .put("/api/user-collections/$id")
//                            .then()
                    //could be 400 if AMQP already registered it
//                            .statusCode(201)

                    given().cookie("SESSION", cookie)
                            .get("/api/user-collections/$id")
                            .then()
                            .statusCode(200)

                    true
                }
    }

    @Test
    fun testAMQPSignUp() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {

                    val id = "foo_testCreateUser_" + System.currentTimeMillis()

                    given().get("/api/auth/user")
                            .then()
                            .statusCode(401)

                    given().put("/api/user-collections/$id")
                            .then()
                            .statusCode(401)

                    given().get("/api/trips/$id")
                            .then()
                            .statusCode(404)


                    val password = "123456"

                    val cookie = given().contentType(ContentType.JSON)
                            .body("""
                                {
                                    "userId": "$id",
                                    "password": "$password"
                                }
                            """.trimIndent())
                            .post("/api/auth/signUp")
                            .then()
                            .statusCode(201)
                            .header("Set-Cookie", CoreMatchers.not(equalTo(null)))
                            .extract().cookie("SESSION")

                    given().cookie("SESSION", cookie)
                            .get("/api/auth/user")
                            .then()
                            .statusCode(200)

                    Awaitility.await().atMost(10, TimeUnit.SECONDS)
                            .pollInterval(Duration.ofSeconds(2))
                            .ignoreExceptions()
                            .until {
                                given().cookie("SESSION", cookie)
                                        .get("/api/user-collections/$id")
                                        .then()
                                        .statusCode(200)

//                                given().auth().basic("admin", "admin")
//                                        .contentType(ContentType.JSON)
//                                        .body(
//                                                """
//                                                    {"tripId": "$id", "place": "Bosnia", "duration": 3, "cost": 100}
//                                                """.trimIndent()
//                                        )
//                                        .put("/$id")
//                                        .then()
//                                        .statusCode(201)
//
//                                given().get("/api/trips/$id")
//                                        .then()
//                                        .statusCode(200)
//                                        .body("data.cost", equalTo(0))

                                true
                            }

                    true
                }
    }


    @Test
    fun testUserCollectionAccessControl() {

        val alice = "alice_testUserCollectionAccessControl_" + System.currentTimeMillis()
        val eve =   "eve_testUserCollectionAccessControl_" + System.currentTimeMillis()

        given().get("/api/user-collections/$alice").then().statusCode(401)
        given().put("/api/user-collections/$alice").then().statusCode(401)
        given().patch("/api/user-collections/$alice").then().statusCode(401)

        val cookie = given().contentType(ContentType.JSON)
                .body("""
                                {
                                    "userId": "$eve",
                                    "password": "123456"
                                }
                            """.trimIndent())
                .post("/api/auth/signUp")
                .then()
                .statusCode(201)
                .header("Set-Cookie", CoreMatchers.not(equalTo(null)))
                .extract().cookie("SESSION")



        given().cookie("SESSION", cookie)
                .get("/api/user-collections/$alice")
                .then()
                .statusCode(403)
    }
}
