package ru.makkarpov.ucl.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.CMakeLauncher;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import org.jetbrains.annotations.NotNull;

public class OCDLauncher extends CMakeLauncher {
    private final OCDRunConfiguration configuration;

    public OCDLauncher(@NotNull OCDRunConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    @Override
    public ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {
        return super.createProcess(commandLineState);
    }

    @NotNull
    @Override
    public CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        GDBDriverConfiguration conf = new GDBDriverConfiguration();
        return new OCDDebugProcess(configuration, conf, xDebugSession, commandLineState.getConsoleBuilder());
    }
}
