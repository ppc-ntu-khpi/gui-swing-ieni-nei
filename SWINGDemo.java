package com.mybank.tui;

import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SWINGDemo {

    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox<String> clients;

    public SWINGDemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(250, 250));
        show = new JButton("Show");
        report = new JButton("Report");
        clients = new JComboBox<>();

        loadCustomersFromFile("test.dat");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName() + ", " + Bank.getCustomer(i).getFirstName());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 3));

        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayCustomerInfo();
            }
        });

        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void displayCustomerInfo() {
        Customer current = Bank.getCustomer(clients.getSelectedIndex());
        StringBuilder custInfo = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">")
                .append(current.getLastName()).append(", ").append(current.getFirstName()).append("</span><br><hr>");

        for (int i = 0; i < current.getNumberOfAccounts(); i++) {
            if (current.getAccount(i) instanceof CheckingAccount) {
                custInfo.append("&nbsp;<b>Acc Type: Checking</b><br>")
                        .append("&nbsp;<b>Balance: <span style=\"color:red;\">$")
                        .append(current.getAccount(i).getBalance()).append("</span></b><br><br>");
            } else if (current.getAccount(i) instanceof SavingsAccount) {
                custInfo.append("&nbsp;<b>Acc Type: Savings</b><br>")
                        .append("&nbsp;<b>Balance: <span style=\"color:red;\">$")
                        .append(current.getAccount(i).getBalance()).append("</span></b><br><br>");
            }
        }

        log.setText(custInfo.toString());
    }

    private void generateReport() {
        StringBuilder reportText = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">Report</span><br><hr>");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            Customer customer = Bank.getCustomer(i);
            reportText.append("<br>&nbsp;<b><span style=\"font-size:1.5em;\">")
                    .append(customer.getLastName()).append(", ").append(customer.getFirstName()).append("</span><br>");

            for (int j = 0; j < customer.getNumberOfAccounts(); j++) {
                String accountType = customer.getAccount(j) instanceof CheckingAccount ? "Checking" : "Savings";
                reportText.append("&nbsp;<b>Acc Type: ").append(accountType).append("</b><br>")
                        .append("&nbsp;<b>Balance: <span style=\"color:red;\">$")
                        .append(customer.getAccount(j).getBalance()).append("</span></b><br><br>");
            }
        }

        log.setText(reportText.toString());
    }

    private void loadCustomersFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] customerData = line.split("\\s+");
                if (customerData.length < 3) {
                    System.out.println("Invalid data format for customer: " + line);
                    continue;
                }
                String firstName = customerData[0];
                String lastName = customerData[1];
                int numAccounts = Integer.parseInt(customerData[2]);
                Bank.addCustomer(firstName, lastName);
                Customer customer = Bank.getCustomer(Bank.getNumberOfCustomers() - 1);

                for (int j = 0; j < numAccounts; j++) {
                    line = br.readLine();
                    if (line == null) {
                        System.out.println("Missing account data for customer: " + firstName + " " + lastName);
                        break;
                    }
                    String[] accountData = line.split("\\s+");
                    if (accountData.length < 3) {
                        System.out.println("Invalid data format for account: " + line);
                        continue;
                    }
                    String accountType = accountData[0];
                    double balance = Double.parseDouble(accountData[1]);
                    double parameter = Double.parseDouble(accountData[2]);

                    if (accountType.equals("S")) {
                        customer.addAccount(new SavingsAccount(balance, parameter));
                    } else if (accountType.equals("C")) {
                        customer.addAccount(new CheckingAccount(balance, parameter));
                    } else {
                        System.out.println("Invalid account type: " + accountType);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SWINGDemo demo = new SWINGDemo();
        demo.launchFrame();
    }
}
