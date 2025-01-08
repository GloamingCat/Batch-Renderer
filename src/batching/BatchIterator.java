package batching;

import java.util.ArrayList;

public class BatchIterator {
	
	private final Obj[] objects;
	private int i;

	public BatchIterator(ArrayList<Obj> objects) {
		this.objects = new Obj[objects.size()];
		objects.toArray(this.objects);
		this.i = 0;
	}
	
	public Batch next() {
		String texture = objects[i].quad.path;
		int j = i + 1;
		while (j < objects.length) {
			if (objects[j].quad.path.equals(texture))
				j++;
			else
				break;
		}
		Batch batch = new Batch(j - i, texture);
        System.arraycopy(objects, i, batch.objects, 0, batch.objects.length);
		i = j;
		return batch;
	}
	
	public boolean done() {
		return i >= objects.length;
	}
	
}
