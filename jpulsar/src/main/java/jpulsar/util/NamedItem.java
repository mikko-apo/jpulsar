package jpulsar.util;

public class NamedItem<T> {
    public String name;
    public T data;

    public NamedItem(String name, T data) {
        this.name = name;
        this.data = data;
    }
}
