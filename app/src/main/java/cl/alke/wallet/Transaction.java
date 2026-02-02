package cl.alke.wallet;

public class Transaction {


    private String recipientName;
    private String recipientEmail;
    private int recipientPhoto;
    private double amount;
    private String notes;
    private long timestamp;
    public String type;

    public Transaction(String recipientName, String recipientEmail, int recipientPhoto,
                       double amount, String notes, long timestamp, String type) {
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.recipientPhoto = recipientPhoto;
        this.amount = amount;
        this.notes = notes;
        this.timestamp = timestamp;
        this.type = type;
    }

    // Getters
    public String getRecipientName() { return recipientName; }
    public String getRecipientEmail() { return recipientEmail; }
    public int getRecipientPhoto() { return recipientPhoto; }
    public double getAmount() { return amount; }
    public String getNotes() { return notes; }
    public long getTimestamp() { return timestamp; }

    // Serialización simple a String JSON
    public String toJson() {
        return recipientName + ";" + recipientEmail + ";" + recipientPhoto + ";" + amount + ";" + notes + ";" + timestamp + ";" + type;
    }

    public static Transaction fromJson(String json) {
        String[] parts = json.split(";");
        return new Transaction(
                parts[0],
                parts[1],
                Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]),
                parts[4],
                Long.parseLong(parts[5]),
                parts[6]
        );
    }
}
