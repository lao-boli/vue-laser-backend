package com.hquyyp.utils;

public class Point {
    private double x;
    private double y;


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof com.hquyyp.utils.Point)) return false;
        com.hquyyp.utils.Point other = (com.hquyyp.utils.Point) o;
        return !other.canEqual(this) ? false : ((Double.compare(getX(), other.getX()) != 0) ? false : (!(Double.compare(getY(), other.getY()) != 0)));
    }

    protected boolean canEqual(Object other) {
        return other instanceof com.hquyyp.utils.Point;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $x = Double.doubleToLongBits(getX());
        result = result * 59 + (int) ($x >>> 32L ^ $x);
        long $y = Double.doubleToLongBits(getY());
        return result * 59 + (int) ($y >>> 32L ^ $y);
    }

    public String toString() {
        return "Point(x=" + getX() + ", y=" + getY() + ")";
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}