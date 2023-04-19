package sync;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SyncServer {
    public static void main(String[] args) throws Exception {
        SyncImplementation syncImpl = new SyncImplementation();
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind("SyncService", syncImpl);
        System.out.println("Sync service started");
    }
}
