//Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.Timer;
import java.lang.Math;

//Calculator Program to make GUI
public class Calculator_Program extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField displayField;
    private StringBuilder currentInput;
    private ArrayList<String> history;
    private boolean cursorVisible;
    private int cursorPosition;

    private static final String AGREEMENT_KEY = "agreementAccepted";

    //GUI Settings
    public Calculator_Program() {
        setTitle("CalC_JavaEdition™");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Terms and Agreements Yes and No functions
        if (!hasUserAgreed()) {
            if (showDisclaimer()) {
                setUserAgreed();
            } else {
                System.exit(0);
            }
        }

   //Display and Buttons
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setFont(new Font("Arial", Font.PLAIN, 30));

        currentInput = new StringBuilder();
        history = new ArrayList<>();
        cursorVisible = true;
        cursorPosition = 0;

        JPanel buttonPanel = createButtonPanel();
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(createDisplayPanel(), BorderLayout.CENTER);
        inputPanel.add(createControlPanel(), BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        new Timer(500, e -> {
            cursorVisible = !cursorVisible;
            updateDisplay();
        }).start();

        displayField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    delete();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveCursorLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveCursorRight();
                }
            }
        });
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 4));
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "^", "√", "+",
                "(", ")", "C", "H"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(e -> buttonClicked(label));
            button.setBackground(new Color(0, 0, 0));
            button.setForeground(new Color(245, 245, 245));
            panel.add(button);
        }

        return panel;
    }

    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.add(displayField, BorderLayout.CENTER);
        return displayPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());

        JButton equalsButton = new JButton("=");
        equalsButton.addActionListener(e -> calculate());
        equalsButton.setBackground(new Color(0, 0, 0));
        equalsButton.setForeground(new Color(245, 245, 245));

        JButton leftMoverButton = new JButton("<");
        JButton rightMoverButton = new JButton(">");
        leftMoverButton.addActionListener(e -> moveCursorLeft());
        rightMoverButton.addActionListener(e -> moveCursorRight());
        leftMoverButton.setBackground(new Color(0, 0, 0));
        leftMoverButton.setForeground(new Color(245, 245, 245));
        rightMoverButton.setBackground(new Color(0, 0, 0));
        rightMoverButton.setForeground(new Color(245, 245, 245));

        JButton deleteButton = new JButton("Del");
        deleteButton.addActionListener(e -> delete());
        deleteButton.setBackground(new Color(0, 0, 0));
        deleteButton.setForeground(new Color(245, 245, 245));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(leftMoverButton);
        buttonPanel.add(rightMoverButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(equalsButton);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        return controlPanel;
    }

//Button click function
    private void buttonClicked(String buttonText) {
        switch (buttonText) {
            case "=":
                calculate();
                break;
            case "H":
                showHistory();
                break;
            case "C":
                clear();
                break;
            case "^":
                insertAtCursor("^");
                break;
            case "√":
                insertAtCursor("√");
                break;
            default:
                insertAtCursor(buttonText);

        }
    }

//Calculation Function
    private void calculate() {
        try {
            String result = evaluateExpression(currentInput.toString());
            history.add(currentInput.toString() + " = " + result);
            currentInput.setLength(0);
            currentInput.append(result);
            cursorPosition = currentInput.length();
            updateDisplay();
        } catch (Exception ex) {
            displayField.setText("Error");
        }
    }

    private String evaluateExpression(String expression) {
        try {
            return String.valueOf(eval(expression));
        } catch (Exception e) {
            return "Error";
        }
    }

//Inputs and blinking bar(Cursor)
    private void insertAtCursor(String text) {
        currentInput.insert(cursorPosition, text);
        cursorPosition += text.length();
        updateDisplay();
    }

    private void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
            updateDisplay();
        }
    }

    private void moveCursorRight() {
        if (cursorPosition < currentInput.length()) {
            cursorPosition++;
            updateDisplay();
        }
    }

    private void delete() {
        if (cursorPosition > 0) {
            currentInput.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
            updateDisplay();
        }
    }

    private void clear() {
        currentInput.setLength(0);
        cursorPosition = 0;
        updateDisplay();
    }

//Update the display screen
    private void updateDisplay() {
        StringBuilder displayText = new StringBuilder();
        displayText.append(currentInput);
        if (cursorVisible) {
            displayText.insert(cursorPosition, "|");
        }
        displayField.setText(displayText.toString());
    }

//History feature
    private void showHistory() {
        StringBuilder historyText = new StringBuilder("History:\n");
        for (int i = 0; i < history.size(); i++) {
            historyText.append(i + 1).append(". ").append(history.get(i)).append("\n");
        }

        historyText.append("\nType the number of the entry to go back to said entry, for example: 2");
        String entryNumberStr = JOptionPane.showInputDialog(this, historyText.toString(),
                "History", JOptionPane.INFORMATION_MESSAGE);

        if (entryNumberStr != null && !entryNumberStr.isEmpty()) {
            try {
                int entryNumber = Integer.parseInt(entryNumberStr);

                if (entryNumber >= 1 && entryNumber <= history.size()) {
                    String selectedEntry = history.get(entryNumber - 1);
                    currentInput.setLength(0);
                    currentInput.append(selectedEntry.substring(0, selectedEntry.indexOf("=")).trim());
                    cursorPosition = currentInput.length();
                    updateDisplay();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid entry number", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

//Terms and Agreements Pop-up
    private boolean hasUserAgreed() {
        return Boolean.parseBoolean(System.getProperty(AGREEMENT_KEY, "false"));
    }

    private void setUserAgreed() {
        System.setProperty(AGREEMENT_KEY, "true");
    }

    private static boolean showDisclaimer() {
    	String agreementText = "TERMS OF USE AND DISCLAIMER\n\n" +
                " 1. Agreement:\n    a. The use of CalC_JavaEdition™ is contingent upon your acceptance of the following terms.\n" +
                "    b. If you disagree, discontinue use.\n\n" +
                " 2. Data Consent:\n    a. By utilizing the application, you expressly grant consent for the collection, storage, and processing of personal and non-personal information.\n" +
                "    b. The Owner may share data for the improvement of the application.\n\n" +
                " 3. Liability Disclaimer:\n    a. The Owner disclaims all liability for any direct, indirect, or consequential damages resulting from the use or inability to use the application.\n" +
                "    b. The application is provided 'as is,' and users assume all risks associated with its use.\n\n" +
                " 4. Governing Law:\n    a. These terms are governed by the laws of [Your Jurisdiction].\n" +
                "    b. Any disputes shall be subject to the exclusive jurisdiction of the courts in [Your Jurisdiction].\n\n" +
                " 5. Indemnification:\n    a. You agree to indemnify and hold harmless the Owner, including its affiliates, officers, employees, and agents, from any claims or liabilities arising from your use of the application.\n" +
                "    b. This indemnification clause survives termination.\n\n" +
                " 6. Modification:\n    a. The Owner reserves the right to modify these terms without notice.\n" +
                "    b. Users are responsible for regularly reviewing these terms. Continued use constitutes acceptance of any changes.\n\n" +
                " 7. Security Measures:\n    a. While the Owner takes reasonable security measures.\n" +
                "    b. Users acknowledge that no method of transmission over the internet or electronic storage is 100% secure.\n" +
                "    c. Users are responsible for securing access credentials.\n\n" +
                "8. Entertainment Disclaimer:\n    a. All statements and terms are for entertainment purposes only.\n" +
                "    b. They are not real or legally binding. The Owner holds no responsibilities for the information provided herein.";


        int result = JOptionPane.showConfirmDialog(null, agreementText, "Terms and Conditions", JOptionPane.YES_NO_OPTION);

        return result == JOptionPane.YES_OPTION;
    }

//Main Method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator_Program calculator = new Calculator_Program();
            calculator.setVisible(true);
        });
    }

//Math for Calculation Function
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean isDigit(char c) {
                return Character.isDigit(c);
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if (isDigit((char) ch) || ch == '.') {
                    while (isDigit((char) ch) || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else if (eat('√')) {
                    x = Math.sqrt(parseFactor());
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                while (true) {
                    startPos = this.pos;
                    if (eat('^')) {
                        x = Math.pow(x, parseFactor());
                    } else {
                        this.pos = startPos;
                        return x;
                    }
                }
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
        }.parse();
    }
}
