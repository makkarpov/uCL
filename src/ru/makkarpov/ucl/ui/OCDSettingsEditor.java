package ru.makkarpov.ucl.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.GridBag;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfigurationSettingsEditor;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;
import ru.makkarpov.ucl.runner.OCDRunConfiguration;
import ru.makkarpov.ucl.ui.TclEditor;

import javax.swing.*;

public class OCDSettingsEditor extends CMakeAppRunConfigurationSettingsEditor {
    private JBCheckBox uploadFirmware;
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
    }

    @Override
    protected void resetEditorFrom(@NotNull CMakeAppRunConfiguration cMakeAppRunConfiguration) {
        super.resetEditorFrom(cMakeAppRunConfiguration);
        OCDRunConfiguration ocdConfig = (OCDRunConfiguration) cMakeAppRunConfiguration;

        uploadFirmware.setSelected(ocdConfig.uploadFirmware);
        ocdParameters.setText(ocdConfig.parameters);
        ocdScript.setText(ocdConfig.script);
    }

    @Override
    protected void createEditorInner(JPanel panel, GridBag gridBag) {
        super.createEditorInner(panel, gridBag);

        panel.add(new JBLabel("OpenOCD parameters"), gridBag.nextLine().next());
        panel.add(ocdParameters = new RawCommandLineEditor(), gridBag.next().coverLine());
        panel.add(new JBLabel("OpenOCD script"), gridBag.nextLine().next());
        panel.add(ocdScript = new TclEditor(""), gridBag.next().coverLine());
        panel.add(uploadFirmware = new JBCheckBox("Upload firmware to board"), gridBag.nextLine().coverLine());
    }
}
