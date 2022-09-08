import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogFile {
    private final File file;
    private long timePassed;
    private final HashMap<String, Integer> logSeverity;
    private final HashMap<String, Integer> libsOccurence;
    private final ArrayList<Long> timePassedList;
     LogFile(File file){
         this.file=file;
         this.logSeverity= new HashMap<>();
         this.libsOccurence =new HashMap<>();
         this.timePassed=0;
         this.timePassedList=new ArrayList<>();
     }

     public void readFile(){
         try {
             Scanner myReader = new Scanner(file);
             //System.out.println("file: "+file); //uncomment to print file name

             timePassed=System.currentTimeMillis();
             while (myReader.hasNextLine()){
                 String data = myReader.nextLine();
                 populateMaps(data);
             }
             timePassed = System.currentTimeMillis()-timePassed;
         } catch (FileNotFoundException e){
             System.out.println("An error occurred.");
             e.printStackTrace();
         }
     }

     private void populateMaps(String data){
         ArrayList<String> dataList=new ArrayList<>(Arrays.asList(data.split(" ")));

         if (dataList.size() > 3
                 && isValidDate(dataList.get(0) + " " + dataList.get(1))) {
             timePassedList.add(System.currentTimeMillis());
             // check if the log severity string is so long it stacks with library string, e.g 2017-12-04 11:41:44,750 WARNING[org.jboss.as.config]
             // reasoning for this condition: log with 4char severity INFO has 2 whitespaces after, 5char DEBUG has only 1 whitespace after
             if (dataList.get(2).length() > 5 && dataList.get(2).contains("[")){
                 ArrayList<String> list=new ArrayList<String>(Arrays.asList(dataList.get(2).split("\\[")));
                 logSeverity.merge(list.get(0), 1, Integer::sum);
                 libsOccurence.merge("["+list.get(1), 1, Integer::sum);
             } else {
                 logSeverity.merge(dataList.get(2), 1, Integer::sum);
                 while (dataList.get(3).isEmpty()){
                    dataList.remove(3);
             }
             libsOccurence.merge(dataList.get(3), 1, Integer::sum);
            }

         }
     }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,ms");
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public  String getLogSeverityGrouped(){
        if (!logSeverity.isEmpty()) {
            String groupedSeverity=""; //storing all libs ordered from the least to most severe in this string
            if (logSeverity.containsKey("TRACE")) groupedSeverity += "TRACE=" + logSeverity.get("TRACE") + ", ";
            if (logSeverity.containsKey("DEBUG")) groupedSeverity += "DEBUG=" + logSeverity.get("DEBUG") + ", ";
            if (logSeverity.containsKey("INFO")) groupedSeverity += "INFO=" + logSeverity.get("INFO") + ", ";
            if (logSeverity.containsKey("WARN")) groupedSeverity += "WARN=" + logSeverity.get("WARN") + ", ";
            if (logSeverity.containsKey("WARNING")) groupedSeverity += "WARNING=" + logSeverity.get("WARNING") + ", ";
            if (logSeverity.containsKey("ERROR")) groupedSeverity += "ERROR=" + logSeverity.get("ERROR") + ", ";
            if (logSeverity.containsKey("FATAL")) groupedSeverity += "FATAL=" + logSeverity.get("FATAL") + ", ";
            return groupedSeverity;
        } else return "There are no logs in this file";
    }

    public  HashMap<String, Integer> getLibsOccurrence(){
         return libsOccurence;
    }

    public long getTimePassed(){
         return timePassed;
    }
    public String getTimeScope(){
         if(timePassedList.size()==1)return "Time between the first and last log: 0 milliseconds because there is only one log.";
         else return "Time between the first and last log: "+(timePassedList.get(timePassedList.size()-1)-timePassedList.get(0))+" milliseconds.";
    }
    public String getLogAnalysis(){
        long moreSevereLogCount = 0;
        long lessSevereLogCount= 0;
        //not sure any other log severities exist, maybe INFORMATION but in the given log example it's called INFO, so im not worrying about that
        if (logSeverity.containsKey("ERROR"))moreSevereLogCount +=logSeverity.get("ERROR");
        if (logSeverity.containsKey("FATAL"))moreSevereLogCount += logSeverity.get("FATAL");
        if (logSeverity.containsKey("WARN"))lessSevereLogCount += logSeverity.get("WARN");
        if (logSeverity.containsKey("WARNING"))lessSevereLogCount += logSeverity.get("WARNING");
        if (logSeverity.containsKey("DEBUG"))lessSevereLogCount += logSeverity.get("DEBUG");
        if (logSeverity.containsKey("TRACE"))lessSevereLogCount += logSeverity.get("TRACE");
        if (logSeverity.containsKey("INFO"))lessSevereLogCount += logSeverity.get("INFO");

        return "Logs with severity 'ERROR' or higher: "
                +Math.round(moreSevereLogCount*100.0/(lessSevereLogCount+moreSevereLogCount)*100.0)/100.0+"% ("
                +moreSevereLogCount+" out of " +(lessSevereLogCount+moreSevereLogCount)+" total)";
    }
    public void readAndPrint(){
        readFile();
        if (!timePassedList.isEmpty()) {
            System.out.println("Log file took " + getTimePassed() + " milliseconds to read.");
            System.out.println(getTimeScope());
            System.out.println("Logs grouped by severity: " + getLogSeverityGrouped());
            //System.out.println(logFile.getLibsOccurrence()); // uncomment to print libs map, contains key: lib name and value: number of times it occured
            System.out.println("Unique libs occurrences: " + getLibsOccurrence().size()+".");
            System.out.println(getLogAnalysis()+".\n");
        } // else System.out.println("File has no logs.\n"); // uncomment to print that file has no logs (or just don't mention that file entirely)
    }
}
