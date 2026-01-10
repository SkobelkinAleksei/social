package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.NotificationBroadcaster;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.SettingsClient;
import org.example.livechatmodule.client.UserClient;
import org.example.livechatmodule.mainView.profile.ProfileSideMenu;

@Slf4j
@RequiredArgsConstructor
public class MainLayout extends AppLayout implements RouterLayout, AfterNavigationObserver {

    private final UserClient userClient;
    private final SettingsClient settingsClient;
    private final PostClient postClient;
    private final NotificationBroadcaster broadcaster;

    private NotificationListenerComponent notificationListener;
    private ProfileSideMenu sideMenu;

    {
        log.debug("Инициализация header MainLayout");
        DrawerToggle toggle = new DrawerToggle();
        H2 title = new H2("SOCIAL");
        title.addClassName("app-title");
        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(title);
        addToNavbar(header);
    }

    private void initSideMenu() {
        try {
            log.debug("Инициализация бокового меню");
            var userId = userClient.getMyProfile().getId();
            log.info("Создание бокового меню для пользователя ID: {}", userId);
            sideMenu = new ProfileSideMenu(userId, userClient, settingsClient, true, postClient);
            sideMenu.addClassName("side-menu");
            addToDrawer(sideMenu);
            log.debug("Боковое меню добавлено в drawer");
        } catch (Exception e) {
            log.error("Ошибка загрузки бокового меню", e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        log.trace("Закрытие drawer после навигации: {}", event.getLocation());
        setDrawerOpened(false);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        log.debug("MainLayout attached");

        getUI().ifPresent(ui -> ui.access(() -> {
            try {
                log.info("Инициализация MainLayout в UI потоке");
                initSideMenu();
                notificationListener = new NotificationListenerComponent(broadcaster);
                notificationListener.init();
                log.info("MainLayout полностью инициализирован");
            } catch (Exception e) {
                log.error("Ошибка инициализации MainLayout", e);
            }
        }));
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        log.debug("MainLayout detach");
        try {
            if (notificationListener != null) {
                log.debug("Уничтожение notification listener");
                notificationListener.destroy();
            }
        } catch (Exception e) {
            log.warn("Ошибка при detach notification listener", e);
        } finally {
            super.onDetach(detachEvent);
        }
    }
}
