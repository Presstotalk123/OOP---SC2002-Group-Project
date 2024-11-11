package hms;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class Feedback {
    private static final String FEEDBACK_FILE = "feedback.csv";
    int ID=new Random().nextInt(9000) + 1000;
    private String id=Integer.toString(ID);
    private String patientId;
    private String comments;
    private int rating;

    public Feedback(String patientId, String comments, int rating) {
        this.id = UUID.randomUUID().toString().substring(0, 8); // Generate unique ID (first 8 chars)
        this.patientId = patientId;
        this.comments = comments;
        this.rating = rating;
    }

    public void save() throws IOException {
        File file = new File(FEEDBACK_FILE);
        boolean isNewFile = !file.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            if (isNewFile) {
                pw.println("id,patientId,comments,rating"); // Header row
            }
            pw.printf("%s,%s,%s,%d%n", this.id, this.patientId, this.comments.replace(",", " "), this.rating);
        }
    }

    public static List<String> viewAllFeedback() throws IOException {
        List<String> feedbackList = new ArrayList<>();
        File file = new File(FEEDBACK_FILE);
        if (!file.exists()) {
            return feedbackList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                feedbackList.add(line);
            }
        }
        return feedbackList;
    }
}
