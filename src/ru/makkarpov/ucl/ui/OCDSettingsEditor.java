package ru.makkarpov.ucl.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;
import ru.makkarpov.ucl.runner.OCDRunConfiguration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class OCDSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private JBCheckBox uploadFirmware, resetHalt;
    private JBTextField gdbPort;
    private RawCommandLineEditor ocdParameters;
    private TclEditor ocdScript;

    public OCDSettingsEditor(Project project, @NotNull CMakeBuildConfigurationHelper cMakeBuildConfigurationHelper) {
        super(project, cMakeBuildConfigurationHelper);
    }

    @Override
    protected void applyEditorTo(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) throws ConfigurationException {
        super.applyEditorTo(cMakeAppRunConfiguration);
        OCDRunConfiguration ocdConfig = (OCDRunConfiguration) cMakeAppRunConfiguration;

        ocdConfig.uploadFirmware = uploadFirmware.isSelected();
        ocdConfig.parameters = ocdParameters.getText();
        ocdConfig.script = ocdScript.getText();
        ocdConfig.gdbPort = parsePort();
        ocdConfig.resetHalt = resetHalt.isSelected();
    }

    @Override
    protected void resetEditorFrom(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) {
        super.resetEditorFrom(cMakeAppRunConfiguration);
        OCDRunConfiguration ocdConfig = (OCDRunConfiguration) cMakeAppRunConfiguration;

        uploadFirmware.setSelected(ocdConfig.uploadFirmware);
        ocdParameters.setText(ocdConfig.parameters);
        ocdScript.setText(ocdConfig.script);
        gdbPort.setText(ocdConfig.isGDBPortValid() ? String.valueOf(ocdConfig.gdbPort) : "");
        resetHalt.setSelected(ocdConfig.resetHalt);
    }

    @Override
    protected void createEditorInner(JPanel panel, GridBag gridBag) {
        super.createEditorInner(panel, gridBag);

        panel.add(new JBLabel("OpenOCD parameters"), gridBag.nextLine().next());
        panel.add(ocdParameters = new RawCommandLineEditor(), gridBag.next().coverLine());
        panel.add(new JBLabel("OpenOCD script"), gridBag.nextLine().next());
        panel.add(ocdScript = new TclEditor(""), gridBag.next().coverLine());
        panel.add(new JBLabel("GDB port"), gridBag.nextLine().next());
        panel.add(gdbPort = new JBTextField(""), gridBag.next().coverLine());

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

        checkboxPanel.add(uploadFirmware = new JBCheckBox("Upload firmware to the target"));
        checkboxPanel.add(Box.createHorizontalStrut(20));
        checkboxPanel.add(resetHalt = new JBCheckBox("Halt target after reset"));

        panel.add(checkboxPanel, gridBag.nextLine().coverLine());

        uploadFirmware.addChangeListener(e -> resetHalt.setEnabled(uploadFirmware.isSelected()));
        gdbPort.getEmptyText().setText("pick automatically");
        gdbPort.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (parsePort() == 0) {
                    gdbPort.setText("");
                }
            }
        });
    }

    private int parsePort() {
        try {
            int port = Integer.parseInt(gdbPort.getText());
            if (port > 0 && port <= 65535)
                return port;
        } catch (NumberFormatException ignored) {
        }

        return 0;
    }
}
