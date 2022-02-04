import java.time.Clock
import java.time.Instant

interface TimestampManager {
    fun now(): Instant
}

fun TimestampManager(): TimestampManager = TimestampManagerImpl()

private class TimestampManagerImpl : TimestampManager {
    override fun now(): Instant = Instant.now(Clock.systemUTC())
}
