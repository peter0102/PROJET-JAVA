package sync;

import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.rmi.RemoteException;

public class SyncImplementation extends UnicastRemoteObject implements SyncInterface{
    private Gui gui;
    public SyncImplementation() throws RemoteException {
        super();
        this.gui = new Gui();
    }

    @Override
    public void syncFolders(File sourceFolder, File destinationFolder) throws RemoteException {
        String sourceFolderName= sourceFolder.getName();
        String destinationFolderName= destinationFolder.getName();
        Sync sync = new Sync(sourceFolderName, destinationFolderName);
        Thread thread = new Thread(sync);
        thread.start();
    }
}
