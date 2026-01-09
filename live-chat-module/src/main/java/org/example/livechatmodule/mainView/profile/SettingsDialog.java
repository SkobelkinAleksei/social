package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.example.common.dto.user.UpdateAccountUserDto;
import org.example.common.dto.user.UpdatePasswordUserDto;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFullDto;
import org.example.livechatmodule.client.SettingsClient;
import org.example.livechatmodule.client.UserClient;

import java.time.LocalDate;

@CssImport("./styles/settings-dialog.css")
public class SettingsDialog extends Dialog {
    private final SettingsClient settingsClient;
    private final Long userId;

    // Поля аккаунта (имена совпадают с DTO для bindInstanceFields)
    private final TextField firstName = new TextField("Имя");
    private final TextField lastName = new TextField("Фамилия");
    private final EmailField email = new EmailField("Email");
    private final TextField numberPhone = new TextField("Телефон");
    private final DatePicker birthday = new DatePicker("Дата рождения");

    // Поля пароля
    private final PasswordField oldPassword = new PasswordField("Текущий пароль");
    private final PasswordField newPassword = new PasswordField("Новый пароль");
    private final PasswordField confirmPassword = new PasswordField("Подтвердите пароль");

    private final BeanValidationBinder<UpdateAccountUserDto> accountBinder = new BeanValidationBinder<>(UpdateAccountUserDto.class);
    private final BeanValidationBinder<UpdatePasswordUserDto> passBinder = new BeanValidationBinder<>(UpdatePasswordUserDto.class);

    public SettingsDialog(SettingsClient client, UserClient userClient, Long userId) {
        this.settingsClient = client;
        this.userId = userId;

        setHeaderTitle("⚙️ Настройки аккаунта");
        addClassName("settings-dialog");
        setWidth("900px");
        setHeight("700px");

        setupAccountBinder();
        setupPasswordBinder();

        firstName.addClassNames("form-field", "text-field");
        lastName.addClassNames("form-field", "text-field");
        email.addClassNames("form-field", "email-field");
        numberPhone.addClassNames("form-field", "text-field");
        birthday.addClassNames("form-field", "date-picker");

        oldPassword.addClassNames("form-field", "password-field");
        newPassword.addClassNames("form-field", "password-field");
        confirmPassword.addClassNames("form-field", "password-field");

        Tab accountTab = new Tab("Обновить аккаунт");
        Tab passwordTab = new Tab("Сменить пароль");
        Tabs tabs = new Tabs(accountTab, passwordTab);
        tabs.setWidthFull();

        VerticalLayout activeContent = new VerticalLayout();
        activeContent.setWidthFull();
        activeContent.setHeight("500px");
        activeContent.addClassName("form-container");

        tabs.addSelectedChangeListener(event -> {
            activeContent.removeAll();
            if (event.getSelectedTab() == accountTab) {
                activeContent.add(createAccountForm());
            } else {
                activeContent.add(createPasswordForm());
            }
        });

        activeContent.add(createAccountForm());

        Button saveBtn = new Button("Сохранить", e -> save(tabs.getSelectedTab()));
        saveBtn.addClassName("primary");
        Button cancelBtn = new Button("Отмена", e -> close());
        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        add(tabs, activeContent, buttons);

        preloadAccountData(userClient);
    }

    private void setupAccountBinder() {
        accountBinder.bindInstanceFields(this);
        birthday.setMax(LocalDate.now());
        accountBinder.setBean(new UpdateAccountUserDto());
    }

    private Component createAccountForm() {
        VerticalLayout form = new VerticalLayout();
        form.setSpacing(true);
        form.setPadding(true);
        form.setWidthFull();

        // Строка 1: Имя + Фамилия
        HorizontalLayout row1 = new HorizontalLayout(firstName, lastName);
        row1.addClassName("form-row");
        row1.setWidthFull();

        // Строка 2: Email Телефон
        HorizontalLayout row2 = new HorizontalLayout(email, numberPhone);
        row2.addClassName("form-row");
        row2.setWidthFull();

        // Строка 3: Дата рождения
        HorizontalLayout row3 = new HorizontalLayout(birthday);
        row3.addClassName("form-row");

        form.add(row1, row2, row3);

        Div errors = new Div();
        errors.addClassName("status-label");
        accountBinder.setStatusLabel(errors);
        form.add(errors);

        return form;
    }

    private Component createPasswordForm() {
        VerticalLayout form = new VerticalLayout();
        form.setSpacing(true);
        form.setPadding(true);
        form.setWidthFull();

        // Строка 1: Старый + Новый пароль
        HorizontalLayout row1 = new HorizontalLayout(oldPassword, newPassword);
        row1.addClassName("form-row");
        row1.setWidthFull();

        // Строка 2: Подтверждение
        HorizontalLayout row2 = new HorizontalLayout(confirmPassword);
        row2.addClassName("form-row");

        form.add(row1, row2);

        Div errors = new Div();
        errors.addClassName("status-label");
        passBinder.setStatusLabel(errors);
        form.add(errors);

        oldPassword.setRequired(true);
        oldPassword.setRequiredIndicatorVisible(true);
        newPassword.setRequired(true);
        newPassword.setRequiredIndicatorVisible(true);

        Runnable validate = passBinder::validate;
        oldPassword.addValueChangeListener(e -> {
            validate.run();
            validateConfirmPassword();
        });
        newPassword.addValueChangeListener(e -> {
            validate.run();
            validateConfirmPassword();
        });
        confirmPassword.addValueChangeListener(e -> validateConfirmPassword());

        return form;
    }

    private void validateConfirmPassword() {
        String newPass = newPassword.getValue();
        String confirm = confirmPassword.getValue();

        if (confirm != null && newPass != null && !confirm.equals(newPass)) {
            confirmPassword.setInvalid(true);
            confirmPassword.setErrorMessage("Пароли не совпадают");
        } else {
            confirmPassword.setInvalid(false);
        }
    }

    private void save(Tab selectedTab) {
        boolean isAccountTab = "Обновить аккаунт".equals(selectedTab.getLabel());
        Binder<?> binder = isAccountTab ? accountBinder : passBinder;

        binder.validate();

        if (!binder.isValid()) {
            Notification.show("Исправьте ошибки в форме!", 3000, Notification.Position.MIDDLE);
            return;
        }

        Object bean = binder.getBean();
        if (bean == null) {
            Notification.show("Форма не инициализирована!", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (!isAccountTab) {
            UpdatePasswordUserDto dto = (UpdatePasswordUserDto) bean;
            if (dto.getOldPassword() == null || dto.getOldPassword().trim().isEmpty() ||
                    dto.getNewPassword() == null || dto.getNewPassword().trim().isEmpty()) {
                Notification.show("Заполните все поля пароля!", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (!dto.getNewPassword().equals(confirmPassword.getValue())) {
                Notification.show("Пароли не совпадают!", 3000, Notification.Position.MIDDLE);
                return;
            }
        }

        try {
            if (isAccountTab) {
                UserDto updated = settingsClient.updateAccount((UpdateAccountUserDto) bean);
                if (updated != null) {
                    Notification.show("✅ Аккаунт обновлён!");
                    UI.getCurrent().getPage().reload();
                }
            } else {
                settingsClient.updatePassword((UpdatePasswordUserDto) bean);
                Notification.show("✅ Пароль изменён!");
                UI.getCurrent().getPage().reload();
            }
            close();
        } catch (Exception e) {
            Notification.show("❌ Ошибка: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private void setupPasswordBinder() {
        passBinder.forField(oldPassword)
                .bind(UpdatePasswordUserDto::getOldPassword, UpdatePasswordUserDto::setOldPassword);
        passBinder.forField(newPassword)
                .bind(UpdatePasswordUserDto::getNewPassword, UpdatePasswordUserDto::setNewPassword);

        passBinder.setBean(new UpdatePasswordUserDto());
    }

    private void preloadAccountData(UserClient userClient) {
        UserFullDto profile = userClient.getMyProfile();
        if (profile != null) {
            UpdateAccountUserDto dto = new UpdateAccountUserDto();

            if (profile.getFirstName() != null) dto.setFirstName(profile.getFirstName());
            if (profile.getLastName() != null) dto.setLastName(profile.getLastName());
            if (profile.getEmail() != null) dto.setEmail(profile.getEmail());
            if (profile.getNumberPhone() != null) dto.setNumberPhone(profile.getNumberPhone());
            if (profile.getBirthday() != null) dto.setBirthday(profile.getBirthday());

            accountBinder.setBean(dto);
        }
    }

}
