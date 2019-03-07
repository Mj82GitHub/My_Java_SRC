import java.util.function.Function;

public class MyClass {
		
	public static void main(String[] args) {
		Function<Integer, Integer> factorial = (n) -> {
			int res = 1;
			
			for(int i = 1; i <= n; i++)
				res *= i;
			
			return res;
		};
		
		System.out.println("Factorial: " + factorial.apply(5));
	}
}
