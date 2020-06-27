package service;

import dto.BalanceDto;
import dto.PaymentDto;
import exception.LackSufficientBalanceException;
import util.DepositTypeEnum;
import util.FileTypeEnum;
import util.ReadAndWriteFile;
import util.SalaryPaymentTheared;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SalaryPaymentServer {

    private final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

    public CountDownLatch salaryPaymentThreadsExecuter(BigDecimal deptorDepositAmount) throws LackSufficientBalanceException, IOException {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));

        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        ReadAndWriteFile readAndWriteFile = new ReadAndWriteFile();
        List<PaymentDto> paymentDtoList = readAndWriteFile.paymentFileReader(paymentPath);
        List<BalanceDto> balanceDtoList = readAndWriteFile.balanceFileReader(balancePath);

        Files.write(balancePath, " ".getBytes());

        int paymentRowCount = paymentDtoList.size();
        BigDecimal sumPaymentDto = calculateSumPayments(paymentDtoList);

        int taskRowCount = 100;
        int taskCount = (paymentRowCount / taskRowCount);
        taskCount++;

        CountDownLatch latch = new CountDownLatch(taskCount);

        if (deptorDepositAmount.compareTo(sumPaymentDto) == 1) {

            int startLine = 0;
            int endLine = 0;
            if (paymentDtoList.size() > 100) {
                endLine = 99;
            } else {
                endLine = paymentDtoList.size() - 1;
            }

            int lastIndex = endLine;

            for (int i = 0; i < taskCount; i++) {

                Runnable task = new SalaryPaymentTheared(readAndWriteFile, paymentDtoList, balanceDtoList, sumPaymentDto, latch, startLine, endLine);
                threadPool.execute(task);

                startLine = lastIndex + 1;
                if (paymentDtoList.size() > lastIndex + 100) {
                    endLine = startLine + 99;
                } else {
                    endLine = paymentDtoList.size() - 1;
                }
                lastIndex = endLine;
            }
            threadPool.shutdown();
        } else {
            throw new LackSufficientBalanceException("Balance Not Sufficient Exception");
        }

        return latch;
    }

    public void createFile(BigDecimal deptorDepositAmount, int rowCount) throws Exception {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));

        ReadAndWriteFile paymentFile = new ReadAndWriteFile();
        paymentFile.timeCreationFileWriter(paymentPath, deptorDepositAmount, rowCount);

        if (!Files.exists(balancePath) || Files.lines(balancePath).count() == 0) {
            ReadAndWriteFile balanceFile = new ReadAndWriteFile();
            balanceFile.timeCreationFileWriter(balancePath, deptorDepositAmount, rowCount);
        }
    }

    private BigDecimal calculateSumPayments(List<PaymentDto> paymentDtoList) {

        return paymentDtoList.stream().filter(s -> !s.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())).map(PaymentDto::getDepositAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

