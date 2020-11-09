package jpulsar.scan;

class TestMethod {
    private String name;
    private Class<?>[] parameters;

    public TestMethod(String name, Class<?>[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public <T> Class<?>[] getParameters() {
        return parameters;
    }
}
