package main;

import classFiles.Balance;
import classFiles.Payment;
import classFiles.Transaction;
import org.apache.log4j.Logger;
import util.DepositTypeEnum;
import util.ReadAndWriteFile;
import util.FileTypeEnum;
import util.LackSufficientBalanceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

    public static void main(String[] args){

        int fileRowCount = 5;
        long deptorDepositAmount = 10000;

        try {

            Path SalaryPaymentFolderPath = Paths.get("..\\..\\SalaryPayment");
            Files.createDirectories(SalaryPaymentFolderPath);

            createFile(deptorDepositAmount, fileRowCount);

            String message = salaryPayment(deptorDepositAmount);

            System.out.println(message);

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } catch (LackSufficientBalanceException e) {
            System.err.print(e);
        }
    }
    private static void createFile(long deptorDepositAmount, int rowCount) throws IOException {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));

        ReadAndWriteFile paymentFile = new ReadAndWriteFile();
        paymentFile.timeCreationFileWriter(paymentPath, deptorDepositAmount, rowCount);

        if(!Files.exists(balancePath) || Files.lines(balancePath).count() == 0) {
            ReadAndWriteFile balanceFile = new ReadAndWriteFile();
            balanceFile.timeCreationFileWriter(balancePath, deptorDepositAmount, rowCount);
        }
    }

    private static String salaryPayment(long deptorDepositAmount) throws LackSufficientBalanceException {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));
        Path transactionPath = Paths.get(String.format(baseDirectory, FileTypeEnum.TRANSACTION.getFileType()));

        if(deptorDepositAmount > ReadAndWriteFile.paymentSum) {

            try {
                ArrayList<Payment> payments = ReadAndWriteFile.paymentFileReader(paymentPath);
                ArrayList<Balance> balances = ReadAndWriteFile.balanceFileReader(balancePath);

                ArrayList<Balance> newBalances = new ArrayList<Balance>();
                ArrayList<Transaction> transactions = new ArrayList<Transaction>();

                Payment debtor = deptorFinder(payments);

                for(Payment payment :payments){

                    if(!payment.equals(debtor)){

                        Balance balance= balanceFinder(balances, payment);

                        newBalances.add(balance);

                        long debtorDipositAmount = debtor.getDepositAmount() - payment.getDepositAmount();
                        debtor.setDepositAmount(debtorDipositAmount);

                        Transaction transaction = new Transaction(debtor.getDepositNumber(), balance.getDepositNumber(), payment.getDepositAmount());
                        transactions.add(transaction);
                    }
                }

                Balance balance = new Balance(debtor.getDepositNumber(), debtor.getDepositAmount());
                newBalances.add(balance);

                ReadAndWriteFile.balanceFileWriter(balancePath, newBalances);
                ReadAndWriteFile.transactionFileWriter(transactionPath, transactions);

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        } else {
            throw new LackSufficientBalanceException(" Balance Not Sufficient Exception ");
        }

        return " Payments were made successfully ";
    }

    private static Payment deptorFinder(ArrayList<Payment> payments){

        List<Payment> paymentList = payments.stream().filter(s -> s.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())).collect(Collectors.toList());
        return paymentList.get(0);
    }

    private static Balance balanceFinder(List<Balance> balances, Payment payment){

        List<Balance> balancelist =  balances.stream().filter(s -> s.getDepositNumber().equals(payment.getDepositNumber())).collect(Collectors.toList());
        Balance balance = new Balance();

        if(balancelist.size()>0) {
            balance = balancelist.get(0);
            long newBalanceAmount = payment.getDepositAmount() + balance.getDepositAmount();
            balance.setDepositAmount(newBalanceAmount);
        }else {
            balance.setDepositNumber(payment.getDepositNumber());
            balance.setDepositAmount(payment.getDepositAmount());
        }
        return balance;
    }

    public static final Logger logger = Logger.getLogger(Main.class);
}
