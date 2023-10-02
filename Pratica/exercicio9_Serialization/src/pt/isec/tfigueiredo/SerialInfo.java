package pt.isec.tfigueiredo;

import java.io.*;

public class SerialInfo implements Serializable {

    private String hour, minutes, seconds;
    private final String fileName = "files/maintenance.ser";
    File serFile = new File(fileName);

    public SerialInfo() {
        this.hour = "X";
        this.minutes = "Y";
        this.seconds = "Z";
    }

    private void setSerialInfo(SerialInfo serialInfo) {
        this.hour = serialInfo.hour;
        this.minutes = serialInfo.minutes;
        this.seconds = serialInfo.seconds;
    }

    public void openSerialFile() {
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            SerialInfo getS = (SerialInfo) in.readObject();

            in.close();
            fileIn.close();

            setSerialInfo(getS);

        } catch (IOException | ClassNotFoundException e) {
            //throw new RuntimeException(e);
        }
    }

    public boolean updateSerial(String horas, String minutos, String segundos) {

        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            setHour(horas);
            setMinutes(minutos);
            setSeconds(segundos);

            out.writeObject(this);
            out.close();
            fileOut.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getHour() {
        return hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getSeconds() {
        return seconds;
    }


    //SETS - única informação que pode ser editável

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMinutes(String minutes) { this.minutes = minutes; }

    public void setSeconds(String seconds) { this.seconds = seconds; }

}
