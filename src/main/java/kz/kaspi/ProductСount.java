package kz.kaspi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ProductСount {
    private static final Logger logger = LoggerFactory.getLogger(RemoveDuplicate.class);

    /**
     * Метод для подсчета количества товаров и записи результатов в файл.
     * @param inputFileName Имя входного файла с данными о товарах.
     * @param outputFileName Имя выходного файла для записи результатов подсчета.
     */
    public static void productСount(String inputFileName, String outputFileName) {

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

            Map<String, Integer> productCountMap = new HashMap<>();

            String firstLine = reader.readLine();
            if (firstLine != null) {
                writer.write(firstLine);
                writer.write("\n\n");
            }

            String line;
            int linesToSkip = 2;
            int currentLine = 0;

            while ((line = reader.readLine()) != null) {
                currentLine++;

                if (currentLine <= linesToSkip) {
                    continue;
                }

                line = line.trim();

                int count = productCountMap.getOrDefault(line, 0);
                productCountMap.put(line, count + 1);
            }

            for (Map.Entry<String, Integer> entry : productCountMap.entrySet()) {
                String productName = entry.getKey();
                int count = entry.getValue();

                writer.write(productName + " Количество товаров: " + count);
                writer.newLine();
            }

            logger.info("Количество товаров посчитано");

        } catch (IOException e) {
            logger.error("Ошибка при подсчете товаров", e);
        }
    }
}