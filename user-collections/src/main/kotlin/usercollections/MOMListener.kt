package usercollections
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/user-collections/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/usercollections/MOMListener.kt
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import usercollections.db.UserService

@Service
class MOMListener(
        private val userService: UserService
) {

    companion object{
        private val log = LoggerFactory.getLogger(MOMListener::class.java)
    }


    @RabbitListener(queues = ["#{queue.name}"])
    fun receiveFromAMQP(userId: String) {

        val ok = userService.registerNewUser(userId)
        if(ok){
            log.info("Registered new user via MOM: $userId")
        }
    }
}
