package br.ufsc.ine.ppgcc.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Classe auxiliar responsável por manipular arquivos CSV
 */
@Component
public class CsvUtil {

    public double[][] readFile(String path) {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        List<double[]> data = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(path)).withCSVParser(parser).build()) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                data.add(Arrays.stream(values).flatMapToDouble(v -> DoubleStream.of(Double.parseDouble(v))).toArray());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        double[][] dataArray = new double[data.size()][];
        data.toArray(dataArray);
        return dataArray;
    }
}
