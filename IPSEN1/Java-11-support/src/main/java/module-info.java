module nl.hsl.heist {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires javafx.media;
    requires org.apache.commons.io;
    requires org.json;

    exports nl.hsl.heist.controllers;
    exports nl.hsl.heist.models;
    exports nl.hsl.heist.observers;
    exports nl.hsl.heist.shared;
    exports nl.hsl.heist.server;
    exports nl.hsl.heist.views;

    opens nl.hsl.heist.views to javafx.fxml;
//    exports nl.hsl.heist.views to javafx.graphics;
//    exports nl.hsl.heist.controllers to javafx.fxml;

    opens nl.hsl.heist.controllers to javafx.fxml;
}
