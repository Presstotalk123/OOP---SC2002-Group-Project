package hms;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class Feedback {
  private String id;
  private String patientId;
  private String comments;
  private int rating;

  public Feedback(String patientId, String comments, int rating) {
    Random random = new Random();
    this.id = String.valueOf(random.nextInt(9000) + 1000);
    this.patientId = patientId;
    this.comments = comments;
    this.rating = rating;
  }

  public void save() throws IOException {
    List<String> lines = Files.readAllLines(Paths.get("../data/feedback.csv"));
    FileOutputStream output = new FileOutputStream("../data/feedback.csv");

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i) + "\n";
      output.write(line.getBytes());
    }

    String newEntry = this.id + "," + this.patientId + "," + this.comments + "," + this.rating;
    output.write(newEntry.getBytes());

    output.close();
  }
}
