package batching;

import java.util.ArrayList;

public class BatchIterator {
	
	private Obj[] objects;
	private int i;

	public BatchIterator(ArrayList<Obj> objects) {
		this.objects = objects.toArray(new Obj[objects.size()]);
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
		System.out.println("New batch: " + texture);
		for (int k = 0; k < batch.objects.length; k++) {
			batch.objects[k] = objects[k + i];
			System.out.println("Added obj");
		}
		i = j;
		return batch;
	}
	
	public boolean done() {
		return i >= objects.length;
	}
	
}
