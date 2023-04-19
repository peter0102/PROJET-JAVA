package sync;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.File;

public class SyncClient {
    public static void main(String[] args) throws Exception {
        SyncInterface sync = (SyncInterface) Naming.lookup("rmi://localhost/SyncService");
        File sourceFolder = new File("sourceFolder");
        File destinationFolder = new File("destinationFolder");
        sync.syncFolders(sourceFolder, destinationFolder);
        System.out.println("Sync done");
    }
}
