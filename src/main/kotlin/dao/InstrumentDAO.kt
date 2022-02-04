package dao

import Instrument
import java.util.concurrent.ConcurrentHashMap

interface InstrumentDAO {
    fun get(isin: String): Instrument?
    fun add(instrument: Instrument): Instrument
    fun delete(isin: String): Instrument?
}

fun InstrumentDAO(): InstrumentDAO = InMemoryInstrumentDAO()

private class InMemoryInstrumentDAO : InstrumentDAO {
    private val instruments = ConcurrentHashMap<String, Instrument>()

    override fun get(isin: String) = instruments[isin]

    override fun add(instrument: Instrument): Instrument {
        instruments[instrument.isin] = instrument
        return instrument
    }

    override fun delete(isin: String): Instrument? = instruments.remove(isin)
}