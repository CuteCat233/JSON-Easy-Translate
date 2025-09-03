package Setting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * A class for managing application settings.
 * 
 * @version 1.0
 * @author CuteCat233
 * @since 2025-09-03
 */
public class SettingControl {
    private String apiKey;
    private String language;
    private String lastOpenPath;
    private String outputPath;
    private boolean jsonKeyDisplay;
    private boolean emptyTranslationDisplay;
    private final static File SETTING_FILE = new File("settings.json");

    public SettingControl(){
        this.apiKey = "";
        this.language = "";
        this.lastOpenPath = "";
        this.outputPath = "";
        this.jsonKeyDisplay = true;
        this.emptyTranslationDisplay = false;
    }

    public void loadSettings() {
        if (SETTING_FILE.exists()) {
            if (SETTING_FILE.length() == 0) {
                System.out.println("Settings file is empty, using default settings.");
                saveSettings();
                return;
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println("Reading JSON from file: " + SETTING_FILE.getAbsolutePath());
            try (JsonReader reader = new JsonReader(new FileReader(SETTING_FILE))) {
                SettingControl settings = gson.fromJson(reader, SettingControl.class);
                this.apiKey = settings.apiKey;
                this.language = settings.language;
                this.lastOpenPath = settings.lastOpenPath;
                this.outputPath = settings.outputPath;
                this.jsonKeyDisplay = settings.jsonKeyDisplay;
                this.emptyTranslationDisplay = settings.emptyTranslationDisplay;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveSettings(); // 如果文件不存在，保存默认设置
        }
    }

    public void saveSettings() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Writing JSON to file: " + SETTING_FILE.getAbsolutePath());
        try (FileWriter writer = new FileWriter(SETTING_FILE)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter and Setter methods for each field
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastOpenPath() {
        return lastOpenPath;
    }

    public void setLastOpenPath(String lastOpenPath) {
        this.lastOpenPath = lastOpenPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public boolean isJsonKeyDisplay() {
        return jsonKeyDisplay;
    }

    public void setJsonKeyDisplay(boolean jsonKeyDisplay) {
        this.jsonKeyDisplay = jsonKeyDisplay;
    }

    public boolean isEmptyTranslationDisplay() {
        return emptyTranslationDisplay;
    }

    public void setEmptyTranslationDisplay(boolean emptyTranslationDisplay) {
        this.emptyTranslationDisplay = emptyTranslationDisplay;
    }

}
