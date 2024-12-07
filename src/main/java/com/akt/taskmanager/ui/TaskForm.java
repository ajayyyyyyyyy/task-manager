package com.akt.taskmanager.ui;

import com.akt.taskmanager.entity.Task;
import com.akt.taskmanager.entity.TaskStatus;
import com.akt.taskmanager.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;

public class TaskForm extends Dialog {

    private final TextField descriptionField;
    private final DatePicker createdDateField;
    private final ComboBox<TaskStatus> statusField;
    private final Button saveButton;
    private final Button cancelButton;
    private final TextField commentsField;

    private final TaskService taskService;
    private final Grid<Task> grid;
    private Task task;

    public TaskForm(TaskService taskService, Grid<Task> grid) {
        this.taskService = taskService;
        this.grid = grid;

        descriptionField = new TextField("Description");
        commentsField = new TextField("Comments");
        createdDateField = new DatePicker("Created Date");
        statusField = new ComboBox<>("Status", TaskStatus.values()); // Add status field
        saveButton = new Button("Save", e -> saveTask());
        cancelButton = new Button("Cancel", e -> close());

        add(descriptionField,commentsField, createdDateField, saveButton, cancelButton);
    }

    public void open() {
        task = new Task();
        descriptionField.setValue("");
        commentsField.setValue("");
        createdDateField.setValue(LocalDate.now());
        statusField.setValue(TaskStatus.PENDING); // Reset status to default
        setOpened(true);

    }

    private void saveTask() {
        task.setDescription(descriptionField.getValue());
        task.setComments(commentsField.getValue());
        task.setCreatedDate(createdDateField.getValue().atStartOfDay());
        task.setStatus(statusField.getValue()); // Set selected status
        taskService.saveTask(task);
        grid.setItems(taskService.getTasks(""));
        close();
    }
}
