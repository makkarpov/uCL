package ru.makkarpov.ucl.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import com.jetbrains.cidr.execution.CidrCommandLineState;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.makkarpov.ucl.Utils;

public class OCDRunConfiguration extends CMakeAppRunConfiguration
implements RunConfigurationWithSuppressedDefaultRunAction {
    public static final String UPLOAD_FIRMWARE_ATTR = "upload-firmware";
    public static final String SCRIPT_NODE = "script";
    public static final String PARAMETERS_NODE = "parameters";

    public final Application application;

    public boolean uploadFirmware = true;
    public String script = "";
    public String parameters = "";

    protected OCDRunConfiguration(Application application, Project project, ConfigurationFactory cfgFactory, String name) {
        super(project, cfgFactory, name);

        this.application = application;
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute(UPLOAD_FIRMWARE_ATTR, String.valueOf(uploadFirmware), Utils.NAMESPACE);

        Element scriptElement = new Element(SCRIPT_NODE, Utils.NAMESPACE);
        scriptElement.addContent(new CDATA(script));
        element.addContent(scriptElement);

        Element parametersElement = new Element(PARAMETERS_NODE, Utils.NAMESPACE);
        parametersElement.addContent(new CDATA(parameters));
        element.addContent(parametersElement);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        uploadFirmware =
                Boolean.parseBoolean(element.getAttributeValue(UPLOAD_FIRMWARE_ATTR, Utils.NAMESPACE, "true"));

        Element scriptElement = element.getChild(SCRIPT_NODE, Utils.NAMESPACE);
        if (scriptElement != null)
            script = scriptElement.getText();

        Element parametersElement = element.getChild(PARAMETERS_NODE, Utils.NAMESPACE);
        if (parametersElement != null)
            parameters = parametersElement.getText();
    }

    @Nullable
    @Override
    public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new CidrCommandLineState(executionEnvironment, new OCDLauncher(this));
    }
}
