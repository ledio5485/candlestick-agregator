package candlestick

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import java.time.Duration

private const val DEFAULT_WINDOW_INTERVAL = "PT30M"
private const val DEFAULT_CANDLESTICK_INTERVAL = "PT1M"

interface CandlestickApi {
    fun getCandlesticks(req: Request): Response
}

fun CandlestickApi(
    candlestickAggregator: CandlestickAggregator,
    objectMapper: ObjectMapper
): CandlestickApi =
    CandlestickApiRest(candlestickAggregator, objectMapper)

private class CandlestickApiRest(
    private val candlestickAggregator: CandlestickAggregator,
    private val objectMapper: ObjectMapper
) : CandlestickApi {
    override fun getCandlesticks(req: Request): Response {
        val isin = req.query("isin") ?: return Response(Status.BAD_REQUEST).body("{'reason': 'missing isin'}")
        val windowInterval = Duration.parse(req.query("windowInterval") ?: DEFAULT_WINDOW_INTERVAL)
        val candlestickInterval = Duration.parse(req.query("candlestickInterval") ?: DEFAULT_CANDLESTICK_INTERVAL)

        if (windowInterval < candlestickInterval) {
            return Response(Status.BAD_REQUEST).body("{'reason': 'windowInterval < candlestickInterval'}")
        }

        val candlesticks = candlestickAggregator.getCandlesticks(isin, windowInterval, candlestickInterval)

        return Response(Status.OK).body(objectMapper.writeValueAsBytes(candlesticks).inputStream())
    }
}