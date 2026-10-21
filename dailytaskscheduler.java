
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DailyTaskScheduler extends JFrame {

    // Store registered users and their tasks
    Map<String, String> users = new HashMap<>();
    Map<String, ArrayList<Task>> userTasks = new HashMap<>();

    String currentUser;

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    JTextField usernameField = new JTextField(15);
    JPasswordField passwordField = new JPasswordField(15);

    JTextField taskField = new JTextField(15);
    JTextField dueField = new JTextField(15);

    DefaultTableModel model;
    JTable taskTable;

    JLabel summaryLabel = new JLabel("Task Summary");

    // Task class
    static class Task {
        String description;
        String dueTime;
        String status;

        Task(String description, String dueTime) {
            this.description = description;
            this.dueTime = dueTime;
            this.status = "Pending";
        }
    }

    public DailyTaskScheduler() {
        setTitle("Daily Task Scheduler");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createLoginPage();
        createDashboard();

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    // LOGIN AND REGISTRATION PAGE
    void createLoginPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(235, 242, 255));

        JPanel form = new JPanel(new GridLayout(5, 1, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        form.setPreferredSize(new Dimension(330, 250));

        JLabel title = new JLabel("DAILY TASK SCHEDULER",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 19));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        form.add(title);
        form.add(usernameField);
        form.add(passwordField);
        form.add(loginButton);
        form.add(registerButton);

        panel.add(form);
        mainPanel.add(panel, "login");

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
    }

    void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter username and password.");
            return;
        }

        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists.");
            return;
        }

        users.put(username, password);
        userTasks.put(username, new ArrayList<>());

        JOptionPane.showMessageDialog(this,
                "Registration successful! Please log in.");
    }

    void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (users.containsKey(username)
                && users.get(username).equals(password)) {

            currentUser = username;
            refreshTable();
            updateSummary();
            cardLayout.show(mainPanel, "dashboard");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid login. Please register first.");
        }
    }

    // MAIN TASK DASHBOARD
    void createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("My Daily Tasks",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 23));

        panel.add(heading, BorderLayout.NORTH);

        // Task input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                "Create a New Task"));

        inputPanel.add(new JLabel("Task Description:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Due Time:"));
        inputPanel.add(dueField);

        model = new DefaultTableModel(
                new String[]{"Task Description", "Due Time", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Task");
        JButton completeButton = new JButton("Mark Completed");
        JButton deleteButton = new JButton("Delete Task");
        JButton summaryButton = new JButton("View Performance");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(addButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(summaryButton);
        buttonPanel.add(logoutButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "dashboard");

        addButton.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> completeTask());
        deleteButton.addActionListener(e -> deleteTask());
        summaryButton.addActionListener(e -> showPerformance());

        logoutButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "login");
        });
    }

    // ADD TASK
    void addTask() {
        String description = taskField.getText().trim();
        String dueTime = dueField.getText().trim();

        if (description.isEmpty() || dueTime.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter task description and due time.");
            return;
        }

        userTasks.get(currentUser).add(new Task(description, dueTime));

        taskField.setText("");
        dueField.setText("");

        refreshTable();
        updateSummary();
    }

    // DISPLAY TASKS
    void refreshTable() {
        model.setRowCount(0);

        if (currentUser == null) return;

        for (Task task : userTasks.get(currentUser)) {
            model.addRow(new Object[]{
                    task.description, task.dueTime, task.status
            });
        }
    }

    // MARK TASK AS COMPLETED
    void completeTask() {
        int row = taskTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task first.");
            return;
        }

        userTasks.get(currentUser).get(row).status = "Completed";

        refreshTable();
        updateSummary();
    }

    // DELETE TASK
    void deleteTask() {
        int row = taskTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a task to delete.");
            return;
        }

        userTasks.get(currentUser).remove(row);

        refreshTable();
        updateSummary();
    }

    // TASK SUMMARY
    void updateSummary() {
        if (currentUser == null) return;

        int total = userTasks.get(currentUser).size();
        int completed = 0;

        for (Task task : userTasks.get(currentUser)) {
            if (task.status.equals("Completed")) {
                completed++;
            }
        }

        int pending = total - completed;
        double rate = total == 0 ? 0 : completed * 100.0 / total;

        summaryLabel.setText(String.format(
                "Total: %d    Completed: %d    Pending: %d    Completion: %.1f%%",
                total, completed, pending, rate));
    }

    void showPerformance() {
        updateSummary();

        StringBuilder report = new StringBuilder();
        report.append("DAILY PERFORMANCE REPORT\n\n");

        for (Task task : userTasks.get(currentUser)) {
            report.append(task.description)
                    .append(" - ")
                    .append(task.status)
                    .append("\n");
        }

        report.append("\n").append(summaryLabel.getText());

        JOptionPane.showMessageDialog(this,
                report.toString(), "Task Performance",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DailyTaskScheduler app = new DailyTaskScheduler();
            app.setVisible(true);
        });
    }
}
