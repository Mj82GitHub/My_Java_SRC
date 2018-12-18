package Elements;

public interface EventPointImpl {

	default void handleTransition(RandomizedSearchTree<Edge> sweepline, 
                                  RandomizedSearchTree<EventPoint> schedule,
                                  List<EventPoint> result) {}
}
