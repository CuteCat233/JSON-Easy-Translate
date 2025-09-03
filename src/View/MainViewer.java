package View;

import java.io.File;
import java.util.LinkedHashMap;
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
import Setting.SettingControl;

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

    private SettingControl settingControl = new SettingControl();

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

        settingControl.loadSettings();

        if (!settingControl.isJsonKeyDisplay()) {
            keyColumn.setVisible(false);
            showJsonKey.setSelected(false);
        } else {
            keyColumn.setVisible(true);
            showJsonKey.setSelected(true);
        }

        if (!settingControl.isEmptyTranslationDisplay()) {
            showEmptyTr.setSelected(false);
        } else {
            showEmptyTr.setSelected(true);
            displayEmptyTr(new ActionEvent());
        }

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

        for (TranslationEntry entry : fullTableData) {
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
        if (settingControl.getOutputPath() != null && !settingControl.getOutputPath().isEmpty()) {
            File initialDir = new File(settingControl.getOutputPath());
            if (initialDir.exists() && initialDir.isDirectory()) {
                exfc.setInitialDirectory(initialDir);
            }
        }

        File efFile = exfc.showSaveDialog(exportFileButton.getScene().getWindow());
        if (efFile != null) {
            settingControl.setOutputPath(efFile.getParentFile().getAbsolutePath());
            settingControl.saveSettings();
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
        if (settingControl.getLastOpenPath() != null && !settingControl.getLastOpenPath().isEmpty()) {
            File initialDir = new File(settingControl.getLastOpenPath());
            if (initialDir.exists() && initialDir.isDirectory()) {
                orfc.setInitialDirectory(initialDir);
            }
        }
        
        File orFile = orfc.showOpenDialog(selectFileButton.getScene().getWindow());

        // System.out.println(orFile != null ? orFile.getParentFile().getAbsolutePath() : "");
        if (orFile != null) {
            System.out.println(orFile.getAbsolutePath());
            settingControl.setLastOpenPath(orFile.getParentFile().getAbsolutePath());
            settingControl.saveSettings();
            orfs.setFile(orFile);
        }
        refreshTable();
    }

    @FXML
    void selectTrFile(ActionEvent event) {
        if (orfs.getFile() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("没有待翻译数据");
            alert.setContentText("请选择一个有效的待翻译文件。");
            alert.showAndWait();
            return;
        }
        FileChooser trfc = new FileChooser();
        trfc.setTitle("选择文件");
        File initialDir = new File(settingControl.getLastOpenPath());
        if (initialDir.exists() && initialDir.isDirectory()) {
            trfc.setInitialDirectory(initialDir);
        }
        trfc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        File trFile = trfc.showOpenDialog(selectTrFileButton.getScene().getWindow());

        if (trFile != null) {
            trfs.setFile(trFile);
            settingControl.setLastOpenPath(trFile.getParentFile().getAbsolutePath());
            settingControl.saveSettings();
        }
        refreshTable();
        if (showEmptyTr.isSelected()) {
            displayEmptyTr(new ActionEvent());
        }
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
        for (String key : orhm.keySet()) {
            String orVal = orhm.getOrDefault(key, null);
            String trVal = trhm.getOrDefault(key, null); // 或用 "" 代替 null
            fullTableData.add(new TranslationEntry(key, orVal, trVal));
        }
        table.setItems(fullTableData);
    }
        
}
