import candlestick.CandlestickAggregator
import candlestick.CandlestickApi
import dao.InstrumentDAO
import dao.QuoteDAO
import event.EventManager

fun main() {
    println("starting up")

    val quoteDAO = QuoteDAO()
    val instrumentDAO = InstrumentDAO()
    val objectMapper = ObjectMapper()
    val timestampManager = TimestampManager()
    val candlestickAggregator = CandlestickAggregator(quoteDAO, timestampManager)
    val candlestickApi = CandlestickApi(candlestickAggregator, objectMapper)

    Server(candlestickApi).start()

    EventManager(instrumentDAO, quoteDAO, objectMapper).start()
}
