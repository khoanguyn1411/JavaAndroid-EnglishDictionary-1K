package com.duanthivien1k.model;

public class TuCuaBan {
    private  String tuCuaBan;
    private int thich;
    private String id;

    public TuCuaBan(String tuCuaBan, int thich, String id) {
        this.tuCuaBan = tuCuaBan;
        this.thich = thich;
        this.id = id;
    }

    public String getTuCuaBan() {
        return tuCuaBan;
    }

    public void setTuCuaBan(String tuCuaBan) {
        this.tuCuaBan = tuCuaBan;
    }

    public int getThich() {
        return thich;
    }

    public void setThich(int thich) {
        this.thich = thich;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
