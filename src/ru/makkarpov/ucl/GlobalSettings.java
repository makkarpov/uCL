package ru.makkarpov.ucl;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ru.makkarpov.ucl.GlobalSettings", storages = @Storage("ucl.xml"))
public class GlobalSettings implements ApplicationComponent, PersistentStateComponent<GlobalSettings> {
    public String ocdExecutablePath = "";

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ru.makkarpov.ucl.GlobalSettings";
    }

    @Nullable
    @Override
    public GlobalSettings getState() {
        return this;
    }

    @Override
    public void loadState(GlobalSettings globalSettings) {
        XmlSerializerUtil.copyBean(globalSettings, this);
    }
}
