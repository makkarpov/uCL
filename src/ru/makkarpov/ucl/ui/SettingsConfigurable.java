package ru.makkarpov.ucl.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import ru.makkarpov.ucl.GlobalSettings;

import javax.swing.*;
import java.awt.*;

public class SettingsConfigurable implements Configurable {
    public static final FileChooserDescriptor OCD_BINARY_DESCRIPTOR = new FileChooserDescriptor(
            true, false, false, false, false,
            false
    );

    private TextFieldWithBrowseButton ocdBinary;
    private GlobalSettings settings;

    public SettingsConfigurable(GlobalSettings settings) {
        this.settings = settings;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "μCL";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBag gridBag = new GridBag()
                .setDefaultFill(GridBagConstraints.BOTH)
                .setDefaultAnchor(GridBagConstraints.CENTER)
                .setDefaultWeightX(1, 1.0D)
                .setDefaultInsets(0, JBUI.insets(0, 0, 4, 10))
                .setDefaultInsets(1, JBUI.insetsBottom(4));

        mainPanel.add(new JBLabel("Path to OpenOCD executable"), gridBag.nextLine().next());
        mainPanel.add(ocdBinary = new TextFieldWithBrowseButton(), gridBag.next().coverLine());

        ocdBinary.setText(settings.ocdExecutablePath);
        ocdBinary.addBrowseFolderListener("Choose OpenOCD Executable", null, null, OCD_BINARY_DESCRIPTOR);

        mainPanel.add(new JPanel(), gridBag.nextLine().next().coverLine().weighty(1));

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !settings.ocdExecutablePath.equals(ocdBinary.getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        settings.ocdExecutablePath = ocdBinary.getText();
    }

    @Override
    public void reset() {
        ocdBinary.setText(settings.ocdExecutablePath);
    }
}
