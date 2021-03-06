package ru.makkarpov.ucl.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.util.Key;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.gdb.GDBDriver;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteGDBDebugProcessKt;
import org.jetbrains.annotations.NotNull;
import ru.makkarpov.ucl.GlobalSettings;
import ru.makkarpov.ucl.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class OCDDebugProcess extends CidrDebugProcess {
    private OCDRunConfiguration ocdConfig;
    private GlobalSettings settings;

    private File executableFile;

    private OSProcessHandler ocdProcess;
    private File ocdScriptFile;

    private String gdbStartMarker = "# gdb_start_marker";
    private int gdbPort;
    private boolean ocdInitialized = false;

    public OCDDebugProcess(@NotNull OCDRunConfiguration ocdConfig,
                           @NotNull GDBDriverConfiguration driverCfg,
                           @NotNull XDebugSession paramXDebugSession,
                           @NotNull TextConsoleBuilder paramTextConsoleBuilder) throws ExecutionException {
        super(CidrRemoteGDBDebugProcessKt.createParams(driverCfg), paramXDebugSession, paramTextConsoleBuilder);
        this.ocdConfig = ocdConfig;
        this.settings = ocdConfig.application.getComponent(GlobalSettings.class);
        this.executableFile = ocdConfig.getExecutableFile();

        gdbPort = ocdConfig.isGDBPortValid() ? ocdConfig.gdbPort : Utils.getAvailablePort();
    }


    @Override
    protected void doStart(@NotNull DebuggerDriver debuggerDriver) throws ExecutionException {
        if (settings.ocdExecutablePath == null || settings.ocdExecutablePath.isEmpty()) {
            stop();
            throw new ExecutionException("No OpenOCD binary was specified in settings");
        }

        try {
            OCDScriptWriter.OCDScriptConfig cfg = new OCDScriptWriter.OCDScriptConfig();
            cfg.gdbPort = gdbPort;
            cfg.gdbStartMarker = gdbStartMarker;
            cfg.executableFile = executableFile;

            // We will run target later through GDB
            cfg.resetHalt = true;

            ocdScriptFile = OCDScriptWriter.writeScript(ocdConfig, cfg);
        } catch (IOException e) {
            stop();
            throw new ExecutionException("Failed to write OpenOCD script file", e);
        }

        try {
            ocdProcess = new ColoredProcessHandler(new PtyCommandLine()
                    .withExePath(settings.ocdExecutablePath)
                    .withParameters(ParametersListUtil.parse(ocdConfig.parameters))
                    .withParameters("-f", ocdScriptFile.getCanonicalPath())
            );

            getConsole().attachToProcess(ocdProcess);

            ocdProcess.addProcessListener(new ProcessAdapter() {
                @Override
                public void onTextAvailable(ProcessEvent event, Key outputType) {
                    if (event.getText().contains(gdbStartMarker)) {
                        synchronized (OCDDebugProcess.this) {
                            ocdInitialized = true;
                            OCDDebugProcess.this.notifyAll();
                        }
                        ocdProcess.removeProcessListener(this);
                    }
                }
            });

            ocdProcess.startNotify();
        } catch (Exception e) {
            stop();
            throw new ExecutionException("Failed to start OpenOCD", e);
        }
    }

    @Override
    protected void doLaunchTarget(@NotNull DebuggerDriver debuggerDriver) throws ExecutionException {
        GDBDriver gdb = (GDBDriver) debuggerDriver;

        synchronized (this) {
            while (!ocdInitialized) {
                try {
                    wait(20000, 0);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        if (!ocdInitialized) {
            stop();
            throw new ExecutionException("Interrupted while waiting for OpenOCD startup");
        }

        try {
            gdb.loadForRemote(executableFile, null, Collections.emptyList());
            gdb.executeConsoleCommand("target extended-remote tcp:localhost:" + gdbPort);

            if (ocdConfig.uploadFirmware && !ocdConfig.resetHalt) {
                gdb.executeConsoleCommand("continue");
            }
        } catch (Exception e) {
            stop();
            throw new ExecutionException("Failed to initialize GDB", e);
        }
    }

    @Override
    public void stop() {
        if (ocdScriptFile != null) {
            if (!ocdScriptFile.delete()) {
                ocdScriptFile.deleteOnExit();
                System.out.println("WARN: failed to delete script file");
            }

            ocdScriptFile = null;
        }

        if (ocdProcess != null) {
            System.out.println("Stopping OpenOCD");
            ocdProcess.destroyProcess();
            ocdProcess = null;
        }

        super.stop();
    }
}
