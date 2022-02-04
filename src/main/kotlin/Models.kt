import java.time.Clock
import java.time.Instant

data class Instrument(
    val isin: String,
    val description: String,
    val timestamp: Instant = Instant.now(Clock.systemUTC())
)

data class Quote(val isin: String, val price: Double, val timestamp: Instant = Instant.now(Clock.systemUTC()))