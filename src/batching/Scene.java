package batching;

import java.util.ArrayList;
import java.util.Random;

public class Scene {
	
	private ArrayList<Obj>[] objects;
	private int minDepth = 0;

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
		objects[depth + minDepth].add(new Obj(quad, transform, x, y, width, height));
	}
	
	public void shuffle() {
		Random rand = new Random(0);
		for (ArrayList<Obj> list : objects) {
			for (int i = 0; i < list.size(); i++) {
				Obj temp = list.get(i);
				int j = rand.nextInt(list.size());
				list.set(i, list.get(j));
				list.set(j, temp);
			}
		}
	}
	
	public ArrayList<Obj> allObjects() {
		ArrayList<Obj> order = new ArrayList<Obj>();
		// TODO: optimize
		for (ArrayList<Obj> list : objects) {
			order.addAll(list);
		}
		return order;
	}
	
	public ArrayList<Obj> allObjects(boolean flip) {
		ArrayList<Obj> order = allObjects();
		if (flip) {
			for (int i = 0; i < order.size() / 2; i++) {
				int j = order.size() - 1 - i;
				Obj obj = order.get(i);
				order.set(i, order.get(j));
				order.set(j, obj);
			}
		}
		return order;
	}
	
	public BatchIterator getBatchIterator(boolean flip) {
		return new BatchIterator(allObjects(flip));
	}
	
	public BatchIterator getBatchIterator() {
		return new BatchIterator(allObjects());
	}
	
}
