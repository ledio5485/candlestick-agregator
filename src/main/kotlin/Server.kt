import candlestick.CandlestickApi
import org.http4k.core.Method
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer

class Server(private val candlestickApi: CandlestickApi) {

    fun start() =
        routes(
            "candlesticks" bind Method.GET to { candlestickApi.getCandlesticks(it) }
        )
            .asServer(Netty(SERVER_PORT))
            .start()
            .also { println("server started at port $SERVER_PORT") }
}
