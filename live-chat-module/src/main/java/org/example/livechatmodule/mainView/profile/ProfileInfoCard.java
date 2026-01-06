package org.example.livechatmodule.mainView.profile;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.example.common.dto.user.UserDto;

public class ProfileInfoCard extends VerticalLayout {

    public ProfileInfoCard(UserDto user) {
        setWidth("520px");
        setPadding(false);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        addClassName("profile-card");

        add(buildHeader(user), buildInfoSection(user));
    }

    private HorizontalLayout buildHeader(UserDto user) {
        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        avatar.setColorIndex(3);
        avatar.setWidth("72px");
        avatar.setHeight("72px");

        H3 name = new H3(user.getFirstName() + " " + user.getLastName());
        name.addClassName("profile-title-h3-name");

        Paragraph email = new Paragraph(user.getEmail());
        email.addClassName("profile-secondary-text");

        VerticalLayout nameBlock = new VerticalLayout(name, email);
        nameBlock.setPadding(false);
        nameBlock.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(avatar, nameBlock);
        header.setWidthFull();
        header.setSpacing(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        return header;
    }

    private VerticalLayout buildInfoSection(UserDto user) {
        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("margin-top", "12px");

        info.add(
                line("Телефон", user.getNumberPhone() != null ? user.getNumberPhone() : "-"),
                line("Дата рождения", user.getBirthday() != null ? user.getBirthday().toString() : "-"),
                line("Email", user.getEmail() != null ? user.getEmail() : "-")
        );

        return info;
    }

    private HorizontalLayout line(String label, String value) {
        Paragraph l = new Paragraph(label + ":");
        l.addClassName("profile-label");

        Paragraph v = new Paragraph(value);
        v.addClassName("profile-value");

        HorizontalLayout row = new HorizontalLayout(l, v);
        row.setWidthFull();
        row.setSpacing(true);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return row;
    }
}
