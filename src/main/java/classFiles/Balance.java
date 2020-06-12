package classFiles;

public class Balance {

    private String depositNumber;
    private long depositAmount;

    public Balance() { }

    public Balance(String depositNumber, long depositAmount) {
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
