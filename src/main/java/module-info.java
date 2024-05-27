module com.ayush.vinylrecords.vinylqueryapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.sql.rowset;
    requires org.postgresql.jdbc;


    opens com.ayush.vinylrecords.vinylqueryapp to javafx.fxml;
    exports com.ayush.vinylrecords.vinylqueryapp;
}