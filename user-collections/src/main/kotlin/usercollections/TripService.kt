package usercollections

//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/CardService.kt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import rest.WrappedResponse
import trips.dto.CollectionDto
import usercollections.model.Trip
import usercollections.model.Collection
import javax.annotation.PostConstruct
import kotlin.random.Random

@Service
class TripService(
        private val client: RestTemplate,
        private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {

    companion object{
        private val log = LoggerFactory.getLogger(TripService::class.java)
    }

    protected var collection: Collection? = null

    @Value("\${tripsServiceAddress}")
    private lateinit var tripsServiceAddress: String

    val tripCollection : List<Trip>
        get() = collection?.trips ?: listOf()

    private val lock = Any()

    private lateinit var cb: CircuitBreaker


    @PostConstruct
    fun init(){

        cb = circuitBreakerFactory.create("circuitBreakerToTrips")

        synchronized(lock){
            if(tripCollection.isNotEmpty()){
                return
            }
            fetchData()
        }
    }

    fun isInitialized() = tripCollection.isNotEmpty()

    protected fun fetchData(){

        val version = "v1_000"
        val uri = UriComponentsBuilder
                .fromUriString("http://${tripsServiceAddress.trim()}/api/trips/collection_$version")
                .build().toUri()

        val response = cb.run(
                {
                    client.exchange(
                            uri,
                            HttpMethod.GET,
                            null,
                            object : ParameterizedTypeReference<WrappedResponse<CollectionDto>>() {})
                },
                { e ->
                    log.error("Failed to fetch data from Trip Service: ${e.message}")
                    null
                }
        ) ?: return


        if (response.statusCodeValue != 200) {
            log.error("Error in fetching data from Trip Service. Status ${response.statusCodeValue}." +
                    "Message: " + response.body.message)
        }

        try {
            collection = Collection(response.body.data!!)
        } catch (e: Exception) {
            log.error("Failed to parse trip collection info: ${e.message}")
        }
    }

    private fun verifyCollection(){

        if(collection == null){
            fetchData()

            if(collection == null){
                throw IllegalStateException("No collection info")
            }
        }
    }

    fun sellValue(tripId: String) : Int {
        verifyCollection()
        val trip : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid tripId $tripId")

        return trip.cost
    }

    fun price(tripId: String) : Int {
        verifyCollection()
        val trip : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid tripId $tripId")

        return trip.cost
    }
}
