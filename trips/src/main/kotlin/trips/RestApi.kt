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
import trips.db.Trips
import trips.db.UserTripsRepository
import trips.db.TripsService
import trips.dto.CollectionDto
import trips.dto.TripDto
//import trips.dto.TripsDto
import java.net.URI
import java.util.concurrent.TimeUnit

@Api(value = "/api/trips", description = "Operation on the trips existing")
@RequestMapping(
        path = ["/api/trips"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestApi (
        private val statsRepository: UserTripsRepository,
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

    @ApiOperation("Old-version endpoints. Will automatically redirect to most recent version")
    @GetMapping(path = [
        "/collection_v0_001",
        "/collection_v0_002",
        "/collection_v0_003"
    ])
    fun getOld() : ResponseEntity<Void>{

        return ResponseEntity.status(301)
                .location(URI.create("/api/trips/collection_$LATEST"))
                .build()
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
