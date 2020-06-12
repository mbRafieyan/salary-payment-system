package classFiles;

public class Payment {

    private String depositNumber;
    private long depositAmount;
    private String depositType;

    public Payment(String depositNumber, long depositAmount, String depositType) {
        this.depositNumber = depositNumber;
        this.depositAmount = depositAmount;
        this.depositType = depositType;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public void setDepositAmount(long depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getDepositNumber() {
        return depositNumber;
    }

    public long getDepositAmount() {
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
