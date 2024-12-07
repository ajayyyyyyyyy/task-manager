package com.akt.taskmanager.ui;

import com.akt.taskmanager.entity.Task;
import com.akt.taskmanager.entity.TaskStatus;
import com.akt.taskmanager.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("tasks")
@PageTitle("Task Manager")
public class TaskView extends VerticalLayout {

    private final TaskService taskService;
    private final Grid<Task> grid;
    private final TextField filterText;

    public TaskView(TaskService taskService) {
        this.taskService = taskService;
//        H1 title = new H1("Task Manager");
//        title.addClassName("centered-title"); // Optional: you can still apply a class for more control

        // Grid setup
        grid = new Grid<>(Task.class);
//        grid.setColumns("description", "createdDate", "status"); // Add "status" column
        grid.setColumns("description", "createdDate");
        grid.addComponentColumn(task -> createStatusComboBox(task)) // Add editable status column
                .setHeader("Status");
        grid.addComponentColumn(task -> createDeleteButton(task))
                .setHeader("Actions");

        // Filter setup
        filterText = new TextField();
        filterText.setPlaceholder("Filter tasks...");
        filterText.addValueChangeListener(e -> updateTaskList());

        // Add task form
        TaskForm taskForm = new TaskForm(taskService, grid);
        Button addButton = new Button("Add Task", e -> taskForm.open());

        // Layout setup
//        add(title, addButton, filterText, grid);
        add(addButton, filterText, grid);

        updateTaskList(); // Initial update of the task list
    }

    // Method to create the ComboBox for changing task status
    private ComboBox<TaskStatus> createStatusComboBox(Task task) {
        ComboBox<TaskStatus> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(TaskStatus.values()); // Use enum values for status
        statusComboBox.setValue(task.getStatus()); // Set the current status of the task
        statusComboBox.addValueChangeListener(event -> {
            task.setStatus(event.getValue());
            taskService.saveTask(task); // Save the updated task status
            // Notify the UI of the update (push the change to all clients)
            Notification.show("Task status updated", 2000, Notification.Position.MIDDLE);

            // Update task list in real-time
            updateTaskList();
        });
        return statusComboBox;
    }

    private Button createDeleteButton(Task task) {
        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(e -> {
            taskService.deleteTask(task.getId());
            updateTaskList();
        });
        return deleteButton;
    }

    private void updateTaskList() {
        grid.setItems(taskService.getTasks(filterText.getValue()));
    }
}
