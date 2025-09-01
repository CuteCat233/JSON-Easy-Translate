package app.Test;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class FileTest {
    @Test
    public void testGetGson() {
        File file = new File("E:\\zLewdDewValley\\LewdDew_Valley\\i18n\\default.json");
        HashMap<String, String> map = new HashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<HashMap<String, String>>(){}.getType();
        try {
            JsonReader jr = new JsonReader(new FileReader(file));
            map = gson.fromJson(jr, type);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(map);
    }

    @Test
    public void testWriteGson() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        Gson gson = new Gson();
        String json = gson.toJson(map);
        assertNotNull(json);
        System.out.println(json);
        assertTrue(json.contains("\"key1\":\"value1\""));
        assertTrue(json.contains("\"key2\":\"value2\""));
    }
}
