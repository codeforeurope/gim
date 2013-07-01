package sistematica.gim.datex;

import java.util.HashSet;
import java.util.Set;

public class Application extends javax.ws.rs.core.Application {

    private Set<Object> singletons = new HashSet();
    private Set<Class<?>> empty = new HashSet();

    public Application() {
        this.singletons.add(new Resource());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
}
