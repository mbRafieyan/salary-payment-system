package dto;

import java.math.BigDecimal;

public class BalanceDto {

    private String depositNumber;
    private BigDecimal depositAmount;

    public BalanceDto() {
    }

    public BalanceDto(String depositNumber, BigDecimal depositAmount) {
        this.depositNumber = depositNumber;
        this.depositAmount = depositAmount;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    @Override
    public String toString() {
        return depositNumber + " " + depositAmount + System.lineSeparator();
    }
}
