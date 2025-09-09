import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import pt.ua.dicoogle.core.dicom.PrivateDictionary;

public class PrivateDictionaryFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
	try {
	  PrivateDictionary pd = new PrivateDictionary();
	  FileOutputStream out = new FileOutputStream("file");
	  out.write(data.consumeRemainingAsBytes());
	  out.close();
	  pd.parse("file");
	} catch(IOException e) {
	}
    }
}
