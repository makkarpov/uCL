package ru.makkarpov.ucl.runner;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.jetbrains.cidr.cpp.execution.CMakeRunConfigurationType;
import org.jetbrains.annotations.NotNull;
import ru.makkarpov.ucl.ui.OCDSettingsEditor;

import javax.swing.*;

public class OCDConfigurationType extends CMakeRunConfigurationType {
    public static final Icon ICON = IconLoader.findIcon("/ru/makkarpov/ucl/chip.png", OCDConfigurationType.class);

    public final Application application;

    protected OCDConfigurationType(Application application) {
        super("ru.makkarpov.ucl.openocd", "OpenOCD", "OpenOCD debugging", ICON);
        this.application = application;
    }

    @NotNull
    @Override
    protected CMakeAppRunConfiguration createRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory configurationFactory) {
        return new OCDRunConfiguration(application, project, configurationFactory, "");
    }

    @Override
    public SettingsEditor<? extends CMakeAppRunConfiguration> createEditor(@NotNull Project project) {
        return new OCDSettingsEditor(project, new CMakeBuildConfigurationHelper(project));
    }
}
