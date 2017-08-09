package ru.makkarpov.ucl;

import com.intellij.execution.ExecutionException;
import org.jdom.Namespace;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {
    public static final Namespace NAMESPACE = Namespace.getNamespace("ucl", "https://makkarpov.ru/xmlns/ucl");
    public static final String GLOBAL_STORAGE = "ucl.xml";

    public static String tclEscape(String s) {
        return "{" + s.replaceAll("([{}])", "\\\\$1") + "}";
    }

    public static int getAvailablePort() throws ExecutionException {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new ExecutionException("Failed to get available port", e);
        } finally {
            if (socket != null)
                try { socket.close(); }
                catch (IOException ignored) { }
        }
    }
}
