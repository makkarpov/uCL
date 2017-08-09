package ru.makkarpov.ucl.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.components.JBTextField;

import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;

public class TclEditor extends ComponentWithBrowseButton<JBTextField> {
    private String script;

    public TclEditor(String script) {
        super(new JBTextField(), null);
        addActionListener(this::browseClicked);
        getChildComponent().setEditable(false);

        this.script = script;
        getChildComponent().setText(replaceScript(script));
    }

    private void browseClicked(ActionEvent e) {
        Document doc = new DocumentImpl(script);
        Editor editor;
        try {
            Constructor<EditorImpl> constructor =
                    EditorImpl.class.getDeclaredConstructor(Document.class, boolean.class, Project.class);
            constructor.setAccessible(true);
            editor = constructor.newInstance(doc, /* read-only: */ false, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle("Edit OpenOCD script");

        builder.setDimensionServiceKey("EditParametersPopupWindow");
        builder.setCenterPanel(editor.getComponent());
        builder.setPreferredFocusComponent(editor.getComponent());

        builder.addOkAction();
        builder.addCancelAction();
        builder.setOkOperation(() -> {
            setText(doc.getText());
            builder.getDialogWrapper().close(0);
        });
        builder.show();
    }

    private String replaceScript(String s) {
        return s.replaceAll("[\n\\s]+", " ");
    }

    public String getText() {
        return script;
    }

    public void setText(String script) {
        this.script = script;
        getChildComponent().setText(replaceScript(script));
    }
}
