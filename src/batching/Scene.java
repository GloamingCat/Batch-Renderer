package batching;

import java.util.ArrayList;

public class Scene {
	
	private ArrayList<Obj>[] objects;

	@SuppressWarnings("unchecked")
	public Scene(int depth) {
		objects = new ArrayList[depth];
		for (int i = 0; i < depth; i++) {
			objects[i] = new ArrayList<>();
		}
	}
	
	public void add(Quad quad, float x, float y, float width, float height, int depth) {
		add(quad, new Transform(), x, y, width, height, depth);
	}
	
	public void add(Quad quad, Transform transform, float x, float y, float width, float height, int depth) {
		depth -= transform.offsetDepth;
		objects[depth].add(new Obj(quad, transform, x, y, width, height));
	}
	
	private ArrayList<Obj> sortObjects() {
		ArrayList<Obj> order = new ArrayList<Obj>();
		// TODO: optimize
		for (ArrayList<Obj> list : objects) {
			order.addAll(list);
		}
		return order;
	}
	
	public BatchIterator getBatchIterator() {
		return new BatchIterator(sortObjects());
	}
	
}
