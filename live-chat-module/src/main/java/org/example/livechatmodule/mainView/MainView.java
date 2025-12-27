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
import org.example.httpcore.httpCore.IHttpCore;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.isNull;

@Route("")
public class MainView extends VerticalLayout {

    private final IHttpCore iHttpCore;
    private final Storage storage;
    private Registration registration;

    private Grid<Storage.ChatMessage> grid;
    private VerticalLayout chat;
    private UserDto currentUser;
    private VerticalLayout login;
    private String user = "";

    public MainView(Storage storage, IHttpCore iHttpCore) {
        this.storage = storage;
        this.iHttpCore = iHttpCore;

        buildLogin();
        buildChat();
    }

    private void buildLogin() {
        login = new VerticalLayout() {{
            TextField emailField = new TextField("Введите Ваш email");
            add(
                    emailField,
                    new Button("Login", e -> {
                        login.setVisible(false);
                        chat.setVisible(true);
                        currentUser = getUserFromApi(emailField.getValue());
                        storage.addRecordJoined(currentUser.getEmail());

                        login.getUI().ifPresent(ui -> ui.navigate(""));
                    }));
        }};

        add(login);
    }
    // docum
    private void buildChat() {
        chat = new VerticalLayout();
        add(chat);
        chat.setVisible(false);

        grid = new Grid<>();
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
                .setAutoWidth(true);

        TextField field = new TextField();

        chat.add(
                new H3("Vaadin chat"),
                grid,
                new HorizontalLayout() {{
                    add(
                            field,
                            new Button("➡") {{
                                addClickListener(click -> {
                                    storage.addRecord(currentUser.getUsername(), field.getValue());
                                    field.clear();
                                });
                                addClickShortcut(Key.ENTER);
                            }}
                    );
                }}
        );
    }

    public void onMessage(Storage.ChatEvent event) {
        if (getUI().isPresent()) {
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.beforeClientResponse(grid, ctx -> grid.scrollToEnd());
            ui.access(() -> grid.getDataProvider().refreshAll());
            ui.getSession().unlock();
        }
    }

    private String renderRow(Storage.ChatMessage message) {
        if (message.getName().isEmpty()) {
            return Processor.process(String.format("_User **%s** is just joined the chat!_", message.getMessage()));
        } else {
            return Processor.process(String.format("**%s**: %s", message.getName(), message.getMessage()));
        }
    }

    protected UserDto getUserFromApi(String email) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/search/by-email?email=%s"
                        .formatted(email),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, null, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody();
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