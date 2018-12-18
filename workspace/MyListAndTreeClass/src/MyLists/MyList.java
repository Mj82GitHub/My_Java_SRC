package MyLists;
import MyLists.BraidedSearchTree;
import MyLists.CompareFunc;
import MyLists.Edge;
import MyLists.RandomizedSearchTree;
import MyLists.SearchTree;


public class MyList {
	public static double curx; // “екуща€ координата x на сканирующей линии
	
	Integer[] array = new Integer[] {10, 19, 48, 7, 26, 75, 54, 83, 2, 1};	
	
	SearchTree<Integer> tree = new SearchTree<Integer>(CompareFunc::cmp);	
	BraidedSearchTree<Integer> b_tree = new BraidedSearchTree<Integer>(CompareFunc::cmp);
	RandomizedSearchTree<Integer> r_tree = new RandomizedSearchTree<Integer>(CompareFunc::cmp);
	
	RandomizedSearchTree<Edge> e_tree = new RandomizedSearchTree<Edge>(CompareFunc::edgeCmp2);
	
	double[] e_array = new double[] { 100, 250, 300, 100,
                                      150, 300, 350, 400, 
                                      200, 400, 400, 250,
                                      350, 100, 750, 300,
                                      450, 350, 950, 100,
                                      950, 200, 1100, 200,
                                      800, 400, 950, 250,
                                      1000, 150, 1100, 350	}; 
	
	public MyList() {		
		Integer[] array_sort = tree.heapSort(array, array.length, CompareFunc::cmp);
		
		// TREE
		for(int i = 0; i < array_sort.length; i++)
			System.out.print("" + array_sort[i] + " ");
		
		// B_TREE
		for(int i = 0; i < array.length; i++)
			b_tree.insert(array[i]);
		
		System.out.println("\nB_Tree: " + b_tree.find(26));
		
		b_tree.remove();
		
		// R_TREE
		for(int i = 0; i < array.length; i++)
			r_tree.insert(array[i]);
		
		System.out.println("R_Tree: " + r_tree.locate(54));
		
		r_tree.remove();
		
		System.out.println("R_Tree: " + r_tree.find(54));
		System.out.println("R_Tree: " + r_tree.removeMin());
		System.out.println("R_Tree: " + r_tree.find(1));
	}
	
	public static void main(String[] args) {		
		new MyList();		
	}
	
}
