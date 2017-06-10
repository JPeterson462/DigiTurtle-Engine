package utils.profiling;

public abstract class Pool<T> {

	protected abstract T create();

	public T get() {
		return create();
	}

	public void recycle(T t) {
		
	}

}
