package util;

import dto.BalanceDto;
import dto.PaymentDto;
import dto.TransactionDto;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadAndWriteFile {

    public final Logger logger = Logger.getLogger(ReadAndWriteFile.class);
    private final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

    public void timeCreationFileWriter(Path path, BigDecimal deptorDepositAmount, int rowCount) throws Exception {

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

    private String createPaymentFile(BigDecimal deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder paymentRow = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {

            int randomNum = ThreadLocalRandom.current().nextInt(100, 1000 + 1);

            String creditorDepositNumber = "1.20.100." + i;
            BigDecimal creditorDepositAmount = new BigDecimal(randomNum);
            PaymentDto payment = new PaymentDto(creditorDepositNumber, creditorDepositAmount, DepositTypeEnum.CREDITOR.getDepositType());

            paymentRow.append(payment.toString());
        }
        String debtorDepositNumber = "1.10.100.1";
        PaymentDto payment = new PaymentDto(debtorDepositNumber, deptorDepositAmount, DepositTypeEnum.DEBTOR.getDepositType());

        paymentRow.append(payment.toString());

        return paymentRow.toString();
    }

    private String createBalanceFile(BigDecimal deptorDepositAmount, int rowCount) throws IOException {

        StringBuilder balanceRow = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {

            String creatorDepositNumber = "1.20.100." + i;
            BigDecimal creatorDepositAmount = new BigDecimal(0);
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

            String[] paymentArray = strPayment.trim().split(" ");

            String depositType = paymentArray[0];
            String depositNumber = paymentArray[1];
            BigDecimal depositAmount = new BigDecimal(paymentArray[2]);

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
            if (!strBalance.equals(" ")) {

                String[] balanceArray = strBalance.trim().split(" ");
                String depositNumber = balanceArray[0];
                BigDecimal depositAmount = new BigDecimal(balanceArray[1]);

                BalanceDto balanceDto = new BalanceDto(depositNumber, depositAmount);
                balanceDtos.add(balanceDto);
            }
        }

        fileStream.close();
        return balanceDtos;
    }

    public synchronized void balanceWriter(List<PaymentDto> paymentDtos, List<BalanceDto> balanceDtoList, BigDecimal sumPaymentDto, int startLine, int endLine) throws IOException {

        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));

        List<BalanceDto> newBalanceDtoList = new ArrayList<>();

        for (int i = startLine; i <= endLine; i++) {

            PaymentDto paymentDto = paymentDtos.get(i);
            BalanceDto balanceDto = new BalanceDto();

            if (!paymentDto.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())) {

                List<BalanceDto> balanceDtos = balanceDtoList.stream().filter(s -> s.getDepositNumber().equals(paymentDto.getDepositNumber())).collect(Collectors.toList());
                balanceDto.setDepositNumber(paymentDto.getDepositNumber());

                if (balanceDtos.size() > 0) {
                    balanceDto.setDepositAmount(paymentDto.getDepositAmount().add(balanceDtos.get(0).getDepositAmount()));
                } else {
                    balanceDto.setDepositAmount(paymentDto.getDepositAmount());
                }

            } else {
                balanceDto.setDepositNumber(paymentDto.getDepositNumber());
                balanceDto.setDepositAmount(paymentDto.getDepositAmount().subtract(sumPaymentDto));
            }
            newBalanceDtoList.add(balanceDto);
        }
        for (BalanceDto balanceDto : newBalanceDtoList) {
            String balanceStr = balanceDto.getDepositNumber() + " " + balanceDto.getDepositAmount() + System.lineSeparator();
            Files.write(balancePath, balanceStr.getBytes(), StandardOpenOption.APPEND);
        }
    }

    public synchronized void transactionWriter(List<PaymentDto> paymentDtos, int startLine, int endLine) throws IOException {

        Path transactionPath = Paths.get(String.format(baseDirectory, FileTypeEnum.TRANSACTION.getFileType()));

        List<PaymentDto> balanceDtos = paymentDtos.stream().filter(s -> s.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())).collect(Collectors.toList());
        PaymentDto debtor = balanceDtos.get(0);

        List<TransactionDto> transactionDtoList = new ArrayList<>();

        for (int i = startLine; i <= endLine; i++) {

            PaymentDto paymentDto = paymentDtos.get(i);

            if (!paymentDto.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())) {

                TransactionDto transactionDto = new TransactionDto(debtor.getDepositNumber(), paymentDto.getDepositNumber(), paymentDto.getDepositAmount());
                transactionDtoList.add(transactionDto);
            }
        }

        if (Files.notExists(transactionPath)) {
            Files.createFile(transactionPath);
        }

        for (TransactionDto transactionDto : transactionDtoList) {
            Files.write(transactionPath, transactionDto.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }
}
