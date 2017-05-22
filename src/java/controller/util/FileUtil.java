/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import com.dashoptimization.XPRD;
import com.dashoptimization.XPRDCompileException;
import com.dashoptimization.XPRDEvent;
import com.dashoptimization.XPRDModel;
import com.dashoptimization.XPRDMosel;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author YOUNESS
 */
public class FileUtil {

    private List<String> data = new ArrayList<>();
    private String line = "";
    private PrintWriter out;

    public FileUtil() {

    }

    public void prepareFile(Path path, String generationDirectory) throws FileNotFoundException {
        try {
            try {
                Files.deleteIfExists(path);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", path);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("%s not empty%n", path);
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }
            //je crÃ©e un nouveau fichier
            out = new PrintWriter(generationDirectory + "/fichier.dat");
            System.out.println("haaahowa instancia out " + out);
        } catch (FileNotFoundException e) {
            System.out.println("haaaaaaaaaaaaaaaaa 010101 080808");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    public void prepareFileTest(Path path, String generationDirectory) throws FileNotFoundException {
        try {
            try {
                Files.deleteIfExists(path);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", path);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("%s not empty%n", path);
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }
            //je crÃ©e un nouveau fichier
            out = new PrintWriter(generationDirectory + "/result1.txt");
            System.out.println("haaahowa instancia out " + out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

//    public void write(String txt) {
//        System.out.print(txt);
//    }
//
//    public void writeLn(String txt) {
//        System.out.println(txt);
//    }
    public void write(String txt) {
        out.print(txt);
    }

    public void writeLn(String txt) {
        out.println(txt);
    }

    public void printLine(String line) {
        data.add(line);
    }

    public PrintWriter getOut() {
        return out;
    }

    public void printAll() {
        data.forEach(e -> System.out.println("line : " + e));
    }

    public void save(Path path, String generationDirectory) throws IOException {

        Path p = Paths.get(generationDirectory + "/fichier.dat");
        Files.deleteIfExists(path);

        try (
                OutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(p, CREATE, CREATE))) {
            data.forEach(l -> {
                try {
                    out.write(l.getBytes(), 0, l.length());
                    out.write("\n".getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            out.close();

            try {
                solution(generationDirectory);
            } catch (XPRDCompileException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public void saveSafe(Path path, String generationDirectory) throws IOException {

        Path p = Paths.get(generationDirectory + "/result1.txt");
        Files.deleteIfExists(path);

        try (
                OutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(p, CREATE, CREATE))) {
            data.forEach(l -> {
                try {
                    out.write(l.getBytes(), 0, l.length());
                    out.write("\r\n".getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            out.close();
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public void solution(String generationDirectory) throws XPRDCompileException {
        try {
            XPRD xprd = new XPRD();
            XPRDMosel moselInst;
            XPRDModel modPrime;
            XPRDEvent event;

            moselInst = xprd.connect("");    // Open connection to remote nodes
            // "" means the node running this program

            // Compile the model file on remote instance
            moselInst.compile("", generationDirectory + "\\Xpress_Modele1.mos", generationDirectory + "\\Xpress_Modele1.bim");

            // Load the bim file into remote instance
            modPrime = moselInst.loadModel(generationDirectory + "\\Xpress_Modele1.bim");

            //modPrime.execParams = "LIMIT=50000";
            modPrime.run();                // Start execution and
            xprd.waitForEvent(10);          // wait 2 seconds for an event

            if (xprd.isQueueEmpty()) // No event has been sent...
            {
                System.out.println("Model too slow: stopping it!");
                modPrime.stop();              // ... stop the model, then wait
                xprd.waitForEvent();
            }
            // An event is available: model finished
            event = xprd.getNextEvent();
            System.out.println("Event value: " + event.value
                    + " sent by model " + event.sender.getNumber());
            System.out.println("Exit status: " + modPrime.getExecStatus());
            System.out.println("Exit code  : " + modPrime.getResult());

            moselInst.unloadModel(modPrime);  // Unload the submodel
            moselInst.disconnect();           // Terminate the connection

            // Clean up temporary files
            Path path = Paths.get(generationDirectory + "/Xpress_Modele1.bim");
            try {
                Files.deleteIfExists(path);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", path);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("%s not empty%n", path);
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }

        } catch (IOException e) {
        }
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

}
