package com.todo;
import com.todo.gui.TodoAppGUI;
import com.todo.util.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main{
    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DatabaseConnection db_Connection = new DatabaseConnection();
        try{
            Connection cn =db_Connection.getDBConnection();
            System.out.println("Connection established");
        }
        catch(SQLException e){
            System.out.println("Conection Failed");
            System.exit(1);
        }

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            System.err.println("could not set look and feel ",e.getMessage());
//        }
        SwingUtilities.invokeLater(
                ()->{
                    try {
                        new TodoAppGUI().setVisible(true);
                    } catch (Exception e) {
                        System.err.println("Error strarting the application "+e.getLocalizedMessage());
                    }
                }
        );
    }
}