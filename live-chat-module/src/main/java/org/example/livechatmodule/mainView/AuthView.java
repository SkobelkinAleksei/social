package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import org.example.common.dto.LoginResponse;
import org.example.common.dto.LoginUserDto;
import org.example.common.dto.RegistrationUserDto;
import org.example.livechatmodule.client.AuthClient;

@Route("auth")
public class AuthView extends VerticalLayout {

    private final AuthClient authClient;

    // поля регистрации
    private final TextField email = new TextField("Email");
    private final TextField username = new TextField("Имя");
    private final TextField lastName = new TextField("Фамилия");
    private final TextField phone = new TextField("Телефон");
    private final PasswordField password = new PasswordField("Пароль");
    private final DatePicker birthday = new DatePicker("Дата рождения");

    public AuthView(AuthClient authClient) {
        this.authClient = authClient;
        buildLayout();
    }

    private void buildLayout() {
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("background-color", "#e5ebf1");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("420px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.STRETCH);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("margin-top", "8vh");

        H3 title = new H3("Регистрация");
        title.getStyle()
                .set("margin", "0 0 8px 0")
                .set("font-weight", "600")
                .set("text-align", "center");

        email.setWidthFull();
        username.setWidthFull();
        lastName.setWidthFull();
        phone.setWidthFull();
        password.setWidthFull();
        birthday.setWidthFull();

        Button signUp = new Button("Зарегистрироваться", e -> handleSignUp());
        signUp.setWidthFull();

        // блок логина
        TextField loginEmail = new TextField("Email для входа");
        PasswordField loginPass = new PasswordField("Пароль");
        loginEmail.setWidthFull();
        loginPass.setWidthFull();

        Button loginButton = new Button("Войти", e -> handleLogin(loginEmail.getValue(), loginPass.getValue()));
        loginButton.setWidthFull();

        card.add(
                title,
                email, username, lastName, phone, password, birthday,
                signUp,
                new Hr(),
                new H4("Уже есть аккаунт?"),
                loginEmail, loginPass, loginButton
        );

        add(card);
    }

    private void handleSignUp() {
        try {
            RegistrationUserDto dto = new RegistrationUserDto(
                    username.getValue(),
                    lastName.getValue(),
                    email.getValue(),
                    phone.getValue(),
                    password.getValue(),
                    birthday.getValue()
            );
            authClient.signUp(dto);
            Notification.show("Аккаунт создан. Теперь войдите.", 3000, Notification.Position.TOP_CENTER);
        } catch (Exception ex) {
            Notification.show("Ошибка регистрации: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleLogin(String loginEmail, String pass) {
        try {
            LoginUserDto dto = new LoginUserDto(loginEmail, pass);
            LoginResponse resp = authClient.login(dto);

            Notification.show("Успешный вход", 2000, Notification.Position.TOP_CENTER);

            // переходим на страницу профиля, передаём userId в URL
            UI.getCurrent().navigate("profile/" + resp.userId());
        } catch (Exception ex) {
            Notification.show("Ошибка входа: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }
}
