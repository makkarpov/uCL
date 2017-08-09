package ru.makkarpov.ucl.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.CMakeLauncher;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import org.jetbrains.annotations.NotNull;
import ru.makkarpov.ucl.GlobalSettings;
import ru.makkarpov.ucl.Utils;

import java.io.File;
import java.io.IOException;

public class OCDLauncher extends CMakeLauncher {
    private final OCDRunConfiguration configuration;

    public OCDLauncher(@NotNull OCDRunConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    @Override
    public ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {
        GlobalSettings settings = configuration.application.getComponent(GlobalSettings.class);

        File scriptFile;
        try {
            OCDScriptWriter.OCDScriptConfig cfg = new OCDScriptWriter.OCDScriptConfig();

            // Unfortunately there is no "disabled" for GDB port
            cfg.gdbPort = configuration.isGDBPortValid() ? configuration.gdbPort : Utils.getAvailablePort();

            cfg.gdbStartMarker = null;
            cfg.executableFile = configuration.getExecutableFile();
            cfg.resetHalt = false;
            cfg.shutdown = true;

            scriptFile = OCDScriptWriter.writeScript(configuration, cfg);
        } catch (IOException e) {
            throw new ExecutionException("Failed to write GDB script");
        }

        try {
            return new ColoredProcessHandler(new PtyCommandLine()
                    .withExePath(settings.ocdExecutablePath)
                    .withParameters(ParametersListUtil.parse(configuration.parameters))
                    .withParameters("-f", scriptFile.getCanonicalPath())
            );
        } catch (IOException e) {
            throw new ExecutionException("Failed to start OpenOCD", e);
        }
    }

    @NotNull
    @Override
    public CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        GDBDriverConfiguration conf = new GDBDriverConfiguration();
        return new OCDDebugProcess(configuration, conf, xDebugSession, commandLineState.getConsoleBuilder());
    }
}
