package com.contained.game.util;

public class Range<T extends Comparable<T>> {
	private T minValue;
	private T maxValue;
	
	public Range(T minValue, T maxValue) {
		if (minValue.compareTo(maxValue) <= 0) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		} else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	public Range(Range<T> copy) {
		this.minValue = copy.minValue;
		this.maxValue = copy.maxValue;
	}
	
	public T min() {
		return this.minValue;
	}
	
	public T max() {
		return this.maxValue;	
	}	
}
