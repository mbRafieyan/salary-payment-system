package util;

import dto.BalanceDto;
import dto.PaymentDto;
import main.Main;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SalaryPaymentTheared implements Runnable {

    public static final Logger logger = Logger.getLogger(Main.class);

    private int endLine;
    private int startLine;
    private BigDecimal sumPaymentDto;
    private List<PaymentDto> paymentDtoList;
    private List<BalanceDto> balanceDtoList;
    private ReadAndWriteFile readAndWriteFile;
    private CountDownLatch latch;

    public SalaryPaymentTheared(ReadAndWriteFile readAndWriteFile, List<PaymentDto> paymentDtoList, List<BalanceDto> balanceDtoList, BigDecimal sumPaymentDto, CountDownLatch latch, int startLine, int endLine) {
        this.readAndWriteFile = readAndWriteFile;
        this.sumPaymentDto = sumPaymentDto;
        this.paymentDtoList = paymentDtoList;
        this.balanceDtoList = balanceDtoList;
        this.startLine = startLine;
        this.endLine = endLine;
        this.latch = latch;
    }

    @Override
    public void run() {

        try {

            readAndWriteFile.balanceWriter(paymentDtoList, balanceDtoList, sumPaymentDto, startLine, endLine);
            readAndWriteFile.transactionWriter(paymentDtoList, startLine, endLine);

            latch.countDown();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.err.print(e.getMessage());
        }
    }
}



