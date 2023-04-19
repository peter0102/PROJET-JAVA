package sync;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SyncServer {
    public static void main(String[] args) throws Exception {
        try {
            Registry registry = LocateRegistry.createRegistry(1234);
            SyncImplementation syncImpl = new SyncImplementation();
            registry.rebind("SyncService",syncImpl);
            System.out.println("Sync service started");
        }
        catch (Exception e) {
            System.out.println("Sync service failed: " + e);
        }
    }
}
