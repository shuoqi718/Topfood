package edu.monash.topfood.models;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {
    public int compare(Order left, Order right){
        return right.getTime().compareTo(left.getTime());
    }
}
