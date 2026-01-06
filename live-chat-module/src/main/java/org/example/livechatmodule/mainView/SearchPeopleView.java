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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.Role;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFilterDto;
import org.example.livechatmodule.client.UserClient;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@CssImport("./styles/friends-view-styles.css")
@Route(value = "search-people", layout = MainLayout.class)
public class SearchPeopleView extends VerticalLayout {

    private final UserClient userClient;

    // 🔥 ОТДЕЛЬНЫЕ ПОЛЯ ДЛЯ ИМЕНИ И ФАМИЛИИ
    private TextField firstNameField, lastNameField;
    private ComboBox<Role> roleFilter;
    private DatePicker birthdayFrom, birthdayTo;
    private VerticalLayout searchFormLayout;
    private Div resultsContainer;

    public SearchPeopleView(UserClient userClient) {
        this.userClient = userClient;
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
        loadAllUsers();
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

        // 🔥 ОТДЕЛЬНЫЕ ПОЛЯ ИМЕНИ И ФАМИЛИИ
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
        log.info("🔍 Загружаем всех пользователей...");
        UserFilterDto emptyFilter = new UserFilterDto();
        List<UserDto> allUsers = userClient.searchUsers(emptyFilter, 0, 100);
        showResults(allUsers);
    }

    private void performSearch() {
        UserFilterDto filter = new UserFilterDto();

        // 🔥 ОТДЕЛЬНЫЙ ПОИСК ПО ИМЕНИ И ФАМИЛИИ
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

        log.info("🔍 Поиск: имя='{}', фамилия='{}', роль={}, даты={}-{}",
                firstName, lastName, role, fromDate, toDate);

        List<UserDto> users = userClient.searchUsers(filter, 0, 20);
        showResults(users);
    }

    private void showResults(List<UserDto> users) {
        resultsContainer.removeAll();

        H3 title = new H3("Результаты поиска (" + users.size() + ")");

        if (users.isEmpty()) {
            Paragraph empty = new Paragraph("Пользователи не найдены 😔");
            empty.getStyle().set("color", "#6b7b8a").set("font-size", "16px");
            resultsContainer.add(title, empty);
            return;
        }

        Div grid = new Div();
        grid.addClassName("search-grid");

        users.forEach(user -> grid.add(userCard(user)));
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

        cardContent.add(avatar, name, emailText, profileBtn);
        cardContainer.add(cardContent);

        cardContainer.addClickListener(e ->
                UI.getCurrent().navigate("profile/" + user.getUserId()));

        return cardContainer;
    }
}
