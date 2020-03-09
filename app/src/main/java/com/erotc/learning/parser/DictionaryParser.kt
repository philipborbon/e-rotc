package com.erotc.learning.parser

import com.erotc.learning.data.DictionaryEntry
import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRow
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created on 11/5/2018.
 */
class DictionaryParser(private val inputStream: InputStream) {
    private val reader: CsvReader = CsvReader()

    fun parse(): ArrayList<DictionaryEntry> {
        val entries = ArrayList<DictionaryEntry>()
        val parser = reader.parse(InputStreamReader(inputStream))
        var row: CsvRow?
        while (parser.nextRow().also { row = it } != null) {
            val entry = DictionaryEntry()
            entry.tagalog = row?.getField(ROW_TAGALOG)
            entry.hiligaynon = row?.getField(ROW_HILIGAYNON)
            entry.ilocano = row?.getField(ROW_ILOCANO)
            entry.definition = row?.getField(ROW_DEFINITION)
            entry.tagalogExample = row?.getField(ROW_TAGALOG_EXAMPLE)
            entry.hiligaynonExample = row?.getField(ROW_HILIGAYNON_EXAMPLE)
            entry.ilocanoExample = row?.getField(ROW_ILOCANO_EXAMPLE)
            entries.add(entry)
        }

        return entries
    }

    companion object {
        private const val ROW_TAGALOG = 0
        private const val ROW_DEFINITION = 1
        private const val ROW_HILIGAYNON = 2
        private const val ROW_ILOCANO = 3
        private const val ROW_TAGALOG_EXAMPLE = 4
        private const val ROW_HILIGAYNON_EXAMPLE = 5
        private const val ROW_ILOCANO_EXAMPLE = 6
    }
}