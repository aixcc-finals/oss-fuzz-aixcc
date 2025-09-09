import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.hertzbeat.manager.service.impl.AbstractImExportServiceImpl;
import org.apache.hertzbeat.manager.service.impl.ExcelImExportServiceImpl;
import java.lang.RuntimeException;

public class ExcelImExportServiceFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        ExcelImExportServiceImpl excelImExportService = new ExcelImExportServiceImpl();
        ByteArrayInputStream bis = new ByteArrayInputStream(data.consumeRemainingAsBytes());
        try {
            List<AbstractImExportServiceImpl.ExportMonitorDTO> result = excelImExportService.parseImport(bis);
        } catch(RuntimeException e) {

        }
    }
}
