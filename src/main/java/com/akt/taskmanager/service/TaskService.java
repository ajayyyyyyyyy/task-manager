package com.akt.taskmanager.service;

import com.akt.taskmanager.entity.Task;
import com.akt.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public List<Task> getTasks(String filter) {
        if (filter == null || filter.isEmpty()) {
            return taskRepository.findAll();
        } else {
            return taskRepository.findByDescriptionContainingIgnoreCase(filter);
        }
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
