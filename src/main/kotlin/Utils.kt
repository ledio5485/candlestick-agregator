import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Duration
import java.time.Instant
import java.util.*

fun ObjectMapper(): ObjectMapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun generateTimestamps(instant: Instant, windowInterval: Duration, candlestickInterval: Duration): List<Instant> {
    var currentTimestamp = instant.minus(windowInterval)

    val timeRanges = LinkedList<Instant>()
    while (currentTimestamp <= instant) {
        timeRanges.add(currentTimestamp)
        currentTimestamp = currentTimestamp.plus(candlestickInterval)
    }

    return timeRanges
}