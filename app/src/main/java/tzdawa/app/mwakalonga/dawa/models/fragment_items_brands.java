package tzdawa.app.mwakalonga.dawa.models;

public class fragment_items_brands {
    private String mtoken;
    private String mname;
    private String mprice;
    private String mimage;

    public fragment_items_brands(String mtoken, String mname, String mprice, String mimage) {
        this.mtoken = mtoken;
        this.mname = mname;
        this.mprice = mprice;
        this.mimage = mimage;
    }

    public String getMtoken() {
        return mtoken;
    }

    public String getMname() {
        return mname;
    }

    public String getMprice() {
        return mprice;
    }

    public String getMimage() {
        return mimage;
    }
}
