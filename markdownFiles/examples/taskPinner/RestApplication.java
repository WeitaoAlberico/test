import sailpoint.rest.SailPointRestApplication;

import java.util.Set;


public class RestApplication extends SailPointRestApplication{
    public RestApplication() {
        super();}

    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> classes = super.getClasses();
        classes.add(RestTaskLauncher.class);

        return classes;
    }
}
