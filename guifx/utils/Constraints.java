package guifx.utils;

import java.util.function.Predicate;

public class Constraints {
	private static final double eps = 0.01d;
	
	public static class UpperBound implements Predicate<Double> {
		private final double max;
		
		public UpperBound(double max) {
			this.max = max;
		}
		
		@Override
		public boolean test(Double d) {
			return d <= max + eps;
		}
	}
	
	public static class LowerBound implements Predicate<Double> {
		private final double min;
		
		public LowerBound(int coordinate, double min) {
			this.min = min;
		}
		
		@Override
		public boolean test(Double d) {
			return d >= min - eps;
		}
	}
	
	public static class Boundaries implements Predicate<Double> {
		private final double min, max;
		
		public Boundaries(double min, double max) {
			this.min = min;
			this.max = max;
		}
		
		@Override
		public boolean test(Double d) {
			return d >= min - eps && d <= max + eps;
		}
	}
}
