package candlestick

import TimestampManager
import dao.QuoteDAO
import generateTimestamps
import java.time.Duration
import java.time.temporal.ChronoUnit

interface CandlestickAggregator {
    fun getCandlesticks(
        isin: String,
        windowInterval: Duration = Duration.ofMinutes(30),
        candlestickInterval: Duration = Duration.ofMinutes(1)
    ): List<Candlestick>
}

fun CandlestickAggregator(
    quoteDAO: QuoteDAO = QuoteDAO(),
    timestampManager: TimestampManager = TimestampManager()
): CandlestickAggregator {
    return DefaultCandlestickAggregator(quoteDAO, timestampManager)
}

private class DefaultCandlestickAggregator(
    private val quoteDAO: QuoteDAO,
    private val timestampManager: TimestampManager
) : CandlestickAggregator {

    override fun getCandlesticks(
        isin: String,
        windowInterval: Duration,
        candlestickInterval: Duration
    ): List<Candlestick> {
        val now = getCurrentInstant()

        val timestamps = generateTimestamps(now, windowInterval, candlestickInterval)
        val groupedQuotes = quoteDAO.findByIsinAndTimestampBetween(isin, now.minus(windowInterval), now)
            .also { if (it.isEmpty()) return emptyList() }
            .sortedBy { it.timestamp }
            .groupBy { quote -> timestamps.binarySearch { quote.timestamp.compareTo(it).times(-1) } }
            .mapKeys { it.key.times(-1).minus(2) }

        val candlesticks = ArrayList<Candlestick>()
        for (i in 0 until timestamps.size - 1) {
            val currentOpenTimestamp = timestamps[i]
            val candlestick: Candlestick =
                if (groupedQuotes[i].isNullOrEmpty()) {
                    if (i == 0) {
                        Candlestick.from(currentOpenTimestamp, currentOpenTimestamp.plus(candlestickInterval))
                    } else {
                        candlesticks[i - 1].copy(
                            openTimestamp = currentOpenTimestamp.toString(),
                            closeTimestamp = currentOpenTimestamp.plus(candlestickInterval).toString()
                        )
                    }
                } else {
                    Candlestick.from(
                        currentOpenTimestamp,
                        currentOpenTimestamp.plus(candlestickInterval),
                        groupedQuotes[i]!!
                    )
                }
            candlesticks.add(candlestick)
        }
        return candlesticks
    }

    private fun getCurrentInstant() = timestampManager.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1)
}