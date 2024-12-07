package com.akt.taskmanager.controller;

import com.akt.taskmanager.entity.Task;
import com.akt.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadTasksAsCsv() {
        List<Task> tasks = taskService.getAllTasks();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            // Write CSV header
            writer.println("ID,Description,Created Date,Status,Comments");

            // Write task data
            for (Task task : tasks) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        task.getId(),
                        task.getDescription(),
                        task.getCreatedDate(),
                        task.getStatus(),
                        task.getComments());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }
}
