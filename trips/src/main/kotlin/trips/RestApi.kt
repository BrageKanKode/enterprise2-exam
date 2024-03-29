package trips
//https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/master/advanced/exercise-solutions/card-game/part-10/scores/src/main/kotlin/org/tsdes/advanced/exercises/cardgame/scores/RestApi.kt
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rest.PageDto
import rest.RestResponseFactory
import rest.WrappedResponse
import trips.db.Trips
import trips.db.TripsRepository
import trips.db.TripsService
import trips.dto.Command
import trips.dto.PatchTripsDto
import trips.dto.TripDto
import java.lang.IllegalArgumentException
import java.net.URI
import java.util.concurrent.TimeUnit

@Api(value = "/api/trips", description = "Operation on the trips existing")
@RequestMapping(
        path = ["/api/trips"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestApi (
        private val statsRepository: TripsRepository,
        private val tripsService: TripsService
        ) {

    companion object {
        const val LATEST = "v1_000"
    }


    @ApiOperation("Return info on all trips")
    @GetMapping(
            path = ["/collection_$LATEST"],
            produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getLatest() : ResponseEntity<WrappedResponse<Iterable<Trips>>>{

        val collection = statsRepository.findAll()

        return ResponseEntity
                .status(200)
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(WrappedResponse(200, collection).validated())
    }

    @ApiOperation("Edit existing value for trip")
    @PatchMapping(
            path = ["/{tripId}"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)]
    )
    fun alterTripInfo(
            @PathVariable("tripId") tripId: String,
            @RequestBody dto: PatchTripsDto
    ) : ResponseEntity<WrappedResponse<Void>> {

        if(dto.command == null){
            return RestResponseFactory.userFailure("Missing command")
        }
        val tripId = dto.tripId
                ?: return RestResponseFactory.userFailure("Missing trip id")

        if(dto.command == Command.ALTER_TRIP_COST ){
            val cost = dto.cost
                    ?: return RestResponseFactory.userFailure("Missing cost of trip")
            try{
                tripsService.alterTripCost(tripId, cost)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to change cost of trip $tripId")
            }
            return RestResponseFactory.noPayload(201)
        }
        if(dto.command == Command.ALTER_TRIP_PLACE ){
            val place = dto.place
                    ?: return RestResponseFactory.userFailure("Missing place of trip")
            try{
                tripsService.alterTripPlace(tripId, place)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to change place of trip $tripId")
            }
            return RestResponseFactory.noPayload(201)
        }
        if(dto.command == Command.ALTER_TRIP_DURATION ){
            val duration = dto.duration
                    ?: return RestResponseFactory.userFailure("Missing duration of trip")
            try{
                tripsService.alterTripDuration(tripId, duration)
            } catch (e: IllegalArgumentException){
                return RestResponseFactory.userFailure(e.message ?: "Failed to change duration of trip $tripId")
            }
            return RestResponseFactory.noPayload(201)
        }



        return RestResponseFactory.userFailure("Unrecognized command: ${dto.command}")
    }

    @ApiOperation("Retrieve the current trip info for the given player")
    @GetMapping(path = ["/{tripId}"])
    fun getTripInfo(
            @PathVariable("tripId") tripId: String
    ): ResponseEntity<WrappedResponse<TripDto>> {

        val trip = statsRepository.findById(tripId).orElse(null)
        if (trip == null) {
            return RestResponseFactory.notFound("Trip $tripId not found")
        }

        return RestResponseFactory.payload(200, DtoConverter.transform(trip))
    }

    @ApiOperation("Create default info for a new player")
    @PutMapping(
            path = ["/{tripId}"],
            consumes = [(MediaType.APPLICATION_JSON_VALUE)]
    )
    fun createTrip(
            @PathVariable("tripId") tripId: String,
            @RequestBody dto: TripDto
    ): ResponseEntity<WrappedResponse<Void>> {

        if (dto.tripId == null || dto.place == null || dto.duration == null || dto.cost == null) {
            return RestResponseFactory.userFailure("Missing parameters to create trip $tripId")
        }

        val ok = tripsService.registerNewTrip(tripId, dto.place!!, dto.duration!!, dto.cost!!)
        return if (!ok) RestResponseFactory.userFailure("Trip $tripId already exist")
        else RestResponseFactory.noPayload(201)
    }

    @ApiOperation("Return an iterable page of trips, starting from the highest price")
    @GetMapping
    fun getAll(
            @ApiParam("Id of player in the previous page")
            @RequestParam("keysetId", required = false)
            keysetId: String?,
            //
            @ApiParam("Score of the player in the previous page")
            @RequestParam("keysetScore", required = false)
            keysetScore: Int?): ResponseEntity<WrappedResponse<PageDto<TripDto>>> {

        val page = PageDto<TripDto>()

        val n = 10
        val scores = DtoConverter.transform(tripsService.getNextPage(n, keysetId, keysetScore))
        page.list = scores

        if (scores.size == n) {
            val last = scores.last()
            page.next = "?keysetId=${last.tripId}&keysetScore=${last.cost}"
        }

        return ResponseEntity
                .status(200)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
                .body(WrappedResponse(200, page).validated())
    }
}
