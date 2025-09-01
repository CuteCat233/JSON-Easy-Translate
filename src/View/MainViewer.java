package View;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import Model.TranslationEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import Service.FileService;

public class MainViewer {

    @FXML
    private Button exportFileButton;

    @FXML
    private Button selectFileButton;

    @FXML
    private Button selectTrFileButton;

    @FXML
    private CheckBox showJsonKey;

    @FXML
    private TableView<TranslationEntry> table;

    @FXML
    private TableColumn<TranslationEntry, String> keyColumn;

    @FXML
    private TableColumn<TranslationEntry, String> originalColumn;

    @FXML
    private TableColumn<TranslationEntry, String> tranlateColumn;

    @FXML
    private CheckBox showEmptyTr;

    private FileService orfs = new FileService();

    private FileService trfs = new FileService();

    private ObservableList<TranslationEntry> fullTableData = FXCollections.observableArrayList();

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert exportFileButton != null : "fx:id=\"exportFileButton\" was not injected: check your FXML file 'Main.fxml'.";
        assert keyColumn != null : "fx:id=\"keyColumn\" was not injected: check your FXML file 'Main.fxml'.";
        assert originalColumn != null : "fx:id=\"originalColumn\" was not injected: check your FXML file 'Main.fxml'.";
        assert selectFileButton != null : "fx:id=\"selectFileButton\" was not injected: check your FXML file 'Main.fxml'.";
        assert selectTrFileButton != null : "fx:id=\"selectTrFileButton\" was not injected: check your FXML file 'Main.fxml'.";
        assert showJsonKey != null : "fx:id=\"showJsonKey\" was not injected: check your FXML file 'Main.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'Main.fxml'.";
        assert tranlateColumn != null : "fx:id=\"tranlateColumn\" was not injected: check your FXML file 'Main.fxml'.";
        assert showEmptyTr != null : "fx:id=\"showEmptyTr\" was not injected: check your FXML file 'Main.fxml'.";

        keyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        originalColumn.setCellValueFactory(cellData -> cellData.getValue().originalProperty());
        tranlateColumn.setCellValueFactory(cellData -> cellData.getValue().translatedProperty());

        // 设置可编辑
        table.setEditable(true);
        originalColumn.setCellFactory(col -> createTextAreaCell());
        tranlateColumn.setCellFactory(col -> createTextAreaCell());

    }

    private TableCell<TranslationEntry, String> createTextAreaCell() {
        return new TableCell<>() {
            private final TextArea textArea = new TextArea();

            {
                textArea.setWrapText(true);
                textArea.setPrefRowCount(2);
                textArea.setStyle("-fx-font-size: 12px;");
                textArea.setPrefHeight(Region.USE_COMPUTED_SIZE);

                // 自动更新数据模型
                textArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused && getTableRow() != null && getIndex() < getTableView().getItems().size()) {
                        TranslationEntry item = getTableView().getItems().get(getIndex());
                        if (getTableColumn() == originalColumn) {
                            item.originalProperty().set(textArea.getText());
                        } else if (getTableColumn() == tranlateColumn) {
                            item.translatedProperty().set(textArea.getText());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    textArea.setText(item != null ? item : "");
                    textArea.prefWidthProperty().bind(getTableColumn().widthProperty());
                    setGraphic(textArea);
                }
            }
        };
    }

    @FXML
    void displayEmptyTr(ActionEvent event) {
        if (showEmptyTr.isSelected()) {
            // 筛选翻译为空的项
            ObservableList<TranslationEntry> filtered = fullTableData.filtered(entry ->
                entry.translatedProperty().get() == null ||
                entry.translatedProperty().get().trim().isEmpty()
            );
            table.setItems(filtered);
        } else {
            // 恢复完整数据
            table.setItems(fullTableData);
        }
    }

    @FXML
    void displayKey(ActionEvent event) {
        if (showJsonKey.isSelected()) {
            keyColumn.setVisible(true);
        } else {
            keyColumn.setVisible(false);
        }
    }

    @FXML
    void exportFile(ActionEvent event) {
        LinkedHashMap<String, String> exportMap = new LinkedHashMap<>();

        for (TranslationEntry entry : table.getItems()) {
            String key = entry.keyProperty().get();
            String translated = entry.translatedProperty().get();

            // 如果你希望跳过空值，可以加判断
            // if (translated != null && !translated.trim().isEmpty()) {
            exportMap.put(key, translated != null ? translated : "");
            // }
        }
        
        FileChooser exfc = new FileChooser();
        exfc.setTitle("另存为");
        exfc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json")
        );
        File efFile = exfc.showSaveDialog(exportFileButton.getScene().getWindow());
        if (efFile != null) {
            trfs.setFile(efFile);
            trfs.writeJson(exportMap);
        }
    }

    @FXML
    void selectFile(ActionEvent event) {

        FileChooser orfc = new FileChooser();
        orfc.setTitle("选择文件");
        orfc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        ); 
        File orFile = orfc.showOpenDialog(selectFileButton.getScene().getWindow());
        if (orFile != null) {
            System.out.println(orFile.getAbsolutePath());
            orfs.setFile(orFile);
        }
        refreshTable();
    }

    @FXML
    void selectTrFile(ActionEvent event) {
        FileChooser trfc = new FileChooser();
        trfc.setTitle("选择文件");
        
        trfc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        File trFile = trfc.showOpenDialog(selectTrFileButton.getScene().getWindow());
        if (trFile != null) {
            trfs.setFile(trFile);
        }
        refreshTable();
    }

    @SuppressWarnings("unchecked")
    private void refreshTable() {
        table.getItems().clear();
        System.out.println("Refreshing table...");
        LinkedHashMap<String, String> orhm = new LinkedHashMap<>();
        if (orfs.getFile() != null) {
            orhm = orfs.getJson();
        }
        LinkedHashMap<String, String> trhm = new LinkedHashMap<>();
        if (trfs.getFile() != null) {
            trhm = trfs.getJson();
        }
        if (orhm.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Original Data");
            alert.setContentText("Please select a valid original file.");
            alert.showAndWait();
            return;
        }
        for (String key : orhm.keySet()) {
            String orVal = orhm.getOrDefault(key, null);
            String trVal = trhm.getOrDefault(key, null); // 或用 "" 代替 null
            fullTableData.add(new TranslationEntry(key, orVal, trVal));
        }
        table.setItems(fullTableData);
    }
        
}
