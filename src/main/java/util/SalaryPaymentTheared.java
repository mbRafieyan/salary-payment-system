package util;

import dto.BalanceDto;
import dto.PaymentDto;
import main.Main;
import org.apache.log4j.Logger;
import service.SalaryPaymentServer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SalaryPaymentTheared implements Runnable {

    public static final Logger logger = Logger.getLogger(Main.class);

    private final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

    private SalaryPaymentServer salaryPaymentServer;
    private ReadAndWriteFile readAndWriteFile;
    private long lineNumber;
    private long sumPaymentDto;

    public SalaryPaymentTheared(SalaryPaymentServer salaryPaymentServer, ReadAndWriteFile readAndWriteFile, long sumPaymentDto, long lineNumber) {
        this.salaryPaymentServer = salaryPaymentServer;
        this.readAndWriteFile = readAndWriteFile;
        this.sumPaymentDto = sumPaymentDto;
        this.lineNumber = lineNumber;
    }

    @Override
    public void run() {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));
        Path transactionPath = Paths.get(String.format(baseDirectory, FileTypeEnum.TRANSACTION.getFileType()));

        try {
            PaymentDto paymentDto = readAndWriteFile.paymentReader(paymentPath, lineNumber);

            if (!paymentDto.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())) {

                BalanceDto balanceDto = salaryPaymentServer.balanceFinder(balancePath, paymentDto);

                PaymentDto debtor = salaryPaymentServer.deptorFinder(readAndWriteFile, paymentPath);

                readAndWriteFile.balanceWriter(balancePath, paymentPath, balanceDto, debtor, paymentDto, sumPaymentDto);

                readAndWriteFile.transactionWriter(transactionPath, debtor, balanceDto, paymentDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}



