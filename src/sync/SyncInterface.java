package sync;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.File;

public interface SyncInterface extends Remote {
    public void syncFolders(File sourceFolder, File destinationFolder) throws RemoteException;
}
