package service;

import dto.BalanceDto;
import dto.PaymentDto;
import exception.LackSufficientBalanceException;
import util.DepositTypeEnum;
import util.FileTypeEnum;
import util.ReadAndWriteFile;
import util.SalaryPaymentTheared;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SalaryPaymentServer {

    private final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

    public void salaryPaymentThreadsExecuter(long deptorDepositAmount) throws LackSufficientBalanceException, IOException {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));

        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        long paymentRowCount = Files.lines(paymentPath).count();
        long sumPaymentDto = calculateSumPayments(paymentPath);

        ReadAndWriteFile readAndWriteFile = new ReadAndWriteFile();
        SalaryPaymentServer salaryPaymentServer = new SalaryPaymentServer();

        if (deptorDepositAmount > readAndWriteFile.paymentSum) {

            for (int i = 0; i < paymentRowCount - 1; i++) {

                Runnable task = new SalaryPaymentTheared(salaryPaymentServer, readAndWriteFile, sumPaymentDto, i);
                threadPool.execute(task);
            }
        } else {
            throw new LackSufficientBalanceException("Balance Not Sufficient Exception");
        }
    }

    public void createFile(long deptorDepositAmount, int rowCount) throws Exception {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));

        ReadAndWriteFile paymentFile = new ReadAndWriteFile();
        paymentFile.timeCreationFileWriter(paymentPath, deptorDepositAmount, rowCount);

        if (!Files.exists(balancePath) || Files.lines(balancePath).count() == 0) {
            ReadAndWriteFile balanceFile = new ReadAndWriteFile();
            balanceFile.timeCreationFileWriter(balancePath, deptorDepositAmount, rowCount);
        }
    }

    public synchronized PaymentDto deptorFinder(ReadAndWriteFile readAndWriteFile, Path paymentPath) throws IOException {

        List<PaymentDto> paymentDtos = readAndWriteFile.paymentFileReader(paymentPath);
        List<PaymentDto> paymentList = paymentDtos.stream().filter(s -> s.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())).collect(Collectors.toList());

        return paymentList.get(0);
    }

    public synchronized BalanceDto balanceFinder(Path balancePath, PaymentDto payment) throws IOException {

        ReadAndWriteFile readAndWriteFile = new ReadAndWriteFile();

        List<BalanceDto> balances = readAndWriteFile.balanceFileReader(balancePath);
        List<BalanceDto> balancelist = balances.stream().filter(s -> s.getDepositNumber().equals(payment.getDepositNumber())).collect(Collectors.toList());

        if (balancelist.size() > 0) {
            BalanceDto balanceDto = new BalanceDto();
            balanceDto = balancelist.get(0);
            long newBalanceAmount = payment.getDepositAmount() + balanceDto.getDepositAmount();
            balanceDto.setDepositAmount(newBalanceAmount);
            return balanceDto;
        }
        return null;
    }

    private long calculateSumPayments(Path paymentDtoPath) throws IOException {

        Stream<String> paymentLines = Files.lines(paymentDtoPath);
        long sum = 0;
        for (String paymentStr : paymentLines.collect(Collectors.toList())) {
            if (!paymentStr.startsWith(DepositTypeEnum.DEBTOR.getDepositType())) {
                String[] paymentArray = paymentStr.split(" ");
                long paymentAmount = Long.valueOf(paymentArray[2]);
                sum += paymentAmount;
            }
        }
        paymentLines.close();
        return sum;
    }
}

