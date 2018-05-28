package cn.njmeter.intelligenthydrant.network.bean;

public class SocketPackage {
    /**
     * 用户8位ID
     */
    private String userId;
    /**
     * 操作的设备类型
     */
    private int deviceType;
    /**
     * 是否是单表操作（true：单表操作    false：批量操作）
     */
    private int singleOpt;
    /**
     * 指令代码
     */
    private int cmdCode;
    /**
     * 指令原文
     */
    private String cmdContent;
    /**
     * 标记采用cmd还是cmdContent（true：采用cmd并转换成具体指令后发送    false：采用cmdContent，原文转发）
     */
    private boolean useCmdCode;
    /**
     * 表号
     */
    private String meterId;
    /**
     * IMEI号
     */
    private String imei;
    /**
     * 批量操作时层级名称
     */
    private String hierarchy;
    /**
     * 批量操作时层级ID
     */
    private int hierarchyId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getSingleOpt() {
        return singleOpt;
    }

    public void setSingleOpt(int singleOpt) {
        this.singleOpt = singleOpt;
    }

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getCmdContent() {
        return cmdContent;
    }

    public void setCmdContent(String cmdContent) {
        this.cmdContent = cmdContent;
    }

    public boolean isUseCmdCode() {
        return useCmdCode;
    }

    public void setUseCmdCode(boolean useCmdCode) {
        this.useCmdCode = useCmdCode;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public int getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(int hierarchyId) {
        this.hierarchyId = hierarchyId;
    }
}
