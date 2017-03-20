package kg.megacom.as.jettycontainer;

import lombok.Data;

@Data
public class CustomResource<T> {

    public String name;
    public T resource;

    public CustomResource(String name, T resource) {
        this.name = name;
        this.resource = resource;
    }
}
