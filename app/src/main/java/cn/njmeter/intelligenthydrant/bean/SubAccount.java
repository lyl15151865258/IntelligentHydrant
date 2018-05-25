package cn.njmeter.intelligenthydrant.bean;

/**
 * Created by LiYuliang on 2017/9/19 0019.
 * 子账号实体类
 */

public class SubAccount {

    private int loginId = 0;
    /**
     * 子账号数据表主键
     */
    private int child_Account_Id = 0;

    //************************************************************水表平台账号信息************************************************************
    /**
     * http端口号
     */
    private String http_Port_CS = "";
    /**
     * 密码
     */
    private String pass_Word_CS = "";
    /**
     * ip地址
     */
    private String server_Host_CS = "";
    /**
     * 项目名称
     */
    private String service_Name_CS = "";
    /**
     * socket端口
     */
    private String socket_Port_CS = "";
    /**
     * 用户名
     */
    private String user_Name_CS = "";

    //************************************************************消火栓平台************************************************************
    /**
     * http端口号
     */
    private String http_Port_XHS = "";
    /**
     * 密码
     */
    private String pass_Word_XHS = "";
    /**
     * ip地址
     */
    private String server_Host_XHS = "";
    /**
     * 项目名称
     */
    private String service_Name_XHS = "";
    /**
     * socket端口
     */
    private String socket_Port_XHS = "";
    /**
     * 用户名
     */
    private String user_Name_XHS = "";

    //************************************************************分区计量平台************************************************************
    /**
     * http端口号
     */
    private String http_Port_DMA = "";
    /**
     * 密码
     */
    private String pass_Word_DMA = "";
    /**
     * ip地址
     */
    private String server_Host_DMA = "";
    /**
     * 项目名称
     */
    private String service_Name_DMA = "";
    /**
     * socket端口
     */
    private String socket_Port_DMA = "";
    /**
     * 用户名
     */
    private String user_Name_DMA = "";

    //************************************************************机械表抄表************************************************************
    /**
     * http端口号
     */
    private String http_Port_CB = "";
    /**
     * 密码
     */
    private String pass_Word_CB = "";
    /**
     * ip地址
     */
    private String server_Host_CB = "";
    /**
     * 项目名称
     */
    private String service_Name_CB = "";
    /**
     * socket端口
     */
    private String socket_Port_CB = "";
    /**
     * 用户名
     */
    private String user_Name_CB = "";

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public int getChild_Account_Id() {
        return child_Account_Id;
    }

    public void setChild_Account_Id(int child_Account_Id) {
        this.child_Account_Id = child_Account_Id;
    }

    public String getHttp_Port_CS() {
        return http_Port_CS;
    }

    public void setHttp_Port_CS(String http_Port_CS) {
        this.http_Port_CS = http_Port_CS;
    }

    public String getPass_Word_CS() {
        return pass_Word_CS;
    }

    public void setPass_Word_CS(String pass_Word_CS) {
        this.pass_Word_CS = pass_Word_CS;
    }

    public String getServer_Host_CS() {
        return server_Host_CS;
    }

    public void setServer_Host_CS(String server_Host_CS) {
        this.server_Host_CS = server_Host_CS;
    }

    public String getService_Name_CS() {
        return service_Name_CS;
    }

    public void setService_Name_CS(String service_Name_CS) {
        this.service_Name_CS = service_Name_CS;
    }

    public String getSocket_Port_CS() {
        return socket_Port_CS;
    }

    public void setSocket_Port_CS(String socket_Port_CS) {
        this.socket_Port_CS = socket_Port_CS;
    }

    public String getUser_Name_CS() {
        return user_Name_CS;
    }

    public void setUser_Name_CS(String user_Name_CS) {
        this.user_Name_CS = user_Name_CS;
    }

    public String getHttp_Port_XHS() {
        return http_Port_XHS;
    }

    public void setHttp_Port_XHS(String http_Port_XHS) {
        this.http_Port_XHS = http_Port_XHS;
    }

    public String getPass_Word_XHS() {
        return pass_Word_XHS;
    }

    public void setPass_Word_XHS(String pass_Word_XHS) {
        this.pass_Word_XHS = pass_Word_XHS;
    }

    public String getServer_Host_XHS() {
        return server_Host_XHS;
    }

    public void setServer_Host_XHS(String server_Host_XHS) {
        this.server_Host_XHS = server_Host_XHS;
    }

    public String getService_Name_XHS() {
        return service_Name_XHS;
    }

    public void setService_Name_XHS(String service_Name_XHS) {
        this.service_Name_XHS = service_Name_XHS;
    }

    public String getSocket_Port_XHS() {
        return socket_Port_XHS;
    }

    public void setSocket_Port_XHS(String socket_Port_XHS) {
        this.socket_Port_XHS = socket_Port_XHS;
    }

    public String getUser_Name_XHS() {
        return user_Name_XHS;
    }

    public void setUser_Name_XHS(String user_Name_XHS) {
        this.user_Name_XHS = user_Name_XHS;
    }

    public String getHttp_Port_DMA() {
        return http_Port_DMA;
    }

    public void setHttp_Port_DMA(String http_Port_DMA) {
        this.http_Port_DMA = http_Port_DMA;
    }

    public String getPass_Word_DMA() {
        return pass_Word_DMA;
    }

    public void setPass_Word_DMA(String pass_Word_DMA) {
        this.pass_Word_DMA = pass_Word_DMA;
    }

    public String getServer_Host_DMA() {
        return server_Host_DMA;
    }

    public void setServer_Host_DMA(String server_Host_DMA) {
        this.server_Host_DMA = server_Host_DMA;
    }

    public String getService_Name_DMA() {
        return service_Name_DMA;
    }

    public void setService_Name_DMA(String service_Name_DMA) {
        this.service_Name_DMA = service_Name_DMA;
    }

    public String getSocket_Port_DMA() {
        return socket_Port_DMA;
    }

    public void setSocket_Port_DMA(String socket_Port_DMA) {
        this.socket_Port_DMA = socket_Port_DMA;
    }

    public String getUser_Name_DMA() {
        return user_Name_DMA;
    }

    public void setUser_Name_DMA(String user_Name_DMA) {
        this.user_Name_DMA = user_Name_DMA;
    }

    public String getHttp_Port_CB() {
        return http_Port_CB;
    }

    public void setHttp_Port_CB(String http_Port_CB) {
        this.http_Port_CB = http_Port_CB;
    }

    public String getPass_Word_CB() {
        return pass_Word_CB;
    }

    public void setPass_Word_CB(String pass_Word_CB) {
        this.pass_Word_CB = pass_Word_CB;
    }

    public String getServer_Host_CB() {
        return server_Host_CB;
    }

    public void setServer_Host_CB(String server_Host_CB) {
        this.server_Host_CB = server_Host_CB;
    }

    public String getService_Name_CB() {
        return service_Name_CB;
    }

    public void setService_Name_CB(String service_Name_CB) {
        this.service_Name_CB = service_Name_CB;
    }

    public String getSocket_Port_CB() {
        return socket_Port_CB;
    }

    public void setSocket_Port_CB(String socket_Port_CB) {
        this.socket_Port_CB = socket_Port_CB;
    }

    public String getUser_Name_CB() {
        return user_Name_CB;
    }

    public void setUser_Name_CB(String user_Name_CB) {
        this.user_Name_CB = user_Name_CB;
    }
}
