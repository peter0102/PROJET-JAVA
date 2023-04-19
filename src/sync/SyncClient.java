package sync;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;

public class SyncClient {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("serverHostname");
        SyncInterface sync = (SyncInterface) registry.lookup("SyncService");
        File sourceFolder = new File("sourceFolder");
        File destinationFolder = new File("destinationFolder");
        sync.syncFolders(sourceFolder, destinationFolder);
    }
}
