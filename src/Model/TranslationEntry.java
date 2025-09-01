package Model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class TranslationEntry {
    private final StringProperty key;
    private final StringProperty original;
    private final StringProperty translated;

    public TranslationEntry(String key, String original, String translated) {
        this.key = new SimpleStringProperty(key);
        this.original = new SimpleStringProperty(original);
        this.translated = new SimpleStringProperty(translated);
    }

    public StringProperty keyProperty() { return key; }
    public StringProperty originalProperty() { return original; }
    public StringProperty translatedProperty() { return translated; }
}

