package event

import PARTNER_SERVICE_URI
import com.fasterxml.jackson.databind.ObjectMapper
import dao.InstrumentDAO
import dao.QuoteDAO

class EventManager(
    private val instrumentDAO: InstrumentDAO,
    private val quoteDAO: QuoteDAO,
    private val objectMapper: ObjectMapper
) {

    fun start() {
        InstrumentStream(PARTNER_SERVICE_URI, objectMapper)
            .connect { event ->
                println(event)
                when (event.type) {
                    InstrumentEvent.Type.ADD -> instrumentDAO.add(event.data)
                    InstrumentEvent.Type.DELETE -> {
                        val isin = event.data.isin
                        instrumentDAO.delete(isin)
                        quoteDAO.deleteAllByIsin(isin)
                    }
                }
            }

        QuoteStream(PARTNER_SERVICE_URI, objectMapper)
            .connect { event ->
                println(event)
                instrumentDAO.get(event.data.isin)?.let { quoteDAO.add(event.data) }
            }
    }
}