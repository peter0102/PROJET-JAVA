package sync;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SyncServer {
    public static void main(String[] args) throws Exception {
        SyncImplementation syncImpl = new SyncImplementation();
        Naming.rebind("SyncService",syncImpl);
        System.out.println("Sync service started");
    }
}
