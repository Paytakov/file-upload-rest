package com.omnilinx.file_upload_rest_svc.helper;

import com.omnilinx.file_upload_rest_svc.model.dto.PlayerDto;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvPlayerFileParserTest {

    private CsvPlayerFileParser parser = new CsvPlayerFileParser();

    @Test
    void testParseShouldReturnCorrectDataWhenCsvIsValid() {
        String csv = """
                name,position,age,team,country
                Steven,GK,27,Test FC,TestC
                Pesho,AM,30,Test_1 FC,TestC_2
                """;
        byte[] content = csv.getBytes(StandardCharsets.UTF_8);

        List<PlayerDto> result = parser.parse(content);

        assertEquals(2, result.size());

        PlayerDto first = result.get(0);
        assertEquals("Steven", first.getName());
    }

    @Test
    void testParseShouldSkipInvalidLine() {
        String csv = """
                name,position,age,team,country
                Steven,GK,27,Test FC,Bulgaria
                Invalid,Line
                Pesho,AM,30,Test_1 FC,Test_1_C
                """;
        byte[] content = csv.getBytes(StandardCharsets.UTF_8);

        List<PlayerDto> result = parser.parse(content);

        assertEquals(2, result.size());
    }

    @Test
    void testParseShouldSkipLineWithInvalidNumber() {
        String csv = """
                name,position,age,team,country
                Steven,GK,notANumber,Test_1 FC,Test_1_C
                Pesho,AM,30,Test_2 FC,TestC_2
                """;
        byte[] content = csv.getBytes(StandardCharsets.UTF_8);

        List<PlayerDto> result = parser.parse(content);

        assertEquals(1, result.size());
        assertEquals("Pesho", result.get(0).getName());
    }

    @Test
    void testParseShouldThrowWhenContentIsNotValid() {

        // TODO: Find a better solution
        assertThrows(RuntimeException.class, () -> parser.parse(null));
    }
}