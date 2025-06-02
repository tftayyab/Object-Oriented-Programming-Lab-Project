import java.io.File;

public class Read {

    private File folder;
    private File[] files;
    private int numberOfFiles;

    public Read(String filePath) {
        folder = new File(filePath);
        files = folder.listFiles(); // list all the files name in an array
        numberOfFiles = files.length; // how many files 
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    protected String getFileName(int index) {
        return files[index].getAbsolutePath();
    }
}
