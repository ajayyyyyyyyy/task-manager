package com.akt.taskmanager.ui;

import com.akt.taskmanager.entity.Task;
import com.akt.taskmanager.entity.TaskStatus;
import com.akt.taskmanager.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
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
       // grid.setColumns("description", "createdDate");
        grid.removeAllColumns();
        grid.addComponentColumn(this::createTaskNumberColumn)
                .setHeader("No.")
                .setAutoWidth(true)
                .setResizable(true);

        grid.addColumn(Task::getDescription)
                .setSortable(true)
                .setHeader("Description")
                .setAutoWidth(true)
                .setResizable(true);

        grid.addColumn(Task::getCreatedDate)
                .setSortable(true)
                .setHeader("Created Date")
                .setAutoWidth(true)
                .setResizable(true);
        grid.addColumn(Task::getStatus)
                .setSortable(true)
                .setHeader("Status")
                .setAutoWidth(true)
                .setResizable(true);

        grid.addComponentColumn(this::createDeleteButton)
                .setHeader("Actions").setAutoWidth(true).setResizable(true);
        grid.addComponentColumn(this::createEditableCommentsField)
                .setHeader("Comments").setAutoWidth(true).setResizable(true);
        grid.addComponentColumn(this::createStatusComboBox) // Add editable status column
                .setHeader("Update Status").setAutoWidth(true).setResizable(true);
        // Filter setup
        filterText = new TextField();
        filterText.setPlaceholder("Filter tasks...");
        filterText.addValueChangeListener(e -> updateTaskList());

        // Add task form
        TaskForm taskForm = new TaskForm(taskService, grid);
        Button addButton = new Button("Add Task", e -> taskForm.open());

        Button downloadButton = new Button("Download CSV");
        downloadButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.getPage().open("/api/tasks/download"));
        });
        // Layout setup
//        add(title, addButton, filterText, grid);
        add(addButton,downloadButton, filterText, grid);

        updateTaskList(); // Initial update of the task list
    }

    private Span createTaskNumberColumn(Task task) {
        // Get all tasks currently displayed in the grid
        var items = grid.getDataProvider().fetch(new Query<>()).toList();
        // Find the index of the current task and add 1 for human-friendly numbering
        int rowIndex = items.indexOf(task) + 1;
        return new Span(String.valueOf(rowIndex));
    }

    private TextArea createEditableCommentsField(Task task) {
        TextArea commentsField = new TextArea();
        commentsField.setWidthFull();
        commentsField.setValue(task.getComments());
        commentsField.setAutoselect(true);
        commentsField.setClearButtonVisible(true);
        commentsField.addValueChangeListener(event -> {
            task.setComments(event.getValue());
            taskService.saveTask(task); // Save the updated task comments
            Notification.show("Comments updated", 2000, Notification.Position.MIDDLE);
            updateTaskList();
        });
        return commentsField;
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
