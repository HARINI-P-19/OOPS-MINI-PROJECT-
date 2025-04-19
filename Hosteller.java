import java.io.Serializable;
import java.util.Objects;

public class Hosteller implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int age;
    private int roomNo;
    private double feesPaid;
    private String department;
    private int year;

    public Hosteller(int id, String name, int age, int roomNo, double feesPaid, String department, int year) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.roomNo = roomNo;
        this.feesPaid = feesPaid;
        this.department = department;
        this.year = year;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public double getFeesPaid() {
        return feesPaid;
    }

    public String getDepartment() {
        return department;
    }

    public int getYear() {
        return year;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public void setFeesPaid(double feesPaid) {
        this.feesPaid = feesPaid;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // For displaying in the UI
    @Override
    public String toString() {
        return id + " | " + name + " | Age: " + age + " | Room: " + roomNo +
               " | Fees: Rs." + feesPaid + " | " + department + " | Year: " + year;
    }

    // For exporting to CSV
    public String toCSV() {
        return id + "," + name + "," + age + "," + roomNo + "," + feesPaid + "," + department + "," + year;
    }

    // Optional: equals and hashCode (useful for comparisons, sets, etc.)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hosteller)) return false;
        Hosteller other = (Hosteller) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
