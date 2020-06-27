package dto;

import java.math.BigDecimal;

public class PaymentDto {

    private String depositNumber;
    private BigDecimal depositAmount;
    private String depositType;

    public PaymentDto() {
    }

    public PaymentDto(String depositNumber, BigDecimal depositAmount, String depositType) {
        this.depositNumber = depositNumber;
        this.depositAmount = depositAmount;
        this.depositType = depositType;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public String getDepositType() {
        return depositType;
    }

    @Override
    public String toString() {
        return depositType + " " + depositNumber + " " + depositAmount + System.lineSeparator();
    }
}
