package util;

import dto.BalanceDto;
import dto.PaymentDto;
import dto.TransactionDto;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadAndWriteFile {

    public final Logger logger = Logger.getLogger(ReadAndWriteFile.class);
    public long paymentSum = 0;

    public void timeCreationFileWriter(Path path, long deptorDepositAmount, int rowCount) throws Exception {

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            String text = "";

            if (path.toString().contains(FileTypeEnum.PAYMENT.getFileType())) {
                text = createPaymentFile(deptorDepositAmount, rowCount);
            } else {
                text = createBalanceFile(deptorDepositAmount, rowCount);
            }
            writer.write(text);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception();
        }
    }

    private String createPaymentFile(long deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder paymentRow = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {

            int randomNum = ThreadLocalRandom.current().nextInt(100, 1000 + 1);

            String creditorDepositNumber = "1.20.100." + i;
            long creditorDepositAmount = randomNum;
            PaymentDto payment = new PaymentDto(creditorDepositNumber, creditorDepositAmount, DepositTypeEnum.CREDITOR.getDepositType());

            paymentRow.append(payment.toString());

            paymentSum += randomNum;
        }
        String debtorDepositNumber = "1.10.100.1";
        PaymentDto payment = new PaymentDto(debtorDepositNumber, deptorDepositAmount, DepositTypeEnum.DEBTOR.getDepositType());

        paymentRow.append(payment.toString());

        return paymentRow.toString();
    }

    private String createBalanceFile(long deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder balanceRow = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {

            String creatorDepositNumber = "1.20.100." + i;
            long creatorDepositAmount = 0;
            BalanceDto balanceDto = new BalanceDto(creatorDepositNumber, creatorDepositAmount);

            balanceRow.append(balanceDto.toString());
        }

        String depositNumber = "1.10.100.1";
        BalanceDto balanceDto = new BalanceDto(depositNumber, deptorDepositAmount);

        balanceRow.append(balanceDto.toString());

        return balanceRow.toString();
    }

    public void transactionFileWriter(Path path, List<TransactionDto> transactions) throws IOException {

        StringBuffer stringBuffer = new StringBuffer();
        for (TransactionDto transactionDto : transactions) {
            stringBuffer.append(transactionDto);
        }
        if (!Files.exists(path))
            Files.createFile(path);
        Files.write(path, stringBuffer.toString().getBytes(), StandardOpenOption.APPEND);
    }

    public void balanceFileWriter(Path path, List<BalanceDto> balances) throws IOException {

        StringBuffer stringBuffer = new StringBuffer();
        for (BalanceDto balanceDto : balances) {
            stringBuffer.append(balanceDto);
        }
        if (!Files.exists(path))
            Files.createFile(path);
        Files.write(path, stringBuffer.toString().getBytes());
    }

    public List<PaymentDto> paymentFileReader(Path path) throws IOException {

        List<PaymentDto> payments = new ArrayList<>();

        Stream<String> fileStream = Files.lines(path);
        List<String> rowlist = fileStream.collect(Collectors.toList());

        for (String strPayment : rowlist) {

            String[] paymentArray = strPayment.split(" ");

            String depositType = paymentArray[0];
            String depositNumber = paymentArray[1];
            long depositAmount = Long.parseLong(paymentArray[2]);

            PaymentDto payment = new PaymentDto(depositNumber, depositAmount, depositType);
            payments.add(payment);
        }

        return payments;
    }

    public List<BalanceDto> balanceFileReader(Path path) throws IOException {

        List<BalanceDto> balanceDtos = new ArrayList<>();

        Stream<String> fileStream = Files.lines(path);
        List<String> rowlist = fileStream.collect(Collectors.toList());

        for (String strBalance : rowlist) {

            String[] balanceArray = strBalance.split(" ");

            String depositNumber = balanceArray[0];
            long depositAmount = Long.parseLong(balanceArray[1]);

            BalanceDto balanceDto = new BalanceDto(depositNumber, depositAmount);
            balanceDtos.add(balanceDto);
        }

        return balanceDtos;
    }
}
