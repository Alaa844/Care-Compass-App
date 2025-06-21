module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires retrofit2.converter.scalars;
    requires retrofit2.converter.gson;
    requires gson;
    requires java.sql;
    requires java.desktop;
    requires okhttp3;


    // Export & open for JavaFX
    exports com.example.demo;
    exports com.example.demo.controller;
    exports com.example.demo.model;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controller to javafx.fxml;
    opens com.example.demo.model to gson;
}
