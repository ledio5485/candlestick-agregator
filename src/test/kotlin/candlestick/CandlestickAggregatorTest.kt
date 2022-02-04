package candlestick

import Quote
import TimestampManager
import dao.QuoteDAO
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
internal class CandlestickAggregatorTest(
    @MockK private val quoteDAO: QuoteDAO,
    @MockK private val timestampManager: TimestampManager
) {
    private val fixedInstant = Instant.now(Clock.systemUTC())

    private val candlestickAggregator = CandlestickAggregator(quoteDAO, timestampManager)

    @BeforeEach
    internal fun setUp() {
        every { timestampManager.now() } returns fixedInstant
    }

    @Test
    internal fun `should return empty collection when no quotes found`() {
        val isin = "AL1234567890"
        val windowInterval = Duration.ofSeconds(2)
        val candlestickInterval = Duration.ofSeconds(1)
        every { quoteDAO.findByIsinAndTimestampBetween(isin, now().minus(windowInterval), now()) } returns emptyList()

        val actual = candlestickAggregator.getCandlesticks(isin, windowInterval, candlestickInterval)

        assertTrue { actual.isEmpty() }
    }

    @Test
    internal fun `should not filter the quotes with different isin`() {
        val isin = "AL1234567890"
        val isin2 = "AL0123456789"
        val windowInterval = Duration.ofSeconds(2)
        val candlestickInterval = Duration.ofSeconds(1)
        val quote = Quote(isin, 1.23, now().minusMillis(1500))

        every { quoteDAO.findByIsinAndTimestampBetween(isin, now().minus(windowInterval), now()) } returns listOf(quote)
        every { quoteDAO.findByIsinAndTimestampBetween(isin2, now().minus(windowInterval), now()) } returns listOf()

        val actual = candlestickAggregator.getCandlesticks(isin2, windowInterval, candlestickInterval)

        assertTrue { actual.isEmpty() }
    }

    @Test
    internal fun `should return the Candlestick of the previous interval when no quotes are found in the current one`() {
        val isin = "AL1234567890"
        val windowInterval = Duration.ofSeconds(2)
        val candlestickInterval = Duration.ofSeconds(1)
        val quote = Quote(isin, 1.23, now().minusMillis(1500))

        every { quoteDAO.findByIsinAndTimestampBetween(isin, now().minus(windowInterval), now()) } returns listOf(quote)

        val actual = candlestickAggregator.getCandlesticks(isin, windowInterval, candlestickInterval)

        val expected = listOf(
            Candlestick(now().minusSeconds(2).toString(), now().minusSeconds(1).toString(), 1.23, 1.23, 1.23, 1.23),
            Candlestick(now().minusSeconds(1).toString(), now().toString(), 1.23, 1.23, 1.23, 1.23)
        )
        assertEquals(actual, expected)
    }

    @Test
    internal fun `should return the Candlesticks correctly given an isin and a timestamp`() {
        val isin = "AL1234567890"
        val windowInterval = Duration.ofSeconds(2)
        val candlestickInterval = Duration.ofSeconds(1)

        val quote1 = Quote(isin, 1.35, now().minusMillis(1500))
        val quote2 = Quote(isin, 1.25, now().minusMillis(1300))

        val quote3 = Quote(isin, 1.35, now().minusMillis(400))
        val quote4 = Quote(isin, 1.5, now().minusMillis(500))
        val quote5 = Quote(isin, 1.25, now().minusMillis(800))

        every { quoteDAO.findByIsinAndTimestampBetween(isin, now().minus(windowInterval), now()) } returns listOf(quote1, quote2, quote3, quote4, quote5)

        val actual = candlestickAggregator.getCandlesticks(isin, windowInterval, candlestickInterval)

        val expected = listOf(
            Candlestick(now().minusSeconds(2).toString(), now().minusSeconds(1).toString(), 1.35, 1.35, 1.25, 1.25),
            Candlestick(now().minusSeconds(1).toString(), now().toString(), 1.25, 1.5, 1.25, 1.35)
        )
        assertEquals(actual, expected)
    }

    private fun now() = fixedInstant.truncatedTo(ChronoUnit.SECONDS).plusSeconds(1)
}