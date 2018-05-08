package cn.njmeter.intelligenthydrant.bean;

import java.util.List;

/**
 * 消火栓末次数据（实时数据）
 *
 * @author LiYuliang
 * @version 1.0
 * Created at 2018/4/10 15:45
 */

public class HydrantLastData {

    private String result;
    private List<Data> data;
    private String msg;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public class Data {

        private String meter_id;
        private String hei_id;
        private String address;
        private int producttype_id;
        private int use_id;
        private double lng;
        private double lat;
        private String imei;
        private String create_time;
        private String create_time_realdata;
        private double amount;
        private double reverse_amount;
        private String valve_status;
        private double pressure;
        private double flowrate;
        private String volt;
        private String meter_status;
        private String status;
        private String status_1;
        private String method;
        private double user_id;
        private String app_id;
        private String icard;
        private double consumption;
        private String remark3;
        private String remark2;
        private String remark1;
        private String usingCreate_time;
        private String abnormalCreate_time;
        private int product_use_code;

        public String getMeter_id() {
            return meter_id;
        }

        public void setMeter_id(String meter_id) {
            this.meter_id = meter_id;
        }

        public String getHei_id() {
            return hei_id;
        }

        public void setHei_id(String hei_id) {
            this.hei_id = hei_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getProducttype_id() {
            return producttype_id;
        }

        public void setProducttype_id(int producttype_id) {
            this.producttype_id = producttype_id;
        }

        public int getUse_id() {
            return use_id;
        }

        public void setUse_id(int use_id) {
            this.use_id = use_id;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getCreate_time_realdata() {
            return create_time_realdata;
        }

        public void setCreate_time_realdata(String create_time_realdata) {
            this.create_time_realdata = create_time_realdata;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getReverse_amount() {
            return reverse_amount;
        }

        public void setReverse_amount(double reverse_amount) {
            this.reverse_amount = reverse_amount;
        }

        public String getValve_status() {
            return valve_status;
        }

        public void setValve_status(String valve_status) {
            this.valve_status = valve_status;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

        public double getFlowrate() {
            return flowrate;
        }

        public void setFlowrate(double flowrate) {
            this.flowrate = flowrate;
        }

        public String getVolt() {
            return volt;
        }

        public void setVolt(String volt) {
            this.volt = volt;
        }

        public String getMeter_status() {
            return meter_status;
        }

        public void setMeter_status(String meter_status) {
            this.meter_status = meter_status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus_1() {
            return status_1;
        }

        public void setStatus_1(String status_1) {
            this.status_1 = status_1;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public double getUser_id() {
            return user_id;
        }

        public void setUser_id(double user_id) {
            this.user_id = user_id;
        }

        public String getApp_id() {
            return app_id;
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public String getIcard() {
            return icard;
        }

        public void setIcard(String icard) {
            this.icard = icard;
        }

        public double getConsumption() {
            return consumption;
        }

        public void setConsumption(double consumption) {
            this.consumption = consumption;
        }

        public String getRemark3() {
            return remark3;
        }

        public void setRemark3(String remark3) {
            this.remark3 = remark3;
        }

        public String getRemark2() {
            return remark2;
        }

        public void setRemark2(String remark2) {
            this.remark2 = remark2;
        }

        public String getRemark1() {
            return remark1;
        }

        public void setRemark1(String remark1) {
            this.remark1 = remark1;
        }

        public String getUsingCreate_time() {
            return usingCreate_time;
        }

        public void setUsingCreate_time(String usingCreate_time) {
            this.usingCreate_time = usingCreate_time;
        }

        public String getAbnormalCreate_time() {
            return abnormalCreate_time;
        }

        public void setAbnormalCreate_time(String abnormalCreate_time) {
            this.abnormalCreate_time = abnormalCreate_time;
        }

        public int getProduct_use_code() {
            return product_use_code;
        }

        public void setProduct_use_code(int product_use_code) {
            this.product_use_code = product_use_code;
        }
    }

}
