package auth
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-09/auth/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/auth/RestApi.kt
import auth.db.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import rest.RestResponseFactory
import rest.WrappedResponse
import java.net.URI
import java.security.Principal

@RestController
@RequestMapping("/api/auth")
class RestApi(
        private val service: UserService,
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: UserDetailsService,
        private val rabbit: RabbitTemplate,
        private val fanout: FanoutExchange
) {

    @RequestMapping("/user")
    fun user(user: Principal): ResponseEntity<WrappedResponse<Map<String, Any>>> {
        val map = mutableMapOf<String,Any>()
        map["name"] = user.name
        map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
        return RestResponseFactory.payload(200, map)
    }

    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signUp(@RequestBody dto: AuthDto)
            : ResponseEntity<WrappedResponse<Void>> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val registered = service.createUser(userId, password, setOf("USER"))

        if (!registered) {
            return RestResponseFactory.userFailure("Username already exists")
        }

        val userDetails = userDetailsService.loadUserByUsername(userId)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        rabbit.convertAndSend(fanout.name, "", userId)

        return RestResponseFactory.created(URI("/api/auth/user"))
    }

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: AuthDto)
            : ResponseEntity<WrappedResponse<Void>> {

        val userId : String = dto.userId!!
        val password : String = dto.password!!

        val userDetails = try{
            userDetailsService.loadUserByUsername(userId)
        } catch (e: UsernameNotFoundException){
//            return ResponseEntity.status(400).build()
            return RestResponseFactory.userFailure("Username not found")
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return RestResponseFactory.noPayload(204)
        }

        return RestResponseFactory.userFailure("User is not logged in")
    }

}
