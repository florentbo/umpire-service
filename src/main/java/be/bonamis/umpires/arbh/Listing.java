package be.bonamis.umpires.arbh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
    import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Getter
@Slf4j
public class Listing {
    private final Collection<Umpire> umpires;

    public Listing(InputStream inputStream) {
        this.umpires = initUmpires(inputStream);
    }

    private Collection<Umpire> initUmpires(InputStream inputStream) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);

            List<XSSFSheet> sheets = Arrays.asList(wb.getSheetAt(0), wb.getSheetAt(1), wb.getSheetAt(2));
            return sheets
                    .stream()
                    .flatMap((Function<XSSFSheet, Stream<Row>>) sheet -> StreamSupport.stream(sheet.spliterator(), false))
                    .filter(row -> row.getRowNum() > 0)
                    .map(row -> new Umpire(getCell(row, 1), getCell(row, 2),
                            getCell(row, 4), getCell(row, 5)))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("problem with the Listing. IOException is: {}", e);
            return Collections.emptySet();
        }
    }

    private String getCell(Row row, int i) {
        try {
            Cell cell = row.getCell(i);
            return cell!=null ? cell.getStringCellValue() : "";
        } catch (Exception e) {
            //log.error("problem with the Listing. IOException is: {}" ,  e);
            return "";
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class Umpire {
        private String name;
        private String first;
        private String eMail;
        private String mobile;
    }

}
