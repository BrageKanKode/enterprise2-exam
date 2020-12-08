package trips

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
import trips.db.UserTripsRepository
import trips.db.UserTripsService
import trips.dto.CollectionDto
import trips.dto.UserTripsDto
import java.net.URI
import java.util.concurrent.TimeUnit

@Api(value = "/api/cards", description = "Operation on the cards existing in the game")
@RequestMapping(
        path = ["/api/cards"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestApi (
        private val statsRepository: UserTripsRepository,
        private val tripsService: UserTripsService
        ) {

    companion object {
        const val LATEST = "v1_000"
    }


    @ApiOperation("Return info on all cards in the game")
    @GetMapping(
            path = ["/collection_$LATEST"],
            produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getLatest() : ResponseEntity<WrappedResponse<CollectionDto>>{

        val collection = TripCollection.get()

        return ResponseEntity
                .status(200)
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(WrappedResponse(200, collection).validated())
    }

    @ApiOperation("Old-version endpoints. Will automatically redirect to most recent version")
    @GetMapping(path = [
        "/collection_v0_001",
        "/collection_v0_002",
        "/collection_v0_003"
    ])
    fun getOld() : ResponseEntity<Void>{

        return ResponseEntity.status(301)
                .location(URI.create("/api/cards/collection_$LATEST"))
                .build()
    }

    @ApiOperation("Retrieve the current score statistics for the given player")
    @GetMapping(path = ["/{tripId}"])
    fun getTripInfo(
            @PathVariable("tripId") tripId: String
    ): ResponseEntity<WrappedResponse<UserTripsDto>> {

        val trip = statsRepository.findById(tripId).orElse(null)
        if (trip == null) {
            return RestResponseFactory.notFound("Trip $tripId not found")
        }

        return RestResponseFactory.payload(200, DtoConverter.transform(trip))
    }

    @ApiOperation("Create default info for a new player")
    @PutMapping(path = ["/{tripId}"])
    fun createTrip(
            @PathVariable("tripId") tripId: String
    ): ResponseEntity<WrappedResponse<Void>> {
        val ok = tripsService.registerNewTrip(tripId)
        return if (!ok) RestResponseFactory.userFailure("User $tripId already exist")
        else RestResponseFactory.noPayload(201)
    }

    @ApiOperation("Return an iterable page of leaderboard results, starting from the top player")
    @GetMapping
    fun getAll(
            @ApiParam("Id of player in the previous page")
            @RequestParam("keysetId", required = false)
            keysetId: String?,
            //
            @ApiParam("Score of the player in the previous page")
            @RequestParam("keysetScore", required = false)
            keysetScore: Int?): ResponseEntity<WrappedResponse<PageDto<UserTripsDto>>> {

        val page = PageDto<UserTripsDto>()

        val n = 10
        val scores = DtoConverter.transform(tripsService.getNextPage(n, keysetId, keysetScore))
        page.list = scores

        if (scores.size == n) {
            val last = scores.last()
            page.next = "/api/cards?keysetId=${last.tripId}&keysetScore=${last.score}"
        }

        return ResponseEntity
                .status(200)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
                .body(WrappedResponse(200, page).validated())
    }
}
