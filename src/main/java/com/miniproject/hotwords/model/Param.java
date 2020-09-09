package com.miniproject.hotwords.model;

import java.io.Serializable;

public class Param implements Serializable {
    private static final long serialVersionUID = 7209924910917761714L;
    String id;
    String param;
    String pvalue;
    String pdetail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getPvalue() {
        return pvalue;
    }

    public void setPvalue(String pvalue) {
        this.pvalue = pvalue;
    }

    public String getPdetail() {
        return pdetail;
    }

    public void setPdetail(String pdetail) {
        this.pdetail = pdetail;
    }
}
