import java.io.File;
import java.util.ArrayList;

public class LogReader {

    public static void main(String[] args) {
        System.out.println("\nL O G R E A D E R\n");
        File directory=new File("D:/logs");
        if (!directory.exists()){
            System.out.println("No such file exists! Make sure there is a 'logs' directory inside D:/ location.");
            return;
        }
        if (!directory.isDirectory()){
            System.out.println("This file is supposed to be a directory but it is not.");
            return;
        }
        if(!directory.canRead()){
            System.out.println("Can't read from this directory.");
            return;
        }
        File[] files = directory.listFiles();
        if(files == null){
            System.out.println("Directory is empty.");
            return;
        }
        ArrayList<File> fileList=new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.toString().endsWith(".log")) {
            fileList.add(file);
            }
        }

        for (int i = 0; i < fileList.size(); i++) {
            // Inner nested loop pointing 1 index ahead
            for (int j = i + 1; j < fileList.size(); j++) {
                // Checking elements
                File temp;
                if (fileList.get(j).lastModified() > fileList.get(i).lastModified()) {
                    // Swapping
                    temp = fileList.get(i);
                    fileList.set(i, fileList.get(j));
                    fileList.set(j, temp);
                }
            }
        }

        for (File file : fileList) {
            LogFile logFile = new LogFile(file);
            logFile.readAndPrint();
        }

    }

}
