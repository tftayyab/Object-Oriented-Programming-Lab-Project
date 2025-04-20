import java.io.File;

public class Read {

    private File folder;
    private File[] files;
    private int numberOfFiles;

    public Read(String filePath) {
        folder = new File(filePath);
        files = folder.listFiles();
        numberOfFiles = files.length;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    protected String getFileName(int index) {
        return files[index].getAbsolutePath();
    }
}
