package com.akt.taskmanager.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.RouterLink;

public class MainView extends AppLayout {

    public MainView() {
        H1 title = new H1("Task Manager");
        RouterLink taskLink = new RouterLink("Tasks", TaskView.class);
        addToNavbar(title, taskLink);
    }
}
