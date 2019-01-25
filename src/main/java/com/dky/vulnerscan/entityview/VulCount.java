package com.dky.vulnerscan.entityview;

public class VulCount implements Comparable<VulCount> {
    private int allNum;
    private int extremeHigh;
    private int high;
    private int mid;
    private int low;

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

    public int getExtremeHigh() {
        return extremeHigh;
    }

    public void setExtremeHigh(int extremeHigh) {
        this.extremeHigh = extremeHigh;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    @Override
    public int compareTo(VulCount o) {

        return this.allNum - o.allNum;
    }
}
