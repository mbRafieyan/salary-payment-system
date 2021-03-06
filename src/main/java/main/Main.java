package main;

import exception.LackSufficientBalanceException;
import org.apache.log4j.Logger;
import service.SalaryPaymentServer;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        int fileRowCount = 1000;
        BigDecimal deptorDepositAmount = new BigDecimal(1000000000);

        try {

            Path salaryPaymentFolderPath = Paths.get("..\\..\\SalaryPayment");
            Files.createDirectories(salaryPaymentFolderPath);

            SalaryPaymentServer salaryPaymentServer = new SalaryPaymentServer();
            salaryPaymentServer.createFile(deptorDepositAmount, fileRowCount);

            CountDownLatch latch = salaryPaymentServer.salaryPaymentThreadsExecuter(deptorDepositAmount);

            System.out.println("************* PROCESSING *************");
            latch.await();
            System.out.println("********** FINISHED PROCESS **********");

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
            System.err.print(e.getMessage());
            throw new Exception();
        }
    }
}
