package service;

import dto.BalanceDto;
import dto.PaymentDto;
import dto.TransactionDto;
import exception.LackSufficientBalanceException;
import util.DepositTypeEnum;
import util.FileTypeEnum;
import util.ReadAndWriteFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalaryPaymentServer {

    private final String baseDirectory = "..\\..\\SalaryPayment\\%s.txt";

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

    public String salaryPayment(long deptorDepositAmount) throws LackSufficientBalanceException, IOException {

        Path paymentPath = Paths.get(String.format(baseDirectory, FileTypeEnum.PAYMENT.getFileType()));
        Path balancePath = Paths.get(String.format(baseDirectory, FileTypeEnum.BALANCE.getFileType()));
        Path transactionPath = Paths.get(String.format(baseDirectory, FileTypeEnum.TRANSACTION.getFileType()));

        ReadAndWriteFile readAndWriteFile = new ReadAndWriteFile();

        if (deptorDepositAmount > readAndWriteFile.paymentSum) {

            List<PaymentDto> payments = readAndWriteFile.paymentFileReader(paymentPath);
            List<BalanceDto> balances = readAndWriteFile.balanceFileReader(balancePath);

            List<BalanceDto> newBalances = new ArrayList<>();
            List<TransactionDto> transactions = new ArrayList<>();

            PaymentDto debtor = deptorFinder(payments);

            for (PaymentDto payment : payments) {

                if (!payment.equals(debtor)) {

                    BalanceDto balanceDto = balanceFinder(balances, payment);

                    newBalances.add(balanceDto);

                    long debtorDipositAmount = debtor.getDepositAmount() - payment.getDepositAmount();
                    debtor.setDepositAmount(debtorDipositAmount);

                    TransactionDto transactionDto = new TransactionDto(debtor.getDepositNumber(), balanceDto.getDepositNumber(), payment.getDepositAmount());
                    transactions.add(transactionDto);
                }
            }

            BalanceDto balanceDto = new BalanceDto(debtor.getDepositNumber(), debtor.getDepositAmount());
            newBalances.add(balanceDto);

            readAndWriteFile.balanceFileWriter(balancePath, newBalances);
            readAndWriteFile.transactionFileWriter(transactionPath, transactions);

        } else {
            throw new LackSufficientBalanceException(" Balance Not Sufficient Exception ");
        }

        return " Payments were made successfully ";
    }

    private PaymentDto deptorFinder(List<PaymentDto> payments) {

        List<PaymentDto> paymentList = payments.stream().filter(s -> s.getDepositType().equals(DepositTypeEnum.DEBTOR.getDepositType())).collect(Collectors.toList());
        return paymentList.get(0);
    }

    private BalanceDto balanceFinder(List<BalanceDto> balances, PaymentDto payment) {

        List<BalanceDto> balancelist = balances.stream().filter(s -> s.getDepositNumber().equals(payment.getDepositNumber())).collect(Collectors.toList());
        BalanceDto balanceDto = new BalanceDto();

        if (balancelist.size() > 0) {
            balanceDto = balancelist.get(0);
            long newBalanceAmount = payment.getDepositAmount() + balanceDto.getDepositAmount();
            balanceDto.setDepositAmount(newBalanceAmount);
        } else {
            balanceDto.setDepositNumber(payment.getDepositNumber());
            balanceDto.setDepositAmount(payment.getDepositAmount());
        }
        return balanceDto;
    }
}
