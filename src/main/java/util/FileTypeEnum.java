package util;

public enum FileTypeEnum {

    PAYMENT("payment"),
    BALANCE("balance"),
    TRANSACTION("transaction");

    private final String fileType;

    FileTypeEnum(String fileType) {

        this.fileType = fileType;
    }

    public String getFileType() {

        return this.fileType;
    }

    public static FileTypeEnum fromValue(String type) {

        for (FileTypeEnum fileType : FileTypeEnum.values()) {
            if (fileType.getFileType().equals(type)) {
                return fileType;
            }
        }
        return null;
    }
}
