package org.example.livechatmodule.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;

@Push(value = PushMode.AUTOMATIC)
public class AppShellConfig implements AppShellConfigurator {
}
