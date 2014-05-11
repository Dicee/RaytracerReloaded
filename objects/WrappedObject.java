package objects;

public abstract class WrappedObject extends Object3D {
	
	public WrappedObject(Texture texture) {
		super(texture);
	}

	public abstract Object3D getWrappingObject();
}
