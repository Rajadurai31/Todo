package com.todo.dao;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.model.Todo;
import com.todo.util.DatabaseConnection;


public class TodoAppDAO {
    private Todo getTodoRow(ResultSet res) throws SQLException{
        return new Todo(
                res.getInt("id"),
                res.getString("title"),
                res.getString("description"),
                res.getTimestamp("created_at").toLocalDateTime(),
                res.getBoolean("completed"),
                res.getTimestamp("updated_at").toLocalDateTime()
        );
    }
    public List<Todo> getAllTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos ORDER BY created_at DESC");
             ResultSet res = stmt.executeQuery();
        ) {
            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }
}
