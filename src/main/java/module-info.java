module org.example.db_localaization {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;
    requires java.desktop;
    requires java.sql.rowset;



    opens org.example.db_localaization to javafx.fxml;
    exports org.example.db_localaization;
}