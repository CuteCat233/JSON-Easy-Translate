package Service;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;

/**
 * A class for file operations.
 * 
 * @version 1.0
 * @author KQ.W
 * @since 2025-09-01
 */
public class FileService {
    /** The file to operate on */
    private File file;
    private java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<LinkedHashMap<String, String>>(){}.getType();

    /**
     * Constructor for FileOperator.
     * @param file The file to operate on.
     */
    public FileService() {
    }

    /**
     * Gets the file to operate on.
     * @return The file to operate on.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file to operate on.
     * @param file The file to operate on.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the Gson representation of the file.
     * @return The Gson representation of the file.
     */
    public LinkedHashMap<String, String> getJson() {
        System.out.println("Reading JSON from file: " + file.getAbsolutePath());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            // 读取整个文件内容为字符串
            String rawJson = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            // 清理结尾多余的逗号：处理对象结尾的 ",}" 和数组结尾的 ",]"
            String cleanedJson = rawJson
                .replaceAll(",\\s*}", "}")
                .replaceAll(",\\s*]", "]");

            // 使用 JsonReader 包装字符串
            JsonReader jr = new JsonReader(new StringReader(cleanedJson));
            jr.setStrictness(Strictness.LENIENT);
            map = gson.fromJson(jr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Saves the Gson representation of the given map to the file.
     * @param map The map to save.
     */
    public void writeJson(LinkedHashMap<String, String> map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(map, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
