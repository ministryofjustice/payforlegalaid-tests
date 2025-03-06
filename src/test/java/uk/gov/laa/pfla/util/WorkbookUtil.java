package uk.gov.laa.pfla.util;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.pfla.scenario.ScenarioContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Optional.ofNullable;
import static java.text.MessageFormat.format;

@FunctionalInterface
public interface WorkbookUtil {

    Workbook getExcelWorkbook();

    @SneakyThrows
    default Workbook getExcelWorkbook(ScenarioContext scenarioContext) {
        return ofNullable(scenarioContext.getResponseAs(byte[].class))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(WorkbookUtil::createWorkbookFromBytes)
                .orElseThrow(() -> new RuntimeException("Failed to download the file or invalid response."));
    }

    @SneakyThrows
    default Workbook loadExcelFromResources(String pattern, Object ... arguments) {
        return ofNullable(loadResourceAsBytes(format(pattern, arguments)))
                .map(WorkbookUtil::createWorkbookFromBytes)
                .orElseThrow(() -> new RuntimeException("Failed to load the file."));
    }

    @SneakyThrows
    private static Workbook createWorkbookFromBytes(byte[] bytes) {
        try (var bis = new ByteArrayInputStream(bytes)) {
            return new XSSFWorkbook(bis);
        }
    }

    private byte[] loadResourceAsBytes(String fileName) {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return inputStream == null ? null : inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + fileName, e);
        }
    }

}
