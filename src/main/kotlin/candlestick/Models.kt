package candlestick

import Quote
import java.time.Instant

data class Candlestick(
    val openTimestamp: String,
    val closeTimestamp: String,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closingPrice: Double
) {
    companion object {
        fun from(openTimestamp: Instant, closeTimestamp: Instant) =
            Candlestick(
                openTimestamp = openTimestamp.toString(),
                closeTimestamp = closeTimestamp.toString(),
                openPrice = -1.0,
                highPrice = -1.0,
                lowPrice = -1.0,
                closingPrice = -1.0
            )

        fun from(openTimestamp: Instant, closeTimestamp: Instant, quotes: List<Quote>) =
            Candlestick(
                openTimestamp = openTimestamp.toString(),
                closeTimestamp = closeTimestamp.toString(),
                openPrice = quotes.first().price,
                highPrice = quotes.map { it.price }.maxOrNull() ?: -1.0,
                lowPrice = quotes.map { it.price }.minOrNull() ?: -1.0,
                closingPrice = quotes.last().price
            )
    }
}