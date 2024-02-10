module com.example.multiclientui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.multiclientui to javafx.fxml;
    exports com.example.multiclientui;
}