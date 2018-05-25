package cn.njmeter.intelligenthydrant.loginregister.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 主账号登陆成功实体类
 * Created by LiYuliang on 2017/9/14 0014.
 *
 * @author LiYuliang
 * @version 2018/03/10
 */

public class ClientUser implements Serializable {

    /**
     * 登陆成功失败标记
     */
    private String result;
    /**
     * 登陆失败标记
     */
    private String msg;
    /**
     * 账号信息
     */
    private Account account;
    /**
     * 最新稳定版本对象
     */
    private Version2 version2;
    /**
     * 最新测试版本对象
     */
    private Version version;
    /**
     * 默认的服务器配置信息
     */
    private List<Server> server;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return this.version;
    }

    public Version2 getVersion2() {
        return version2;
    }

    public void setVersion2(Version2 version2) {
        this.version2 = version2;
    }

    public List<Server> getServer() {
        return server;
    }

    public void setServer(List<Server> server) {
        this.server = server;
    }

    public class Account implements Serializable {

        //**************************************************************  主账号信息  **************************************************************
        /**
         * 登录ID
         */
        private int loginId = 0;
        /**
         * 登录名
         */
        private String loginName = "";
        /**
         * 登录密码
         */
        private String password = "";
        /**
         * 昵称
         */
        private String nickName = "";
        /**
         * 主账号是管理员/用户（0：管理员，可以登陆电脑端，1：用户，只能登陆手机App）
         */
        private int lv = 0;
        /**
         * 头像图片路径
         */
        private String head_Portrait_URL = "";
        /**
         * 头像图片文件名
         */
        private String head_Portrait_Name = "";
        /**
         * 信息卡公司名
         */
        private String name_Company = "";
        /**
         * 信息卡职务
         */
        private String position_Company = "";
        /**
         * 是否接收正式版本更新（0：不接收；1：接收；默认接收，用户自己不可控制，由管理员控制）
         */
        private int stable_Update = 1;
        /**
         * 是否接收测试版本更新（0：不接收；1：接收；默认不接收，用户和管理员均可控制）
         */
        private int beta_Update = 0;


        //**************************************************************  子账号信息  **************************************************************
        /**
         * 子账号数据表主键
         */
        private int child_Account_Id = 0;

        //************************************  水表平台账号信息  ************************************
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

        //************************************  消火栓平台账号信息  ************************************
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

        //************************************  分区计量平台账号信息  ************************************
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

        //************************************  机械表抄表平台账号信息  ************************************
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

        //************************************  实名认证与蓝牙授权信息  ************************************
        /**
         * 实名与蓝牙认证数据表主键
         */
        private int cer_Id = 0;
        /**
         * 用户初次提交时间（创建时间）
         */
        private String createTime = "";
        /**
         * 用户最新一次修改时间
         */
        private String updateTime = "";
        /**
         * 真实姓名
         */
        private String real_Name = "";
        /**
         * 职务（或单位部门）
         */
        private String position = "";
        /**
         * 公司信息
         */
        private String company_Name = "";
        /**
         * 蓝牙是否被授权
         */
        private int bluetooth_Lv = 0;
        /**
         * 操作的管理员姓名
         */
        private String operator = "";
        /**
         * 操作日志
         */
        private String operate_Log = "";
        /**
         * 实名是否已认证
         */
        private int operate_Flag = 0;
        /**
         * 管理员最新操作时间
         */
        private String operate_Time = "";
        /**
         * 操作的管理员ID
         */
        private int operate_LoginId = 0;

        public int getLoginId() {
            return loginId;
        }

        public void setLoginId(int loginId) {
            this.loginId = loginId;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getLv() {
            return lv;
        }

        public void setLv(int lv) {
            this.lv = lv;
        }

        public String getHead_Portrait_URL() {
            return head_Portrait_URL;
        }

        public void setHead_Portrait_URL(String head_Portrait_URL) {
            this.head_Portrait_URL = head_Portrait_URL;
        }

        public String getHead_Portrait_Name() {
            return head_Portrait_Name;
        }

        public void setHead_Portrait_Name(String head_Portrait_Name) {
            this.head_Portrait_Name = head_Portrait_Name;
        }

        public String getName_Company() {
            return name_Company;
        }

        public void setName_Company(String name_Company) {
            this.name_Company = name_Company;
        }

        public String getPosition_Company() {
            return position_Company;
        }

        public void setPosition_Company(String position_Company) {
            this.position_Company = position_Company;
        }

        public int getStable_Update() {
            return stable_Update;
        }

        public void setStable_Update(int stable_Update) {
            this.stable_Update = stable_Update;
        }

        public int getBeta_Update() {
            return beta_Update;
        }

        public void setBeta_Update(int beta_Update) {
            this.beta_Update = beta_Update;
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

        public int getCer_Id() {
            return cer_Id;
        }

        public void setCer_Id(int cer_Id) {
            this.cer_Id = cer_Id;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getReal_Name() {
            return real_Name;
        }

        public void setReal_Name(String real_Name) {
            this.real_Name = real_Name;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getCompany_Name() {
            return company_Name;
        }

        public void setCompany_Name(String company_Name) {
            this.company_Name = company_Name;
        }

        public int getBluetooth_Lv() {
            return bluetooth_Lv;
        }

        public void setBluetooth_Lv(int bluetooth_Lv) {
            this.bluetooth_Lv = bluetooth_Lv;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getOperate_Log() {
            return operate_Log;
        }

        public void setOperate_Log(String operate_Log) {
            this.operate_Log = operate_Log;
        }

        public int getOperate_Flag() {
            return operate_Flag;
        }

        public void setOperate_Flag(int operate_Flag) {
            this.operate_Flag = operate_Flag;
        }

        public String getOperate_Time() {
            return operate_Time;
        }

        public void setOperate_Time(String operate_Time) {
            this.operate_Time = operate_Time;
        }

        public int getOperate_LoginId() {
            return operate_LoginId;
        }

        public void setOperate_LoginId(int operate_LoginId) {
            this.operate_LoginId = operate_LoginId;
        }
    }

    /**
     * 稳定版本
     */
    public class Version {

        private String createTime;
        /**
         * 软件ID（后期可能有更多软件）
         */
        private int apkTypeId;
        /**
         * 新版本文件MD5值
         */
        private String md5Value;
        /**
         * 新版本号
         */
        private int versionCode;

        private int versionCount;

        /**
         * 版本类型（beta:预览版；stable:正式版）
         */
        private String versionType;

        /**
         * 新版本文件名
         */
        private String versionFileName;

        private String versionFileUrl;

        private int versionId;

        /**
         * 新版本更新日志
         */
        private String versionLog;
        /**
         * 新版本名
         */
        private String versionName;

        private int versionSize;
        /**
         * 新版本下载地址
         */
        private String versionUrl;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setApkTypeId(int apkTypeId) {
            this.apkTypeId = apkTypeId;
        }

        public int getApkTypeId() {
            return this.apkTypeId;
        }

        public void setMd5Value(String md5Value) {
            this.md5Value = md5Value;
        }

        public String getMd5Value() {
            return this.md5Value;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getVersionCode() {
            return this.versionCode;
        }

        public void setVersionCount(int versionCount) {
            this.versionCount = versionCount;
        }

        public int getVersionCount() {
            return this.versionCount;
        }

        public String getVersionType() {
            return versionType;
        }

        public void setVersionType(String versionType) {
            this.versionType = versionType;
        }

        public void setVersionFileName(String versionFileName) {
            this.versionFileName = versionFileName;
        }

        public String getVersionFileName() {
            return this.versionFileName;
        }

        public void setVersionFileUrl(String versionFileUrl) {
            this.versionFileUrl = versionFileUrl;
        }

        public String getVersionFileUrl() {
            return this.versionFileUrl;
        }

        public void setVersionId(int versionId) {
            this.versionId = versionId;
        }

        public int getVersionId() {
            return this.versionId;
        }

        public void setVersionLog(String versionLog) {
            this.versionLog = versionLog;
        }

        public String getVersionLog() {
            return this.versionLog;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVersionName() {
            return this.versionName;
        }

        public void setVersionSize(int versionSize) {
            this.versionSize = versionSize;
        }

        public int getVersionSize() {
            return this.versionSize;
        }

        public void setVersionUrl(String versionUrl) {
            this.versionUrl = versionUrl;
        }

        public String getVersionUrl() {
            return this.versionUrl;
        }
    }

    /**
     * 测试版本
     */
    public class Version2 {
        private int apkTypeId;

        private String createTime;

        private String md5Value;

        private int versionCode;

        private int versionCount;

        private String versionFileName;

        private String versionFileUrl;

        private int versionId;

        private String versionLog;

        private String versionName;

        private int versionSize;

        private String versionType;

        private String versionUrl;

        public void setApkTypeId(int apkTypeId) {
            this.apkTypeId = apkTypeId;
        }

        public int getApkTypeId() {
            return this.apkTypeId;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateTime() {
            return this.createTime;
        }

        public void setMd5Value(String md5Value) {
            this.md5Value = md5Value;
        }

        public String getMd5Value() {
            return this.md5Value;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getVersionCode() {
            return this.versionCode;
        }

        public void setVersionCount(int versionCount) {
            this.versionCount = versionCount;
        }

        public int getVersionCount() {
            return this.versionCount;
        }

        public void setVersionFileName(String versionFileName) {
            this.versionFileName = versionFileName;
        }

        public String getVersionFileName() {
            return this.versionFileName;
        }

        public void setVersionFileUrl(String versionFileUrl) {
            this.versionFileUrl = versionFileUrl;
        }

        public String getVersionFileUrl() {
            return this.versionFileUrl;
        }

        public void setVersionId(int versionId) {
            this.versionId = versionId;
        }

        public int getVersionId() {
            return this.versionId;
        }

        public void setVersionLog(String versionLog) {
            this.versionLog = versionLog;
        }

        public String getVersionLog() {
            return this.versionLog;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVersionName() {
            return this.versionName;
        }

        public void setVersionSize(int versionSize) {
            this.versionSize = versionSize;
        }

        public int getVersionSize() {
            return this.versionSize;
        }

        public void setVersionType(String versionType) {
            this.versionType = versionType;
        }

        public String getVersionType() {
            return this.versionType;
        }

        public void setVersionUrl(String versionUrl) {
            this.versionUrl = versionUrl;
        }

        public String getVersionUrl() {
            return this.versionUrl;
        }

    }

    public class Server implements Serializable {

        private int serverConfigId;
        private String serverHost;
        private String serverName;
        private String serverProjectName;
        private String serverTcpPort;
        private String serverUdpPort;
        private String serverUrlPort;
        private String serverWebSocketPort;

        public int getServerConfigId() {
            return serverConfigId;
        }

        public void setServerConfigId(int serverConfigId) {
            this.serverConfigId = serverConfigId;
        }

        public String getServerHost() {
            return serverHost;
        }

        public void setServerHost(String serverHost) {
            this.serverHost = serverHost;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getServerProjectName() {
            return serverProjectName;
        }

        public void setServerProjectName(String serverProjectName) {
            this.serverProjectName = serverProjectName;
        }

        public String getServerTcpPort() {
            return serverTcpPort;
        }

        public void setServerTcpPort(String serverTcpPort) {
            this.serverTcpPort = serverTcpPort;
        }

        public String getServerUdpPort() {
            return serverUdpPort;
        }

        public void setServerUdpPort(String serverUdpPort) {
            this.serverUdpPort = serverUdpPort;
        }

        public String getServerUrlPort() {
            return serverUrlPort;
        }

        public void setServerUrlPort(String serverUrlPort) {
            this.serverUrlPort = serverUrlPort;
        }

        public String getServerWebSocketPort() {
            return serverWebSocketPort;
        }

        public void setServerWebSocketPort(String serverWebSocketPort) {
            this.serverWebSocketPort = serverWebSocketPort;
        }
    }
}
