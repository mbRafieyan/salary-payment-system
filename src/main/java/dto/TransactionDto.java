package dto;

public class TransactionDto {

    private String debtorDepositNumber;
    private String creditorDepositNumber;
    private long transactionAmount;

    public TransactionDto(String debtorDepositNumber, String creditorDepositNumber, long transactionAmount) {
        this.debtorDepositNumber = debtorDepositNumber;
        this.creditorDepositNumber = creditorDepositNumber;
        this.transactionAmount = transactionAmount;
    }

    public void setDebtorDepositNumber(String debtorDepositNumber) {
        this.debtorDepositNumber = debtorDepositNumber;
    }

    public void setCreditorDepositNumber(String creditorDepositNumber) {
        this.creditorDepositNumber = creditorDepositNumber;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getDebtorDepositNumber() {
        return debtorDepositNumber;
    }

    public String getCreditorDepositNumber() {
        return creditorDepositNumber;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    @Override
    public String toString() {
        return debtorDepositNumber + " " + creditorDepositNumber + " " + transactionAmount + System.lineSeparator();
    }
}
