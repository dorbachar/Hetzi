package com.example.hetzi_beta.Transactions;

public class Transaction {

    // TODO : fields of אמצעי תשלום etc. after payment is integrated into the system
    /*
    * The member 'title' will contain info about the name & quantity. For example if the Transaction
    * was buying 2 Salads the title will be: "2 x Salad"
    * */
    private String  title;
    private String  pay_uid;
    private String  receive_uid;
    private String  duration;
    private String  s_time;
    private int     sum;
    private int     discount;

    public String getPay_uid() {
        return pay_uid;
    }

    public void setPay_uid(String pay_uid) {
        this.pay_uid = pay_uid;
    }

    public String getReceive_uid() {
        return receive_uid;
    }

    public void setReceive_uid(String receive_uid) {
        this.receive_uid = receive_uid;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
