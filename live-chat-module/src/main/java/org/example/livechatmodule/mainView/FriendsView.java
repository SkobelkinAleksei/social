package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.Route;
import jakarta.persistence.EntityNotFoundException;
import org.example.common.dto.FriendDto;
import org.example.livechatmodule.client.FriendClient;

import java.util.List;

@Route("friends/:userId")
public class FriendsView extends VerticalLayout implements BeforeEnterObserver {

    private final FriendClient friendClient; // ← НОВЫЙ КЛИЕНТ

    public FriendsView(FriendClient friendClient) {
        this.friendClient = friendClient;
        setSizeFull();
        getStyle().set("background-color", "#e5ebf1");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // ✅ БЕЗ ХАРДКОДА — получаем из роута!
        String userIdParam = event.getRouteParameters().get("userId")
                .orElseThrow(() -> new EntityNotFoundException("------------"));

        Long currentUserId = Long.valueOf(userIdParam);

        // ✅ РЕАЛЬНЫЙ API CALL
        List<FriendDto> friends = friendClient.getFriends(currentUserId);

        removeAll();
        add(buildFriendsContent(friends));
    }

    private Component buildFriendsContent(List<FriendDto> friends) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H3 title = new H3("Мои друзья (" + friends.size() + ")");
        title.getStyle().set("color", "#2c3e50");

        if (friends.isEmpty()) {
            Paragraph empty = new Paragraph("У вас пока нет друзей 😔");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
            content.add(title, empty);
            return content;
        }

        // Список друзей в карточках
        Div friendsGrid = new Div();
        friendsGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))")
                .set("gap", "16px")
                .set("width", "100%")
                .set("max-width", "800px");

        friends.forEach(friend -> friendsGrid.add(friendCard(friend)));

        content.add(title, friendsGrid);
        return content;
    }

    private Component friendCard(FriendDto friend) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                .set("cursor", "pointer")
                .set("transition", "transform 0.2s")
                .set("width", "280px");

        // Hover эффект
        card.getElement().addEventListener("mouseenter", e ->
                card.getStyle().set("transform", "translateY(-4px)"));
        card.getElement().addEventListener("mouseleave", e ->
                card.getStyle().set("transform", "translateY(0)"));

        // Клик по карточке → профиль друга
        card.addClickListener(e -> UI.getCurrent().navigate("profile/" + friend.getAddresseeId()));

        Avatar avatar = new Avatar("Друг");
        avatar.setColorIndex((int) (friend.getAddresseeId() % 10));
        avatar.setWidth("48px");
        avatar.setHeight("48px");

        H3 name = new H3("Друг #" + friend.getAddresseeId()); // ← позже подтянешь имя из API
        name.getStyle().set("margin", "8px 0 4px 0").set("font-size", "18px");

        Paragraph idText = new Paragraph("ID: " + friend.getAddresseeId());
        idText.getStyle().set("color", "#6b7b8a").set("font-size", "14px").set("margin", "0");

        card.add(avatar, name, idText);
        return card;
    }
}
