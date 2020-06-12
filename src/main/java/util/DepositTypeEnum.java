package util;

public enum DepositTypeEnum {

    DEBTOR("debtor"),CREDITOR("creditor");

    private final String depositType;

    DepositTypeEnum(String depositType) {

        this.depositType = depositType;
    }

    public String getDepositType() {

        return this.depositType;
    }

    public static DepositTypeEnum fromValue(String type) {

        for (DepositTypeEnum depositType : DepositTypeEnum.values()) {
            if (depositType.getDepositType().equals(type)) {
                return depositType;
            }
        }
        return null;
    }
}
