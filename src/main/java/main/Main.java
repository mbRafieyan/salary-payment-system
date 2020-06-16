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

    public static void main(String[] args) throws Exception {

        int fileRowCount = 100;
        long deptorDepositAmount = 1000000;

        try {

            Path salaryPaymentFolderPath = Paths.get("..\\..\\SalaryPayment");
            Files.createDirectories(salaryPaymentFolderPath);

            SalaryPaymentServer salaryPaymentServer = new SalaryPaymentServer();
            salaryPaymentServer.createFile(deptorDepositAmount, fileRowCount);

            salaryPaymentServer.salaryPaymentThreadsExecuter(deptorDepositAmount);

            System.out.println("***** FINISHED *****");

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException();

        } catch (LackSufficientBalanceException e) {
            System.err.print(e);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception();
        }
    }
}
