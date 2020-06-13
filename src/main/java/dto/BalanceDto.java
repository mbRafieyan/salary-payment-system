package dto;

public class BalanceDto {

    private String depositNumber;
    private long depositAmount;

    public BalanceDto() {
    }

    public BalanceDto(String depositNumber, long depositAmount) {
        this.depositNumber = depositNumber;
        this.depositAmount = depositAmount;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public void setDepositAmount(long depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public long getDepositAmount() {
        return depositAmount;
    }

    @Override
    public String toString() {
        return depositNumber + " " + depositAmount + System.lineSeparator();
    }
}
