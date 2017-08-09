package ru.makkarpov.ucl.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
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

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static ru.makkarpov.ucl.Utils.NAMESPACE;

public class OCDRunConfiguration extends CMakeAppRunConfiguration {
    public static final String UPLOAD_FIRMWARE_ATTR = "upload-firmware";
    public static final String GDB_PORT_ATTR = "gdb-port";
    public static final String RESET_HALT_ATTR = "reset-halt";
    public static final String SCRIPT_NODE = "script";
    public static final String PARAMETERS_NODE = "parameters";

    public final Application application;

    public boolean uploadFirmware = true;
    public boolean resetHalt = false;

    public int gdbPort = 0;
    public String script = "";
    public String parameters = "";

    protected OCDRunConfiguration(Application application, Project project, ConfigurationFactory cfgFactory, String name) {
        super(project, cfgFactory, name);

        this.application = application;
    }

    public File getExecutableFile() throws ExecutionException {
        BuildAndRunConfigurations brc = Objects.requireNonNull(getBuildAndRunConfigurations(), "brc == null");
        File runFile = Objects.requireNonNull(brc.getRunFile(), "runFile == null");

        if (!runFile.isFile()) {
            throw new ExecutionException("runFile does not exists: " + runFile);
        }

        return runFile;
    }

    public boolean isGDBPortValid() {
        return gdbPort > 0 && gdbPort <= 65535;
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        element.setAttribute(UPLOAD_FIRMWARE_ATTR, String.valueOf(uploadFirmware), NAMESPACE);
        element.setAttribute(GDB_PORT_ATTR, String.valueOf(gdbPort), NAMESPACE);
        element.setAttribute(RESET_HALT_ATTR, String.valueOf(resetHalt), NAMESPACE);

        Element scriptElement = new Element(SCRIPT_NODE, NAMESPACE);
        scriptElement.addContent(new CDATA(script));
        element.addContent(scriptElement);

        Element parametersElement = new Element(PARAMETERS_NODE, NAMESPACE);
        parametersElement.addContent(new CDATA(parameters));
        element.addContent(parametersElement);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        uploadFirmware = Boolean.parseBoolean(element.getAttributeValue(UPLOAD_FIRMWARE_ATTR, NAMESPACE, "true"));
        resetHalt = Boolean.parseBoolean(element.getAttributeValue(RESET_HALT_ATTR, NAMESPACE, "true"));
        gdbPort = Integer.parseInt(element.getAttributeValue(GDB_PORT_ATTR, NAMESPACE, "0"));

        Element scriptElement = element.getChild(SCRIPT_NODE, NAMESPACE);
        if (scriptElement != null)
            script = scriptElement.getText();

        Element parametersElement = element.getChild(PARAMETERS_NODE, NAMESPACE);
        if (parametersElement != null)
            parameters = parametersElement.getText();
    }

    @Nullable
    @Override
    public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new CidrCommandLineState(executionEnvironment, new OCDLauncher(this));
    }
}
