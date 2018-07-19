package org.kordamp.javafx.sass;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.desktoppanefx.scene.layout.DesktopPane;
import org.kordamp.desktoppanefx.scene.layout.InternalWindow;
import org.kordamp.desktoppanefx.scene.layout.TaskBar;
import org.kordamp.desktoppanefx.scene.layout.TaskBarIcon;
import org.kordamp.ikonli.javafx.FontIcon;

import static javafx.beans.binding.Bindings.createBooleanBinding;

public class Demo extends Application {
    private static int count = 0;

    public static void main(String[] args) {
        launch(Demo.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DesktopPane desktopPane = new MyDesktopPane();
        desktopPane.getStylesheets().add("demo.css");

        ObservableList<TaskBarIcon> icons = desktopPane.getTaskBar().getTaskBarIcons();
        desktopPane.getTaskBar().visibleProperty().bind(createBooleanBinding(() -> icons.size() > 0, icons));

        Button newWindow = new Button("New Window");
        newWindow.setOnAction(e -> {
            InternalWindow window = new MyInternalWindow(
                "window-" + count,
                new FontIcon("mdi-application:20:WHITE"),
                "Title " + count,
                new StackPane(new Label("Content")),
                "window-" + count++);
            window.setMinWidth(300);
            window.setMaxHeight(300);
            desktopPane.addInternalWindow(window);
        });
        Button toggle = new Button("Top/Bottom");
        toggle.setOnAction(e -> {
            switch (desktopPane.getTaskBar().getPosition()) {
                case TOP:
                    desktopPane.getTaskBar().setPosition(TaskBar.Position.BOTTOM);
                    break;
                case BOTTOM:
                    desktopPane.getTaskBar().setPosition(TaskBar.Position.TOP);
            }
        });

        Button minimize = new Button("Minimize All");
        minimize.setOnAction(e -> {
            desktopPane.getInternalWindows()
                .forEach(InternalWindow::minimizeWindow);
        });

        BorderPane mainPane = new BorderPane();
        mainPane.setPrefSize(800, 600);
        ToolBar bb = new ToolBar();
        bb.getItems().addAll(newWindow, toggle, minimize);
        mainPane.setTop(bb);
        mainPane.setCenter(desktopPane);

        stage.setScene(new Scene(mainPane));
        stage.setTitle("DesktopPaneFX - https://github.com/aalmiray/desktoppanefx");
        stage.show();
    }

    public static class MyInternalWindow extends InternalWindow {
        private static final String STEREOTYPE = "STEREOTYPE";

        public MyInternalWindow(String mdiWindowID, Node icon, String title, Node content, String stereotype) {
            super(mdiWindowID, icon, title, content);
            getProperties().put(STEREOTYPE, stereotype);
            getStyleClass().add(stereotype);
        }
    }

    public static class MyDesktopPane extends DesktopPane {
        @Override
        protected TaskBarIcon createTaskBarIcon(InternalWindow internalWindow) {
            TaskBarIcon taskBarIcon = super.createTaskBarIcon(internalWindow);
            String stereotype = (String) internalWindow.getProperties().get(MyInternalWindow.STEREOTYPE);
            taskBarIcon.getStyleClass().add(stereotype);
            return taskBarIcon;
        }
    }
}
