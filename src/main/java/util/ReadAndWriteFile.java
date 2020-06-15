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

    public synchronized List<PaymentDto> paymentFileReader(Path path) throws IOException {

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

        fileStream.close();
        return payments;
    }

    public synchronized List<BalanceDto> balanceFileReader(Path path) throws IOException {

        List<BalanceDto> balanceDtos = new ArrayList<>();

        Stream<String> fileStream = Files.lines(path);
        List<String> rowlist = fileStream.collect(Collectors.toList());

        for (String strBalance : rowlist) {
            if (!strBalance.isEmpty()) {

                String[] balanceArray = strBalance.split(" ");
                String depositNumber = balanceArray[0];
                long depositAmount = Long.parseLong(balanceArray[1]);

                BalanceDto balanceDto = new BalanceDto(depositNumber, depositAmount);
                balanceDtos.add(balanceDto);
            }
        }

        fileStream.close();
        return balanceDtos;
    }

    public synchronized PaymentDto paymentReader(Path paymentPath, long rowNumber) throws Exception {

        PaymentDto paymentDto = new PaymentDto();

        try (Stream<String> lines = Files.lines(paymentPath)) {

            String row = lines.skip(rowNumber).findFirst().get();
            paymentDto = paymentDtoConventor(row);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception();
        }

        return paymentDto;
    }

    public synchronized void balanceWriter(Path balanceDtoPath, Path paymentDtoPath, BalanceDto balanceDto, PaymentDto debtor, PaymentDto paymentDto, long sumPaymentDto) throws IOException {

        Stream<String> balanceLines = Files.lines(balanceDtoPath);
        List<String> balanceList = balanceLines.map(s -> s.contains(paymentDto.getDepositNumber()) ? balanceDto.toString() : s).collect(Collectors.toList());

        StringBuffer stringBuffer = new StringBuffer();
        String deptorStr = "";
        long debtorDipositAmount = debtor.getDepositAmount() - sumPaymentDto;

        for (String balanceDtoStr : balanceList) {
            if (!balanceDtoStr.isEmpty()) {
                if (balanceDtoStr.contains(debtor.getDepositNumber())) {
                    deptorStr = debtor.getDepositNumber() + " " + debtorDipositAmount + System.lineSeparator();
                } else {
                    stringBuffer.append(balanceDtoStr + System.lineSeparator());
                }
            }
        }

        if (balanceDto == null) {
            stringBuffer.append(paymentDto.getDepositNumber() + " " + paymentDto.getDepositAmount() + System.lineSeparator());
        }
        stringBuffer.append(deptorStr);
        balanceLines.close();
        Files.write(balanceDtoPath, stringBuffer.toString().getBytes());
    }

    public synchronized void transactionWriter(Path transactionPath, PaymentDto debtor, BalanceDto balanceDto, PaymentDto paymentDto) throws IOException {

        TransactionDto transactionDto = new TransactionDto(debtor.getDepositNumber(), balanceDto != null ? balanceDto.getDepositNumber() : paymentDto.getDepositNumber(), paymentDto.getDepositAmount());

        if (Files.notExists(transactionPath)) {
            Files.createFile(transactionPath);
        }

        Files.write(transactionPath, transactionDto.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private PaymentDto paymentDtoConventor(String rowText) {

        String[] paymentArray = rowText.split(" ");

        String depositType = paymentArray[0];
        String depositNumber = paymentArray[1];
        long depositAmount = Long.parseLong(paymentArray[2]);

        PaymentDto paymentDto = new PaymentDto(depositNumber, depositAmount, depositType);
        return paymentDto;
    }
}
