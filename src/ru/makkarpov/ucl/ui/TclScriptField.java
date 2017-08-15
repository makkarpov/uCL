package ru.makkarpov.ucl.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;

public class TclScriptField extends ComponentWithBrowseButton<JBTextField> {
    private String script;

    public TclScriptField(String script) {
        super(new JBTextField(), null);
        addActionListener(this::browseClicked);
        getChildComponent().setEditable(false);

        this.script = script;
        getChildComponent().setText(replaceScript(script));
    }

    private void browseClicked(ActionEvent e) {
        Document doc = new DocumentImpl(script);
        Editor editor = null;
        JTextArea plainEditor = null;

        try {
            Constructor<EditorImpl> constructor =
                    EditorImpl.class.getDeclaredConstructor(Document.class, boolean.class, Project.class);
            constructor.setAccessible(true);
            editor = constructor.newInstance(doc, /* read-only: */ false, null);
        } catch (NoSuchMethodException ex) {
            try {
                Constructor<EditorImpl> constructor = EditorImpl.class.getDeclaredConstructor(Document.class,
                        boolean.class, Project.class, EditorKind.class);

                constructor.setAccessible(true);
                editor = constructor.newInstance(doc, false, null, EditorKind.MAIN_EDITOR);
            } catch (Exception exx) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (editor == null) {
            plainEditor = new JTextArea();

            UIUtil.addUndoRedoActions(plainEditor);
            plainEditor.setWrapStyleWord(true);
            plainEditor.setLineWrap(true);
            plainEditor.setText(script);
        }

        final JTextArea finalPlainEditor = plainEditor;
        final JComponent editorComponent = finalPlainEditor != null ? finalPlainEditor : editor.getComponent();

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle("Edit OpenOCD script");

        builder.setDimensionServiceKey(getClass().getCanonicalName());
        builder.setCenterPanel(editorComponent);
        builder.setPreferredFocusComponent(editorComponent);

        builder.addOkAction();
        builder.addCancelAction();
        builder.setOkOperation(() -> {
            setText(finalPlainEditor != null ? finalPlainEditor.getText() : doc.getText());
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
