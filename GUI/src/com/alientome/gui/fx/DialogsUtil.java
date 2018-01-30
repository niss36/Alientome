package com.alientome.gui.fx;

import com.alientome.core.Context;
import com.alientome.core.internationalization.I18N;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class DialogsUtil {

    public static final int YES = 0, NO = 1, CANCEL = 2;

    public static int showYesNoCancelDialog(Alert.AlertType type,
                                            String title, String header, String content,
                                            String yesText, String noText, String cancelText) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("GUI/style.css").toExternalForm());

        ButtonType yes = new ButtonType(yesText);
        ButtonType no = new ButtonType(noText);
        ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yes, no, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        assert result.isPresent();
        // According to spec, if a CANCEL_CLOSE button is set, then it is returned if the dialog is closed.

        if (result.get() == yes)
            return YES;
        if (result.get() == no)
            return NO;
        return CANCEL;
    }

    public static boolean showConfirmDialog(Context context, String titleUnlocalized, String headerUnlocalized, String contentUnlocalized) {

        I18N i18N = context.getI18N();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (titleUnlocalized != null)
            alert.setTitle(i18N.get(titleUnlocalized));
        else
            alert.setTitle(null);
        if (headerUnlocalized != null)
            alert.setHeaderText(i18N.get(headerUnlocalized));
        else
            alert.setHeaderText(null);
        if (contentUnlocalized != null)
            alert.setContentText(i18N.get(contentUnlocalized));
        else
            alert.setContentText(null);

        alert.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("GUI/style.css").toExternalForm());

        ButtonType yes = new ButtonType(i18N.get("gui.yes"), ButtonBar.ButtonData.YES);
        ButtonType cancel = new ButtonType(i18N.get("gui.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yes, cancel);

        Optional<ButtonType> selected = alert.showAndWait();

        return selected.isPresent() && selected.get() == yes;
    }

    public static void showErrorDialog(Context context, Throwable t) {

        I18N i18N = context.getI18N();

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(null);
        alert.setHeaderText(null);

        String contentText = i18N.get("game.error.content");
        if (t.getMessage() != null)
            contentText += t.getMessage();

        alert.setContentText(contentText);

        Label details = new Label(i18N.get("game.error.details"));

        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        t.printStackTrace(print);
        String text = writer.toString();

        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);

        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(area, Priority.ALWAYS);
        GridPane.setHgrow(area, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(details, 0, 0);
        content.add(area, 0, 1);

        alert.getDialogPane().setExpandableContent(content);

        alert.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("GUI/style.css").toExternalForm());

        alert.showAndWait();
    }
}
