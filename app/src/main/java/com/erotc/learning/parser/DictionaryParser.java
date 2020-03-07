package com.erotc.learning.parser;

import com.erotc.learning.dao.DictionaryEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

/**
 * Created on 11/5/2018.
 */
public class DictionaryParser {
    private static int ROW_TAGALOG = 0;
    private static int ROW_DEFINITION = 1;
    private static int ROW_HILIGAYNON = 2;
    private static int ROW_ILOCANO = 3;
    private static int ROW_TAGALOG_EXAMPLE = 4;
    private static int ROW_HILIGAYNON_EXAMPLE = 5;
    private static int ROW_ILOCANO_EXAMPLE = 6;

    private InputStream inputStream;
    private CsvReader reader;

    public DictionaryParser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.reader =  new CsvReader();
    }

    public ArrayList<DictionaryEntry> parse() throws IOException {
        ArrayList<DictionaryEntry> entries = new ArrayList<>();

        CsvParser parser = reader.parse(new InputStreamReader(inputStream));

        CsvRow row;
        while ((row = parser.nextRow()) != null){
            DictionaryEntry entry = new DictionaryEntry();
            entry.setTagalog(row.getField(ROW_TAGALOG));
            entry.setHiligaynon(row.getField(ROW_HILIGAYNON));
            entry.setIlocano(row.getField(ROW_ILOCANO));

            entry.setDefinition(row.getField(ROW_DEFINITION));

            entry.setTagalogExample(row.getField(ROW_TAGALOG_EXAMPLE));
            entry.setHiligaynonExample(row.getField(ROW_HILIGAYNON_EXAMPLE));
            entry.setIlocanoExample(row.getField(ROW_ILOCANO_EXAMPLE));

            entries.add(entry);
        }

        return entries;
    }
}
