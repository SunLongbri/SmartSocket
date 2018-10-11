package com.shoneworn.smartplug.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/1.
 */

public class HomeTitleBean implements Serializable{

    private static final HomeTitleBean bean = new HomeTitleBean();

    public static HomeTitleBean getBean(){
        return bean;
    }

    private int on=0 ;

    private int err=0;

    private int off=0 ;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private int total=on+off;

    public void reset(){
        this.on=0;
        this.off=0;
        this.total =0;
        this.err=0;
    }

    public void addon(){
        on++;
        off --;
    }

    public void addoff(){
        off++;
        on--;
    }

    public int getOn() {
        return on;
    }

    public void setOn(int on) {
        this.on = on;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public int getOff() {
        return off;
    }

    public void setOff(int off) {
        this.off = off;
    }
}
