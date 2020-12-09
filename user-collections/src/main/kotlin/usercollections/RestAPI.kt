package usercollections

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rest.RestResponseFactory
import rest.WrappedResponse
import usercollections.db.UserService
import usercollections.dto.Command
import usercollections.dto.PatchResultDto
import usercollections.dto.PatchUserDto
import usercollections.dto.UserDto
import java.lang.IllegalArgumentException

@Api(value = "/api/user-collections", description = "Operations on card collections owned by users")
@RequestMapping(
        path = ["/api/user-collections"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestAPI(
        private val userService: UserService
) {

    @ApiOperation("Retrieve card collection information for a specific user")
    @GetMapping(path = ["/{userId}"])
    fun getUserInfo(
            @PathVariable("userId") userId: String
    ) : ResponseEntity<WrappedResponse<UserDto>> {

        val user = userService.findByIdEager(userId)
        if(user == null){
            return RestResponseFactory.notFound("User $userId not found")
        }

        return RestResponseFactory.payload(200, DtoConverter.transform(user))
    }

    @ApiOperation("Delete a user, with the given id")
    @DeleteMapping(path = ["/{userId}"])
    fun deleteUser(
            @PathVariable("userId") userId: String
    ): ResponseEntity<WrappedResponse<Void>> {
        val ok = userService.deleteUser(userId)
        return if(!ok) RestResponseFactory.userFailure("User $userId doesn't exist")
        else RestResponseFactory.noPayload(201)
    }

    @ApiOperation("Create a new user, with the given id")
    @PutMapping(path = ["/{userId}"])
    fun createUser(
            @PathVariable("userId") userId: String
    ): ResponseEntity<WrappedResponse<Void>> {
        val ok = userService.registerNewUser(userId)
        return if(!ok)  RestResponseFactory.userFailure("User $userId already exist")
        else RestResponseFactory.noPayload(201)
    }

    @ApiOperation("Execute a command on a user's collection, like for example buying/milling cards")
    @PatchMapping(
            path = ["/{userId}"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)]
    )
    fun patchUser(
            @PathVariable("userId") userId: String,
            @RequestBody dto: PatchUserDto
    ): ResponseEntity<WrappedResponse<PatchResultDto>> {

        if(dto.command == null){
            return RestResponseFactory.userFailure("Missing command")
        }

        if(dto.command == Command.OPEN_PACK){
            val ids = try {
                userService.openPack(userId)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to open pack")
            }
            return RestResponseFactory.payload(200, PatchResultDto().apply { cardIdsInOpenedPack.addAll(ids) })
        }

        val tripId = dto.cardId
                ?: return RestResponseFactory.userFailure("Missing trip id")

        if(dto.command == Command.BUY_CARD){
            try{
                userService.buyTrip(userId, tripId)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to buy trip $tripId")
            }
            return RestResponseFactory.payload(200, PatchResultDto())
        }

        if(dto.command == Command.MILL_CARD){
            try{
                userService.millCard(userId, tripId)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to mill trip $tripId")
            }
            return RestResponseFactory.payload(200, PatchResultDto())
        }

        return RestResponseFactory.userFailure("Unrecognized command: ${dto.command}")
    }
}