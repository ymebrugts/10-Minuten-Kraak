module nl.hsl.heist.views {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires javafx.media;
    requires org.apache.commons.io;
    requires org.json;

    opens nl.hsl.heist.views to javafx.fxml;
    exports nl.hsl.heist.views;
    exports nl.hsl.heist.controllers to javafx.fxml;

    opens nl.hsl.heist.controllers to javafx.fxml;
}
