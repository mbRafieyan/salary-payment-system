package util;

import classFiles.Balance;
import classFiles.Payment;
import classFiles.Transaction;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadAndWriteFile {

    public static long paymentSum = 0;

    public void timeCreationFileWriter(Path path, long deptorDepositAmount, int rowCount){

        try(BufferedWriter writer = Files.newBufferedWriter(path)){

            String text = "";

            if(path.toString().contains(FileTypeEnum.PAYMENT.getFileType())){
                 text = createPaymentFile(deptorDepositAmount, rowCount);
            } else{
                text = createBalanceFile(deptorDepositAmount, rowCount);
            }
            writer.write(text);

        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    private String createPaymentFile(long deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder paymentRow = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {

            int randomNum = ThreadLocalRandom.current().nextInt(100, 1000 + 1);

            String creditorDepositNumber = "1.20.100." + i;
            long creditorDepositAmount = randomNum;
            Payment payment = new Payment(creditorDepositNumber, creditorDepositAmount, DepositTypeEnum.CREDITOR.getDepositType());

            paymentRow.append(payment.toString());

            paymentSum += randomNum;
        }
        String debtorDepositNumber = "1.10.100.1";
        Payment payment = new Payment(debtorDepositNumber, deptorDepositAmount, DepositTypeEnum.DEBTOR.getDepositType());

        paymentRow.append(payment.toString());

        return paymentRow.toString();
    }

    private String createBalanceFile(long deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder balanceRow = new StringBuilder();

        for(int i=0; i<rowCount; i++) {

            String creatorDepositNumber = "1.20.100." + i;
            long creatorDepositAmount = 0;
            Balance balance = new Balance(creatorDepositNumber, creatorDepositAmount);

            balanceRow.append(balance.toString());
        }

        String depositNumber = "1.10.100.1";
        Balance balance = new Balance(depositNumber, deptorDepositAmount);

        balanceRow.append(balance.toString());

        return balanceRow.toString();
    }

    public static void transactionFileWriter(Path path, ArrayList<Transaction> transactions) throws IOException {

        StringBuffer sb = new StringBuffer();
        for(Transaction tr :transactions){
            sb.append(tr);
        }
        if (!Files.exists(path))
            Files.createFile(path);
        Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
    }

    public static void balanceFileWriter(Path path, ArrayList<Balance> balances) throws IOException {

        StringBuffer sb = new StringBuffer();
        for(Balance ba :balances){
            sb.append(ba);
        }
        if (!Files.exists(path))
            Files.createFile(path);
        Files.write(path, sb.toString().getBytes());
    }

    public static ArrayList<Payment> paymentFileReader(Path path) throws IOException {

        ArrayList<Payment> payments = new ArrayList<Payment>();

        Stream<String> fileStream = Files.lines(path);
        List<String> rowlist = fileStream.collect(Collectors.toList());

        for (String strPayment :rowlist){

            String[] paymentArray = strPayment.split(" ");

            String depositType = paymentArray[0];
            String depositNumber = paymentArray[1];
            long depositAmount = Long.parseLong(paymentArray[2]);

            Payment payment = new Payment(depositNumber, depositAmount, depositType);
            payments.add(payment);
        }

        return payments;
    }

    public static ArrayList<Balance> balanceFileReader(Path path) throws IOException {

        ArrayList<Balance> balances = new ArrayList<Balance>();

        Stream<String> fileStream = Files.lines(path);
        List<String> rowlist = fileStream.collect(Collectors.toList());

        for (String strBalance :rowlist){

            String[] balanceArray = strBalance.split(" ");

            String depositNumber = balanceArray[0];
            long depositAmount = Long.parseLong(balanceArray[1]);

            Balance balance = new Balance(depositNumber, depositAmount);
            balances.add(balance);
        }

        return balances;
    }

    public static final Logger logger = Logger.getLogger(ReadAndWriteFile.class);
}
