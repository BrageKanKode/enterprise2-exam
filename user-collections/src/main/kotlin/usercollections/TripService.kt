package usercollections


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
import trips.dto.Rarity.*
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

    @Value("\${cardServiceAddress}")
    private lateinit var tripServiceAddress: String

    val tripCollection : List<Trip>
        get() = collection?.trips ?: listOf()

    private val lock = Any()

    private lateinit var cb: CircuitBreaker


    @PostConstruct
    fun init(){

        cb = circuitBreakerFactory.create("circuitBreakerToCards")

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
                .fromUriString("http://${tripServiceAddress.trim()}/api/cards/collection_$version")
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
            log.error("Failed to parse card collection info: ${e.message}")
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

    fun millValue(tripId: String) : Int {
        verifyCollection()
        val card : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid cardId $tripId")

        return collection!!.millValues[card.rarity]!!
    }

    fun price(tripId: String) : Int {
        verifyCollection()
        val trip : Trip = tripCollection.find { it.tripId  == tripId} ?:
        throw IllegalArgumentException("Invalid tripId $tripId")

        return collection!!.prices[trip.rarity]!!
    }

    fun getRandomSelection(n: Int) : List<Trip>{

        if(n <= 0){
            throw IllegalArgumentException("Non-positive n: $n")
        }

        verifyCollection()

        val selection = mutableListOf<Trip>()

        val probabilities = collection!!.rarityProbabilities
        val bronze = probabilities[BRONZE]!!
        val silver = probabilities[SILVER]!!
        val gold = probabilities[GOLD]!!
        //val pink = probabilities[Rarity.PINK_DIAMOND]!!

        repeat(n) {
            val p = Math.random()
            val r = when{
                p <= bronze -> BRONZE
                p > bronze && p <= bronze + silver -> SILVER
                p > bronze + silver && p <= bronze + silver + gold -> GOLD
                p > bronze + silver + gold -> PINK_DIAMOND
                else -> throw IllegalStateException("BUG for p=$p")
            }
            val card = collection!!.cardsByRarity[r].let{ it!![Random.nextInt(it.size)] }
            selection.add(card)
        }

        return selection
    }
}
