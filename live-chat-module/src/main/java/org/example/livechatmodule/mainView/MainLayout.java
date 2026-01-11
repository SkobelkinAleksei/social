package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import lombok.extern.slf4j.Slf4j;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.SettingsClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.profile.ProfileSideMenu;

@Slf4j
public class MainLayout extends AppLayout implements RouterLayout, AfterNavigationObserver {

    private final UserClient userClient;
    private final SettingsClient settingsClient;
    private final PostClient postClient;
    private final NotificationListenerComponent notificationListenerComponent;

    private ProfileSideMenu sideMenu;

    public MainLayout(
            UserClient userClient,
            SettingsClient settingsClient,
            PostClient postClient,
            NotificationListenerComponent notificationListenerComponent
    ) {
        this.userClient = userClient;
        this.settingsClient = settingsClient;
        this.postClient = postClient;
        this.notificationListenerComponent = notificationListenerComponent;

        DrawerToggle toggle = new DrawerToggle();
        H2 title = new H2("SOCIAL");
        title.addClassName("app-title");

        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(title);

        addToNavbar(header);
        initSideMenu();
    }

    private void initSideMenu() {
        try {
            Long userId = userClient.getMyProfile().getId();
            sideMenu = new ProfileSideMenu(userId, userClient, settingsClient, true, postClient);
            sideMenu.addClassName("side-menu");
            addToDrawer(sideMenu);
        } catch (Exception e) {
            log.warn("[MAINLAYOUT] Ошибка initSideMenu: {}", e.getMessage());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setDrawerOpened(false);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        notificationListenerComponent.init();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        notificationListenerComponent.destroy();
    }
}
