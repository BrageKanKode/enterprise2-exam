package trips

import trips.db.UserStatss
import trips.dto.UserStatsDto

object DtoConverter {

    fun transform(statss: UserStatss) : UserStatsDto =
            statss.run { UserStatsDto(userId, victories, defeats, draws, score)}

    fun transform(scores: Iterable<UserStatss>) : List<UserStatsDto> = scores.map { transform(it) }
}
