package trips
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/MOMListener.kt
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import trips.db.TripsService

@Service
class MOMListener(
        private val statsService: TripsService
) {

    companion object{
        private val log = LoggerFactory.getLogger(MOMListener::class.java)
    }


    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(tripId: String, place: String, duration: Int, cost: Int) {
        val ok = statsService.registerNewTrip(tripId, place, duration, cost)
        if(ok){
            log.info("Registered new trip via MOM: $tripId")
        }
    }
}
