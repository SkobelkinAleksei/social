package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ConfirmDialog extends Dialog {
    public ConfirmDialog(String title, String message, Runnable onConfirm) {
        setHeaderTitle(title);
        add(new Paragraph(message));
        Button yes = new Button("Да", e -> { onConfirm.run(); close(); });
        Button no = new Button("Нет", e -> close());
        add(new HorizontalLayout(yes, no));
    }
}
