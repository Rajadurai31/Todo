package com.todo.gui;
import com.model.Todo;
import com.todo.dao.TodoAppDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TodoAppGUI extends JFrame {
    private TodoAppDAO todoDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton clear;
    private JComboBox<String> filterComboBox;

    public TodoAppGUI() {
        this.todoDAO = new TodoAppDAO();
        initializeComponents();
        setupLayout();
        setupEvenListeners();
        loadTodos();
    }

    private void initializeComponents() {
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Title", "Description", "Completed", "Created At", "Updated At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoTable.getSelectionModel().addListSelectionListener(
                (e) -> {
                    if (!e.getValueIsAdjusting()) {
                        loadSelectedtodo();
                    }
                }
        );

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Completed");
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        clear = new JButton("Clear");
        String[] filterOptions = {"All", "Completed", "Pending"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener((e) -> {
            filterTodos();
        });

    }
    private void filterTodos(){
        String option = filterComboBox.getSelectedItem().toString();
             try {
                 List<Todo> todos;
                 if (option.equals("ALL")) {
                     loadTodos();
                 } else if (option.equals("Completed")) {
                     todos = todoDAO.filterTodo(true);
                     updateTable(todos);
                 } else {
                     todos = todoDAO.filterTodo(false);
                     updateTable(todos);
                 }
             }catch (SQLException e){
                 JOptionPane.showMessageDialog(this,"Error loading todos: "+ e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
             }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx=0;
        gbc.gridy=0;
        inputPanel.add(new JLabel("Title"),gbc);

        gbc.gridx =1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField,gbc);

        gbc.gridx=0;
        gbc.gridy=1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Description"),gbc);
        gbc.gridx=1;
        inputPanel.add(new JScrollPane(descriptionArea),gbc);

        gbc.gridx = 1;
        gbc.gridy=2;
        inputPanel.add(completedCheckBox,gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);
        northPanel.add(buttonPanel,BorderLayout.SOUTH);
        northPanel.add(filterPanel,BorderLayout.NORTH);
//        add(inputPanel,BorderLayout.NORTH);

        add(northPanel,BorderLayout.NORTH);
        add(new JScrollPane(todoTable),BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout((FlowLayout.CENTER),10,10));
        statusPanel.add(new JLabel("Select"));
        add(statusPanel,BorderLayout.SOUTH);


    }
    private void setupEvenListeners(){
        addButton.addActionListener((e)->{addTodo();});
        updateButton.addActionListener((e)->{updateTodo();});
        deleteButton.addActionListener((e)->{deleteTodo();});
        refreshButton.addActionListener((e)->{refreshTodo();});
//        clear.addActionListener((e)->{clearTodo();});
    }
    private void addTodo(){
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter a title for the todo","Validation Error",JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Todo todo = new Todo(title,description);
            todo.setCompleted(completed);
            todoDAO.createtodo(todo);

            JOptionPane.showMessageDialog(this,"Todo added succesfully","Success",JOptionPane.INFORMATION_MESSAGE);
            loadTodos();
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error adding todo","Failure",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateTodo(){
       int row = todoTable.getSelectedRow();
       if(row==-1){
           JOptionPane.showMessageDialog(this,"Please select a row to update","Validation Error",JOptionPane.WARNING_MESSAGE);
           return;
       }
       String title = titleField.getText().trim();
       if(title.isEmpty()){
           JOptionPane.showMessageDialog(this,"Please enter a title for  the todo","Validation Error",JOptionPane.WARNING_MESSAGE);
           return;
       }

       int id = (int)todoTable.getValueAt(row,0);
        try{
            Todo todo  = todoDAO.getTodoBYId(id);
            if(todo!=null){
                todo.setTitle(title);
                todo.setDescription(descriptionArea.getText().trim());
                todo.setCompleted(completedCheckBox.isSelected());
                if(todoDAO.updateTodo(todo)){
                    JOptionPane.showMessageDialog(this,"Todo Updated Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                    loadTodos();
                }
                else{
                    JOptionPane.showMessageDialog(this,"Failed to update todo","Update Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(this,"Error Updating todo"+ e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearTodo(Todo todo){
        todo.setTitle("");
        todo.setDescription("");
        todo.setCompleted(false);
    }
    private void deleteTodo(){
        int row =  todoTable.getSelectedRow();
        if(row==-1){
            JOptionPane.showMessageDialog(this,"Plese select a exist row","Validation Error",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) todoTable.getValueAt(row,0);
        try {
            boolean res = todoDAO.deleteTodo(id);
            if(res){
//                Todo todo  = todoDAO.getTodoBYId(id);
                JOptionPane.showMessageDialog(this,"Todo deleted successfully","Succes",JOptionPane.INFORMATION_MESSAGE);
//               clearTodo();

            }
            else{
                JOptionPane.showMessageDialog(this,"Failed to delete the todo","ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(this,"Error delete todo"+ e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
        loadTodos();

    }
    private void refreshTodo(){
         loadTodos();
    }
   private  void loadTodos(){
        try
        {
            List<Todo> todos = todoDAO.getAllTodos();
            updateTable(todos);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadSelectedtodo(){
        int row = todoTable.getSelectedRow();
        if(row!=-1){
            String title = tableModel.getValueAt(row,1).toString();
            String description = tableModel.getValueAt(row,2).toString();
            boolean completed = Boolean.parseBoolean(tableModel.getValueAt(row, 3).toString());
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);
        }
    }
    private void updateTable(List<Todo> todos){
        tableModel.setRowCount(0);
        for(Todo t : todos){
            Object[] row = {
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.isCompleted(),
                t.getCreated_at(),
                t.getUpdated_at()
            };
            tableModel.addRow(row);
        }
    }
}
