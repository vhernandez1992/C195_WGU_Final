module hernandez.c195_wgu_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.base;


    opens hernandez.c195_wgu_final to javafx.fxml;
    exports hernandez.c195_wgu_final;
    exports controller;
    exports model;
    opens controller to javafx.fxml, javafx.graphics, javafx.base;

}