package dao

import Quote
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant

internal class QuoteDAOTest {
    private val now = Instant.now(Clock.systemUTC())

    private val dao = QuoteDAO()

    @Test
    internal fun `should add a new quote`() {
        val isin = "AL1234567890"
        val quote = Quote(isin, 12.34, now)

        val actual = dao.add(quote)

        val expected = dao.findByIsinAndTimestampBetween(isin, now.minusNanos(1), now.plusNanos(1))

        assertTrue(expected.size == 1)
        assertEquals(expected.single(), actual)
    }

    @Test
    internal fun `should find by isin and timestamp in given time range`() {
        val isin0 = "AL1234567890"
        val isin1 = "AL0123456789"
        val quote0 = Quote(isin0, 12.34, now)
        val quote1 = Quote(isin1, 12.34, now)
        val quote2 = Quote(isin1, 56.78, now.minusNanos(1))
        val quote3 = Quote(isin1, 90.12, now.plusNanos(1))
        val quote4 = Quote(isin1, 56.78, now.minusSeconds(1))
        val quote5 = Quote(isin1, 90.12, now.plusSeconds(1))

        dao.add(quote0)
        dao.add(quote1)
        dao.add(quote2)
        dao.add(quote3)
        dao.add(quote4)
        dao.add(quote5)

        val actual = dao.findByIsinAndTimestampBetween(isin1, now.minusMillis(1), now.plusMillis(1))

        val expected = listOf(quote1, quote2, quote3)
        assertEquals(expected, actual)
    }

    @Test
    internal fun `should delete all by isin`() {
        val isin = "AL1234567890"
        val quote0 = Quote(isin, 12.34, now)
        val quote1 = Quote(isin, 56.78, now.minusNanos(1))
        val quote2 = Quote(isin, 90.12, now.plusNanos(1))

        dao.add(quote0)
        dao.add(quote1)
        dao.add(quote2)

        val expected = dao.findByIsinAndTimestampBetween(isin, now.minusMillis(1), now.plusMillis(1))

        val expectedQuotes = listOf(quote0, quote1, quote2)
        assertEquals(expected, expectedQuotes)

        val actual = dao.deleteAllByIsin(isin)

        assertEquals(expectedQuotes, actual)

        val quotesAfterDelete = dao.findByIsinAndTimestampBetween(isin, now.minusMillis(1), now.plusMillis(1))
        assertTrue(quotesAfterDelete.isEmpty())
    }
}