package org.example.livechatmodule.mainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.auth.LoginUserDto;
import org.example.common.dto.auth.RegistrationUserDto;
import org.example.livechatmodule.client.AuthClient;

import java.util.Optional;

@Slf4j
@Route("auth")
public class AuthView extends VerticalLayout {

    private final AuthClient authClient;

    // Поля регистрации
    private final TextField regEmail = new TextField("Email");
    private final TextField regFirstName = new TextField("Имя");
    private final TextField regLastName = new TextField("Фамилия");
    private final TextField regPhone = new TextField("Телефон");
    private final PasswordField regPassword = new PasswordField("Пароль");
    private final DatePicker regBirthday = new DatePicker("Дата рождения");

    // Поля логина
    private final TextField loginEmail = new TextField("Email");
    private final PasswordField loginPassword = new PasswordField("Пароль");

    public AuthView(AuthClient authClient) {
        this.authClient = authClient;
        log.info("Инициализация AuthView");
        buildLayout();
    }

    private void buildLayout() {
        log.debug("Построение layout авторизации");
        setSizeFull();
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("background-color", "#f8fafc");

        HorizontalLayout mainContainer = new HorizontalLayout();
        mainContainer.setWidth("1200px");
        mainContainer.setHeight("700px");
        mainContainer.setSpacing(true);
        mainContainer.setPadding(true);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        Component regForm = buildRegistrationForm();
        Component loginForm = buildLoginForm();

        mainContainer.add(regForm, loginForm);
        mainContainer.setFlexGrow(1, regForm);
        mainContainer.setFlexGrow(1, loginForm);

        add(mainContainer);
        log.debug("Layout авторизации построен");
    }

    private Component buildRegistrationForm() {
        log.debug("Создание формы регистрации");
        VerticalLayout regCard = new VerticalLayout();
        regCard.setWidth("550px");
        regCard.setHeight("650px");
        regCard.setPadding(true);
        regCard.setSpacing(false);
        regCard.setAlignItems(FlexComponent.Alignment.STRETCH);

        regCard.getStyle()
                .set("background", "linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%)")
                .set("border-radius", "24px")
                .set("box-shadow", "0 15px 50px rgba(0,0,0,0.15)")
                .set("border", "1px solid rgba(255,255,255,0.9)");

        H3 regTitle = new H3("👤 Регистрация");
        regTitle.getStyle()
                .set("margin", "0 0 28px 0")
                .set("text-align", "center")
                .set("color", "#1e293b")
                .set("font-weight", "600")
                .set("font-size", "24px");

        regEmail.setWidthFull();
        regFirstName.setWidthFull();
        regLastName.setWidthFull();
        regPhone.setWidthFull();
        regPassword.setWidthFull();
        regBirthday.setWidthFull();

        regCard.add(regTitle, regEmail, regFirstName, regLastName, regPhone, regPassword, regBirthday);

        Button regBtn = new Button("🎉 Создать аккаунт", e -> handleSignUp());
        regBtn.setWidthFull();
        regBtn.getStyle()
                .set("background-color", "#4f46e5")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("padding", "16px")
                .set("margin-top", "24px")
                .set("font-size", "16px");

        regCard.add(regBtn);
        return regCard;
    }

    private Component buildLoginForm() {
        log.debug("Создание формы логина");
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("450px");
        loginCard.setHeight("500px");
        loginCard.setPadding(true);
        loginCard.setSpacing(false);
        loginCard.setAlignItems(FlexComponent.Alignment.STRETCH);
        loginCard.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "24px")
                .set("box-shadow", "0 15px 50px rgba(0,0,0,0.15)")
                .set("border", "1px solid #e2e8f0");

        H3 loginTitle = new H3("🔑 Быстрый вход");
        loginTitle.getStyle()
                .set("margin", "0 0 36px 0")
                .set("text-align", "center")
                .set("color", "#1e293b")
                .set("font-weight", "600")
                .set("font-size", "24px");

        loginEmail.setWidthFull();
        loginPassword.setWidthFull();

        loginCard.add(loginTitle, loginEmail, loginPassword);

        Button loginBtn = new Button("🚀 Войти", e -> handleLogin());
        loginBtn.setWidthFull();
        loginBtn.getStyle()
                .set("background-color", "#10b981")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("font-weight", "600")
                .set("padding", "20px")
                .set("margin-top", "32px")
                .set("font-size", "18px");

        loginCard.add(loginBtn);
        return loginCard;
    }

    private void handleSignUp() {
        log.info("Обработка регистрации: {}", regEmail.getValue());
        try {
            RegistrationUserDto dto = new RegistrationUserDto(
                    regFirstName.getValue(),
                    regLastName.getValue(),
                    regEmail.getValue(),
                    regPhone.getValue(),
                    regPassword.getValue(),
                    regBirthday.getValue()
            );
            authClient.signUp(dto);
            log.info("Пользователь {} успешно зарегистрирован", regEmail.getValue());
            Notification.show("✅ Аккаунт создан! Теперь войдите.", 3000, Notification.Position.TOP_CENTER);
            clearRegFields();
        } catch (Exception ex) {
            log.error("Ошибка регистрации для {}: {}", regEmail.getValue(), ex.getMessage(), ex);
            Notification.show("❌ Ошибка регистрации: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleLogin() {
        String email = loginEmail.getValue();
        log.info("🔑 Логин для: {}", email);

        try {
            LoginUserDto dto = new LoginUserDto(email, loginPassword.getValue());
            authClient.login(dto);
            log.info("✅ Авторизация успешна для {}", email);

            // 🔥 ПРЯМАЯ НАВИГАЦИЯ - без лишних проверок!
            getUI().ifPresent(ui -> {
                ui.navigate("profile"); // Без параметров = свой профиль
                log.info("🔄 Навигация на /profile");
            });

            Notification.show("✅ Добро пожаловать!", 2000, Notification.Position.TOP_CENTER);
            clearLoginFields();

        } catch (Exception ex) {
            log.error("❌ Ошибка логина: {}", ex.getMessage(), ex);
            Notification.show("❌ " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void clearLoginFields() {
        loginEmail.clear();
        loginPassword.clear();
    }

    private void clearRegFields() {
        log.debug("Очистка полей регистрации");
        regEmail.clear();
        regFirstName.clear();
        regLastName.clear();
        regPhone.clear();
        regPassword.clear();
        regBirthday.clear();
    }
}
