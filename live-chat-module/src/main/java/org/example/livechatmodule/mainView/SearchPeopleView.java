package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendDto;
import org.example.common.dto.friend.FriendRequestDto;
import org.example.common.dto.friend.FriendRequestStatus;
import org.example.common.dto.user.Role;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFilterDto;
import org.example.common.dto.user.UserFullDto;
import org.example.common.security.SecurityUtil;
import org.example.livechatmodule.client.FriendClient;
import org.example.livechatmodule.client.FriendRequestClient;
import org.example.livechatmodule.client.UserClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@CssImport("./styles/friends-view-styles.css")
@Route(value = "search-people", layout = MainLayout.class)
public class SearchPeopleView extends VerticalLayout {

    private final UserClient userClient;
    private final FriendClient friendClient;
    private final FriendRequestClient friendRequestClient;

    private List<FriendDto> myFriends = List.of();
    private List<FriendRequestDto> outgoingRequests = List.of();
    private List<FriendRequestDto> incomingRequests = List.of();
    private Long myId;
    private List<UserDto> currentUsers = List.of();

    // ОТДЕЛЬНЫЕ ПОЛЯ ДЛЯ ИМЕНИ И ФАМИЛИИ
    private TextField firstNameField, lastNameField;
    private ComboBox<Role> roleFilter;
    private DatePicker birthdayFrom, birthdayTo;
    private VerticalLayout searchFormLayout;
    private Div resultsContainer;

    public SearchPeopleView(UserClient userClient, FriendClient friendClient, FriendRequestClient friendRequestClient) {
        this.userClient = userClient;
        this.friendClient = friendClient;
        this.friendRequestClient = friendRequestClient;
        initView();
    }

    private void initView() {
        searchFormLayout = createSearchForm();
        resultsContainer = new Div();
        resultsContainer.getStyle()
                .set("width", "100%")
                .set("margin-top", "30px")
                .set("padding", "0 20px");

        add(searchFormLayout, resultsContainer);

        loadMyIdSync();
        loadFriendshipDataSync();
        loadAllUsers();
    }

    private void loadMyIdSync() {
        myId = SecurityUtil.getCurrentUserId();
        log.info("🔥 SecurityUtil: myId = {}", myId);

        if (myId == null) {
            log.info("[INFO] SecurityUtil вернул null, грузим через API");
            UserFullDto myProfile = userClient.getMyProfile();
            myId = myProfile != null ? myProfile.getId() : null;
            log.info("[INFO] API myProfile: id = {}", myId);
        }
    }

    private void loadFriendshipDataSync() {
        if (myId == null) {
            log.warn("[WARN] myId=null, пропускаем загрузку друзей");
            return;
        }

        log.info("[INFO] Загружаем данные дружбы для myId={}", myId);

        try {
            myFriends = friendClient.getFriends(myId);
            outgoingRequests = friendRequestClient.getOutgoing(0, 100);
            incomingRequests = friendRequestClient.getIncoming(0, 100);

            log.info("[INFO] ДРУЗЬЯ ({}): {}", myId, myFriends.size());
            log.info("[INFO] ИСХОДЯЩИЕ ({}): {}", myId, outgoingRequests.size());
            log.info("[INFO] ВХОДЯЩИЕ ({}): {}", myId, incomingRequests.size());

        } catch (Exception e) {
            log.error("[ERROR] Ошибка загрузки данных дружбы: {}", e.getMessage());
            myFriends = List.of();
            outgoingRequests = List.of();
            incomingRequests = List.of();
        }
    }

    private VerticalLayout createSearchForm() {
        VerticalLayout form = new VerticalLayout();
        form.addClassName("search-form");
        form.setPadding(true);
        form.setSpacing(true);
        form.setMaxWidth("800px");
        form.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 title = new H3("🔍 Поиск людей");
        title.getStyle().set("color", "#2c3e50");

        firstNameField = new TextField("Имя");
        firstNameField.setWidth("48%");
        firstNameField.setClearButtonVisible(true);

        lastNameField = new TextField("Фамилия");
        lastNameField.setWidth("48%");
        lastNameField.setClearButtonVisible(true);

        HorizontalLayout nameRow = new HorizontalLayout(firstNameField, lastNameField);
        nameRow.setSpacing(true);

        HorizontalLayout filters = new HorizontalLayout();
        roleFilter = new ComboBox<>("Роль");
        roleFilter.setItems(Role.values());
        roleFilter.setClearButtonVisible(true);

        birthdayFrom = new DatePicker("ДР от");
        birthdayTo = new DatePicker("ДР до");

        filters.add(roleFilter, birthdayFrom, birthdayTo);
        filters.setSpacing(true);

        Button searchBtn = new Button("🔍 Найти", e -> performSearch());
        searchBtn.addClassNames("vk-button", "profile-btn");

        form.add(title, nameRow, filters, searchBtn);

        firstNameField.addKeyDownListener(Key.ENTER, e -> performSearch());
        lastNameField.addKeyDownListener(Key.ENTER, e -> performSearch());

        return form;
    }

    private void loadAllUsers() {
        log.info("[INFO] Загружаем всех пользователей...");
        UserFilterDto emptyFilter = new UserFilterDto();
        List<UserDto> allUsers = userClient.searchUsers(emptyFilter, 0, 100);
        currentUsers = allUsers;
        showResults(allUsers);
    }

    private void performSearch() {
        UserFilterDto filter = new UserFilterDto();
        String firstName = firstNameField.getValue().trim();
        String lastName = lastNameField.getValue().trim();

        if (!firstName.isEmpty()) filter.setFirstName(firstName);
        if (!lastName.isEmpty()) filter.setLastName(lastName);

        Role role = roleFilter.getValue();
        if (role != null) filter.setRole(role);

        LocalDate fromDate = birthdayFrom.getValue();
        LocalDate toDate = birthdayTo.getValue();
        if (fromDate != null) filter.setBirthdayFrom(fromDate);
        if (toDate != null) filter.setBirthdayTo(toDate);

        List<UserDto> users = userClient.searchUsers(filter, 0, 20);
        currentUsers = users;
        showResults(users);
    }

    private void showResults(List<UserDto> users) {
        resultsContainer.removeAll();

        long visibleCount = users.stream()
                .filter(user -> myId == null || !user.getUserId().equals(myId))
                .count();

        H3 title = new H3("Результаты поиска (" + visibleCount + ")");

        if (visibleCount == 0) {
            Paragraph empty = new Paragraph("Пользователи не найдены 😔");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
            resultsContainer.add(title, empty);
            return;
        }

        Div grid = new Div();
        grid.addClassName("search-grid");

        users.stream()
                .filter(user -> myId == null || !user.getUserId().equals(myId))
                .forEach(user -> grid.add(userCard(user)));

        resultsContainer.add(title, grid);
    }


    private Component userCard(UserDto user) {
        Div cardContainer = new Div();
        cardContainer.addClassName("friend-card-container");

        String fullName = (user.getFirstName() != null ? user.getFirstName() + " " : "")
                + (user.getLastName() != null ? user.getLastName() : "#ID" + user.getUserId());
        String email = user.getEmail() != null ? user.getEmail() : "—";

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(true);
        cardContent.setAlignItems(FlexComponent.Alignment.CENTER);

        Avatar avatar = new Avatar(fullName);
        avatar.setColorIndex((int) (Math.abs(user.getUserId()) % 10));
        avatar.setWidth("60px");
        avatar.setHeight("60px");

        H3 name = new H3(fullName);
        name.getStyle().set("margin", "8px 0 4px 0").set("font-size", "17px");

        Paragraph emailText = new Paragraph(email);
        emailText.getStyle().set("color", "#6b7b8a").set("font-size", "14px");

        Button profileBtn = new Button("👤 Профиль", e ->
                UI.getCurrent().navigate("profile/" + user.getUserId()));
        profileBtn.addClassNames("vk-button", "profile-btn");

        Button actionBtn = createSmartFriendButton(user.getUserId());

        HorizontalLayout buttons = new HorizontalLayout(profileBtn, actionBtn);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttons.setSpacing(true);

        cardContent.add(avatar, name, emailText, buttons);
        cardContainer.add(cardContent);

        cardContainer.addClickListener(e ->
                UI.getCurrent().navigate("profile/" + user.getUserId()));

        return cardContainer;
    }

    private Button createSmartFriendButton(Long userId) {
        boolean isFriend = myFriends.stream().anyMatch(f ->
                f.getUserId1().equals(userId) || f.getUserId2().equals(userId));

        if (isFriend) {
            return createStatusButton("👥 Друзья", false);
        }

        // 2. ИСХОДЯЩИЕ
        Optional<FriendRequestDto> outgoing = outgoingRequests.stream()
                .filter(r -> r.getAddresseeId().equals(userId))
                .findFirst();

        if (outgoing.isPresent()) {
            return createStatusButton("⏳ " + outgoing.get().getStatus().name(), false);
        }

        // 3. ВХОДЯЩИЕ
        Optional<FriendRequestDto> incoming = incomingRequests.stream()
                .filter(r -> r.getRequesterId().equals(userId))
                .findFirst();

        if (incoming.isPresent()) {
            FriendRequestStatus status = incoming.get().getStatus();
            if (status == FriendRequestStatus.PENDING) {
                Button acceptBtn = new Button("✅ Принять");
                acceptBtn.addClassNames("vk-button", "accept-btn");
                acceptBtn.addClickListener(e -> acceptFriendRequest(incoming.get().getId()));
                return acceptBtn;
            }
            return createStatusButton(status.name(), false);
        }

        // 4. ДОБАВИТЬ
        Button addBtn = new Button("🤝 Добавить в друзья");
        addBtn.addClassNames("vk-button", "add-friend-btn");
        addBtn.addClickListener(e -> addFriend(userId));
        return addBtn;
    }

    private Button createStatusButton(String text, boolean enabled) {
        Button btn = new Button(text);
        btn.addClassNames("vk-button", "status-btn");
        btn.setEnabled(enabled);
        return btn;
    }

    private void addFriend(Long userId) {
        try {
            friendRequestClient.addFriend(userId);
            Notification.show("✅ Заявка отправлена!", 3000, Notification.Position.MIDDLE);
            refreshData();
        } catch (Exception e) {
            Notification.show("❌ Ошибка: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void acceptFriendRequest(Long requestId) {
        try {
            friendRequestClient.acceptRequest(requestId);
            Notification.show("✅ Друг добавлен!", 3000, Notification.Position.MIDDLE);
            refreshData();
        } catch (Exception e) {
            Notification.show("❌ Ошибка: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private void refreshData() {
        loadFriendshipDataSync();
        if (!currentUsers.isEmpty()) {
            showResults(currentUsers);
        }
    }
}