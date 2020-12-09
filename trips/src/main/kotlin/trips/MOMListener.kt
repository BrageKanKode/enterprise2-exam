package trips

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
    fun receiveFromAMQP(tripId: String) {
        val ok = statsService.registerNewTrip(tripId)
        if(ok){
            log.info("Registered new trip via MOM: $tripId")
        }
    }
}
