package main;

import exception.LackSufficientBalanceException;
import org.apache.log4j.Logger;
import service.SalaryPaymentServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        int fileRowCount = 5;
        long deptorDepositAmount = 10000;

        try {

            Path salaryPaymentFolderPath = Paths.get("..\\..\\SalaryPayment");
            Files.createDirectories(salaryPaymentFolderPath);

            SalaryPaymentServer salaryPaymentServer = new SalaryPaymentServer();
            salaryPaymentServer.createFile(deptorDepositAmount, fileRowCount);

            String message = salaryPaymentServer.salaryPayment(deptorDepositAmount);

            System.out.println(message);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        } catch (LackSufficientBalanceException e) {
            System.err.print(e);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
