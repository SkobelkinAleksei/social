package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.SettingsClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.profile.ProfileSideMenu;

public class MainLayout extends AppLayout implements RouterLayout, AfterNavigationObserver {

    private final UserClient userClient;
    private final SettingsClient settingsClient;
    private final PostClient postClient;

    private ProfileSideMenu sideMenu;

    public MainLayout(UserClient userClient, SettingsClient settingsClient, PostClient postClient) {
        this.userClient = userClient;
        this.settingsClient = settingsClient;
        this.postClient = postClient;

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
            // Fallback
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setDrawerOpened(false);
    }
}
