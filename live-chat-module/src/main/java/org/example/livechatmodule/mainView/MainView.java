package org.example.livechatmodule.mainView;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.persistence.EntityNotFoundException;
import org.example.common.dto.RequestData;
import org.example.common.dto.UserDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.Key;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.isNull;

@Route("")
public class MainView extends VerticalLayout {

    private final SecuredHttpCore http;
    private final Storage storage;
    private Registration registration;

    private Grid<Storage.ChatMessage> grid;
    private VerticalLayout chat;
    private UserDto currentUser;
    private VerticalLayout login;

    public MainView(Storage storage, SecuredHttpCore http) {
        this.storage = storage;
        this.http = http;

        buildLogin();
        buildChat();
    }

    /* ---------- LOGIN UI ---------- */

    private void buildLogin() {
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("background-color", "#e5ebf1"); // светло-серый фон, как у ВК

        login = new VerticalLayout();
        login.setWidth("360px");
        login.setPadding(true);
        login.setSpacing(true);
        login.setAlignItems(Alignment.STRETCH);
        login.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("margin-top", "12vh");

        H3 title = new H3("Live Chat");
        title.getStyle()
                .set("margin", "0 0 8px 0")
                .set("font-weight", "600")
                .set("text-align", "center")
                .set("color", "#2c3e50");

        TextField emailField = new TextField("Email");
        emailField.setWidthFull();
        emailField.setPlaceholder("user@example.com");

        Button loginButton = new Button("Войти", e -> {
            currentUser = getUserFromApi(emailField.getValue());

            login.setVisible(false);
            chat.setVisible(true);
            storage.addRecordJoined(currentUser.getUsername());
        });
        loginButton.setWidthFull();
        loginButton.getStyle()
                .set("background-color", "#4a76a8") // синий ВК
                .set("color", "white");

        login.add(title, emailField, loginButton);
        add(login);
    }

    private void buildChat() {
        chat = new VerticalLayout();
        chat.setSizeFull();
        chat.setVisible(false);
        chat.setPadding(true);
        chat.setSpacing(false);
        chat.setAlignItems(Alignment.STRETCH);
        chat.getStyle().set("background-color", "#e5ebf1");

        // «шапка» чата
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("background-color", "#4a76a8")
                .set("color", "white");

        H3 title = new H3("Чат с пользователями пока-что");
        title.getStyle()
                .set("margin", "0")
                .set("color", "white")
                .set("flex-grow", "1");

        header.add(title);

        // "ДРУЗЬЯ"
        Button friendsBtn = new Button("👥 Друзья", e -> {
            if (currentUser != null) {
                UI.getCurrent().navigate("friends/" + currentUser.getUserId());
            }
        });
        friendsBtn.getStyle()
                .set("background-color", "transparent")
                .set("color", "white")
                .set("border", "1px solid rgba(255,255,255,0.3)")
                .set("border-radius", "6px")
                .set("padding", "8px 16px")
                .set("font-weight", "500")
                .set("cursor", "pointer");
        header.add(friendsBtn);

        // область сообщений
        grid = new Grid<>();
        grid.setWidthFull();
        grid.setHeight("60vh");
        grid.getStyle().set("background-color", "white");
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
                .setAutoWidth(true)
                .setFlexGrow(1);

        // нижняя панель
        TextField field = new TextField();
        field.setPlaceholder("Введите сообщение...");
        field.setWidthFull();

        Button send = new Button("Отправить");
        send.getStyle()
                .set("background-color", "#4a76a8")
                .set("color", "white");

        send.addClickListener(click -> {
            if (currentUser != null && !field.isEmpty()) {
                storage.addRecord(currentUser.getUsername(), field.getValue());
                field.clear();
            }
        });
        send.addClickShortcut(Key.ENTER);

        HorizontalLayout inputBar = new HorizontalLayout(field, send);
        inputBar.setWidthFull();
        inputBar.setPadding(true);
        inputBar.setSpacing(true);
        inputBar.setAlignItems(Alignment.CENTER);
        inputBar.getStyle()
                .set("background-color", "#dfe6ee")
                .set("border-top", "1px solid #c5d0db");
        inputBar.setFlexGrow(1, field);

        chat.add(header, grid, inputBar);
        add(chat);
    }

    /* ---------- LOGIC ---------- */

    public void onMessage(Storage.ChatEvent event) {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                grid.getDataProvider().refreshAll();
                grid.scrollToEnd();
            });
        });
    }

    private String renderRow(Storage.ChatMessage message) {
        if (message.getName().isEmpty()) {
            return Processor.process(
                    String.format("_User **%s** is just joined the chat!_", message.getMessage())
            );
        } else {
            return Processor.process(
                    String.format("**%s**: %s", message.getName(), message.getMessage())
            );
        }
    }

    protected UserDto getUserFromApi(String email) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/search/by-email?email=%s"
                        .formatted(email),
                null
        );

        ResponseEntity<UserDto> response =
                http.get(requestData, UserDto.class);

        if (isNull(response.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return response.getBody();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}
