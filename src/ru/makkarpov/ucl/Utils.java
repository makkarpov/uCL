package ru.makkarpov.ucl;

import org.jdom.Namespace;

public class Utils {
    public static final Namespace NAMESPACE = Namespace.getNamespace("ucl", "https://makkarpov.ru/xmlns/ucl");

    public static final String tclEscape(String s) {
        return "{" + s + "}";
    }
}
