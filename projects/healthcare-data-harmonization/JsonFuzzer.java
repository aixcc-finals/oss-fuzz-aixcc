import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.google.cloud.verticals.foundations.dataharmonization.data.Data;
import com.google.cloud.verticals.foundations.dataharmonization.data.serialization.impl.JsonSerializerDeserializer;
import com.google.gson.JsonSyntaxException;

public class JsonFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
            try {
                Data d = JsonSerializerDeserializer.jsonToData(data.consumeRemainingAsBytes());
            } catch(JsonSyntaxException e) {
            }
    }
}
