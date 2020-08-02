package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import java.io.Serializable;

public class Type implements Serializable {
    Integer id;
    String name;

    public Type(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
