package codes.recursive.cnms.ords;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        String libsunecPath = System.getenv("LIBSUNEC_PATH");
        if( libsunecPath != null ) {
            System.setProperty("java.library.path", libsunecPath);
            System.loadLibrary("sunec");
        }
        Micronaut.run(Application.class);
    }
}