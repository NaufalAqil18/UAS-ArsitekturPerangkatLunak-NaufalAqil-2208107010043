import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// ==================== OBSERVER PATTERN ====================

interface Observer {
    void update(String message);
}

interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);
}

// ==================== STRATEGY PATTERN ====================

interface NotificationStrategy {
    void sendNotification(String recipient, String message);
}

class EmailNotification implements NotificationStrategy {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("[EMAIL] Mengirim ke " + recipient + ": " + message);
    }
}

class SMSNotification implements NotificationStrategy {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("[SMS] Mengirim ke " + recipient + ": " + message);
    }
}

class WhatsAppNotification implements NotificationStrategy {
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("[WHATSAPP] Mengirim ke " + recipient + ": " + message);
    }
}

class NotificationContext {
    private NotificationStrategy strategy;
    
    public void setStrategy(NotificationStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void sendNotification(String recipient, String message) {
        if (strategy != null) {
            strategy.sendNotification(recipient, message);
        }
    }
}

// ==================== MODEL CLASSES ====================

class Patient implements Observer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private NotificationContext notificationContext;
    
    public Patient(String id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.notificationContext = new NotificationContext();
        this.notificationContext.setStrategy(new EmailNotification()); // Default strategy
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    
    public void setNotificationStrategy(NotificationStrategy strategy) {
        notificationContext.setStrategy(strategy);
    }
    
    @Override
    public void update(String message) {
        notificationContext.sendNotification(email, message);
    }
    
    public String toFileString() {
        return id + "|" + name + "|" + email + "|" + phone + "|" + address;
    }
    
    public static Patient fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Patient(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}

class Doctor implements Observer {
    private String id;
    private String name;
    private String specialization;
    private List<Schedule> schedules;
    private NotificationContext notificationContext;
    
    public Doctor(String id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.schedules = new ArrayList<>();
        this.notificationContext = new NotificationContext();
        this.notificationContext.setStrategy(new SMSNotification()); // Default strategy
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public List<Schedule> getSchedules() { return schedules; }
    
    public void setNotificationStrategy(NotificationStrategy strategy) {
        notificationContext.setStrategy(strategy);
    }
    
    @Override
    public void update(String message) {
        notificationContext.sendNotification(name, message);
    }
    
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
    
    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }
    
    public String toFileString() {
        return id + "|" + name + "|" + specialization;
    }
    
    public static Doctor fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new Doctor(parts[0], parts[1], parts[2]);
    }
}

class Schedule {
    private String id;
    private String doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
    
    public Schedule(String id, String doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
    }
    
    public String getId() { return id; }
    public String getDoctorId() { return doctorId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isAvailable() { return isAvailable; }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    public String toFileString() {
        return id + "|" + doctorId + "|" + date.toString() + "|" + 
               startTime.toString() + "|" + endTime.toString() + "|" + isAvailable;
    }
    
    public static Schedule fromFileString(String line) {
        String[] parts = line.split("\\|");
        Schedule s = new Schedule(parts[0], parts[1], 
                                  LocalDate.parse(parts[2]),
                                  LocalTime.parse(parts[3]),
                                  LocalTime.parse(parts[4]));
        s.setAvailable(Boolean.parseBoolean(parts[5]));
        return s;
    }
}

class Appointment implements Subject {
    private String id;
    private String patientId;
    private String scheduleId;
    private LocalDate bookingDate;
    private String status;
    private List<Observer> observers;
    
    public Appointment(String id, String patientId, String scheduleId) {
        this.id = id;
        this.patientId = patientId;
        this.scheduleId = scheduleId;
        this.bookingDate = LocalDate.now();
        this.status = "Booked";
        this.observers = new ArrayList<>();
    }
    
    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getScheduleId() { return scheduleId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) {
        this.status = status;
        notifyObservers("Status appointment berubah menjadi: " + status);
    }
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    public String toFileString() {
        return id + "|" + patientId + "|" + scheduleId + "|" + 
               bookingDate.toString() + "|" + status;
    }
    
    public static Appointment fromFileString(String line) {
        String[] parts = line.split("\\|");
        Appointment a = new Appointment(parts[0], parts[1], parts[2]);
        a.status = parts[4];
        return a;
    }
}

class ConsultationHistory {
    private String id;
    private String appointmentId;
    private LocalDate consultationDate;
    private String diagnosis;
    private String notes;
    
    public ConsultationHistory(String id, String appointmentId, String diagnosis, String notes) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.consultationDate = LocalDate.now();
        this.diagnosis = diagnosis;
        this.notes = notes;
    }
    
    public String getId() { return id; }
    public String getAppointmentId() { return appointmentId; }
    public LocalDate getConsultationDate() { return consultationDate; }
    public String getDiagnosis() { return diagnosis; }
    public String getNotes() { return notes; }
    
    public String toFileString() {
        return id + "|" + appointmentId + "|" + consultationDate.toString() + "|" + 
               diagnosis + "|" + notes;
    }
    
    public static ConsultationHistory fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new ConsultationHistory(parts[0], parts[1], parts[3], parts[4]);
    }
}

// ==================== SINGLETON PATTERN - DATABASE ====================

class Database {
    private static Database instance;
    private Map<String, Patient> patients;
    private Map<String, Doctor> doctors;
    private Map<String, Schedule> schedules;
    private Map<String, Appointment> appointments;
    private Map<String, ConsultationHistory> consultationHistories;
    
    private static final String DATA_DIR = "data";
    private static final String PATIENTS_FILE = DATA_DIR + "/patients.txt";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctors.txt";
    private static final String SCHEDULES_FILE = DATA_DIR + "/schedules.txt";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.txt";
    private static final String HISTORIES_FILE = DATA_DIR + "/histories.txt";
    private static final String COUNTERS_FILE = DATA_DIR + "/counters.txt";
    
    private int patientCounter = 1;
    private int doctorCounter = 1;
    private int scheduleCounter = 1;
    private int appointmentCounter = 1;
    private int historyCounter = 1;
    
    private Database() {
        patients = new HashMap<>();
        doctors = new HashMap<>();
        schedules = new HashMap<>();
        appointments = new HashMap<>();
        consultationHistories = new HashMap<>();
        
        // Create data directory if not exists
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("[INFO] Folder 'data' berhasil dibuat untuk menyimpan database.");
        }
        
        loadAllData();
    }
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    private void loadAllData() {
        loadCounters();
        loadPatients();
        loadDoctors();
        loadSchedules();
        loadAppointments();
        loadConsultationHistories();
        
        // Initialize sample data if empty
        if (doctors.isEmpty()) {
            initializeSampleData();
        }
    }
    
    private void loadCounters() {
        try (BufferedReader br = new BufferedReader(new FileReader(COUNTERS_FILE))) {
            patientCounter = Integer.parseInt(br.readLine());
            doctorCounter = Integer.parseInt(br.readLine());
            scheduleCounter = Integer.parseInt(br.readLine());
            appointmentCounter = Integer.parseInt(br.readLine());
            historyCounter = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            // Use default values if file doesn't exist
        }
    }
    
    private void saveCounters() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(COUNTERS_FILE))) {
            pw.println(patientCounter);
            pw.println(doctorCounter);
            pw.println(scheduleCounter);
            pw.println(appointmentCounter);
            pw.println(historyCounter);
        } catch (IOException e) {
            System.err.println("Error saving counters: " + e.getMessage());
        }
    }
    
    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Patient patient = Patient.fromFileString(line);
                patients.put(patient.getId(), patient);
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }
    
    private void savePatients() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient patient : patients.values()) {
                pw.println(patient.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving patients: " + e.getMessage());
        }
    }
    
    private void loadDoctors() {
        try (BufferedReader br = new BufferedReader(new FileReader(DOCTORS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Doctor doctor = Doctor.fromFileString(line);
                doctors.put(doctor.getId(), doctor);
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }
    
    private void saveDoctors() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor doctor : doctors.values()) {
                pw.println(doctor.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving doctors: " + e.getMessage());
        }
    }
    
    private void loadSchedules() {
        try (BufferedReader br = new BufferedReader(new FileReader(SCHEDULES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Schedule schedule = Schedule.fromFileString(line);
                schedules.put(schedule.getId(), schedule);
                Doctor doctor = doctors.get(schedule.getDoctorId());
                if (doctor != null) {
                    doctor.addSchedule(schedule);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }
    
    private void saveSchedules() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SCHEDULES_FILE))) {
            for (Schedule schedule : schedules.values()) {
                pw.println(schedule.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving schedules: " + e.getMessage());
        }
    }
    
    private void loadAppointments() {
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Appointment appointment = Appointment.fromFileString(line);
                appointments.put(appointment.getId(), appointment);
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }
    
    private void saveAppointments() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment appointment : appointments.values()) {
                pw.println(appointment.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }
    
    private void loadConsultationHistories() {
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORIES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                ConsultationHistory history = ConsultationHistory.fromFileString(line);
                consultationHistories.put(history.getId(), history);
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
    }
    
    private void saveConsultationHistories() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORIES_FILE))) {
            for (ConsultationHistory history : consultationHistories.values()) {
                pw.println(history.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving consultation histories: " + e.getMessage());
        }
    }
    
    private void initializeSampleData() {
        Doctor d1 = new Doctor("D001", "Dr. Ahmad Yani", "Kardiologi");
        Doctor d2 = new Doctor("D002", "Dr. Siti Rahma", "Pediatri");
        Doctor d3 = new Doctor("D003", "Dr. Budi Santoso", "Orthopedi");
        addDoctor(d1);
        addDoctor(d2);
        addDoctor(d3);
        doctorCounter = 4;
        
        addSchedule(new Schedule("S001", d1.getId(), LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(10, 0)));
        addSchedule(new Schedule("S002", d1.getId(), LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0)));
        addSchedule(new Schedule("S003", d2.getId(), LocalDate.now().plusDays(2), LocalTime.of(14, 0), LocalTime.of(15, 0)));
        addSchedule(new Schedule("S004", d3.getId(), LocalDate.now().plusDays(3), LocalTime.of(11, 0), LocalTime.of(12, 0)));
        
        saveCounters();
    }
    
    public String generatePatientId() {
        String id = String.format("P%03d", patientCounter++);
        saveCounters();
        return id;
    }
    
    public String generateDoctorId() {
        String id = String.format("D%03d", doctorCounter++);
        saveCounters();
        return id;
    }
    
    public String generateScheduleId() {
        String id = String.format("S%03d", scheduleCounter++);
        saveCounters();
        return id;
    }
    
    public String generateAppointmentId() {
        String id = String.format("A%03d", appointmentCounter++);
        saveCounters();
        return id;
    }
    
    public String generateHistoryId() {
        String id = String.format("H%03d", historyCounter++);
        saveCounters();
        return id;
    }
    
    public void addPatient(Patient patient) {
        patients.put(patient.getId(), patient);
        savePatients();
    }
    
    public void addDoctor(Doctor doctor) {
        doctors.put(doctor.getId(), doctor);
        saveDoctors();
    }
    
    public void addSchedule(Schedule schedule) {
        schedules.put(schedule.getId(), schedule);
        Doctor doctor = doctors.get(schedule.getDoctorId());
        if (doctor != null) {
            doctor.addSchedule(schedule);
        }
        saveSchedules();
    }
    
    public void updateSchedule(Schedule schedule) {
        saveSchedules();
    }
    
    public void removeSchedule(String scheduleId) {
        Schedule schedule = schedules.get(scheduleId);
        if (schedule != null) {
            schedules.remove(scheduleId);
            Doctor doctor = doctors.get(schedule.getDoctorId());
            if (doctor != null) {
                doctor.removeSchedule(schedule);
            }
            saveSchedules();
        }
    }
    
    public void addAppointment(Appointment appointment) {
        appointments.put(appointment.getId(), appointment);
        saveAppointments();
    }
    
    public void addConsultationHistory(ConsultationHistory history) {
        consultationHistories.put(history.getId(), history);
        saveConsultationHistories();
    }
    
    public Patient getPatient(String id) {
        return patients.get(id);
    }
    
    public Doctor getDoctor(String id) {
        return doctors.get(id);
    }
    
    public Schedule getSchedule(String id) {
        return schedules.get(id);
    }
    
    public Appointment getAppointment(String id) {
        return appointments.get(id);
    }
    
    public Collection<Patient> getAllPatients() {
        return patients.values();
    }
    
    public Collection<Doctor> getAllDoctors() {
        return doctors.values();
    }
    
    public Collection<Schedule> getAllSchedules() {
        return schedules.values();
    }
    
    public Collection<Appointment> getAllAppointments() {
        return appointments.values();
    }
    
    public Collection<ConsultationHistory> getAllConsultationHistories() {
        return consultationHistories.values();
    }
    
    public List<Appointment> getPatientAppointments(String patientId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment app : appointments.values()) {
            if (app.getPatientId().equals(patientId)) {
                result.add(app);
            }
        }
        return result;
    }
    
    public List<ConsultationHistory> getPatientConsultationHistory(String patientId) {
        List<ConsultationHistory> result = new ArrayList<>();
        for (ConsultationHistory history : consultationHistories.values()) {
            Appointment app = appointments.get(history.getAppointmentId());
            if (app != null && app.getPatientId().equals(patientId)) {
                result.add(history);
            }
        }
        return result;
    }
}

// ==================== FACTORY METHOD PATTERN ====================

abstract class UserFactory {
    public abstract Patient createPatient(String name, String email, String phone, String address);
    public abstract Doctor createDoctor(String name, String specialization);
}

class ConcreteUserFactory extends UserFactory {
    private Database db = Database.getInstance();
    
    @Override
    public Patient createPatient(String name, String email, String phone, String address) {
        String id = db.generatePatientId();
        Patient patient = new Patient(id, name, email, phone, address);
        db.addPatient(patient);
        System.out.println("\n[FACTORY] Patient baru berhasil dibuat dengan ID: " + id);
        return patient;
    }
    
    @Override
    public Doctor createDoctor(String name, String specialization) {
        String id = db.generateDoctorId();
        Doctor doctor = new Doctor(id, name, specialization);
        db.addDoctor(doctor);
        System.out.println("\n[FACTORY] Doctor baru berhasil dibuat dengan ID: " + id);
        return doctor;
    }
}

// ==================== MAIN APPLICATION ====================

public class DoctorSchedulingApp {
    private static Database db = Database.getInstance(); // Singleton
    private static UserFactory userFactory = new ConcreteUserFactory(); // Factory Method
    private static Scanner scanner = new Scanner(System.in);
    private static Patient currentPatient = null;
    private static Doctor currentDoctor = null;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   SISTEM PENJADWALAN DOKTER KLINIK SEHAT      ║");
        System.out.println("║        (Singleton, Factory, Strategy,          ║");
        System.out.println("║         Observer Pattern + TXT Database)       ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    patientMenu();
                    break;
                case 2:
                    doctorMenu();
                    break;
                case 3:
                    viewAllDoctorsAndPatients();
                    break;
                case 4:
                    System.out.println("\n✓ Data telah disimpan ke file .txt");
                    System.out.println("Terima kasih telah menggunakan sistem kami!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MENU UTAMA");
        System.out.println("=".repeat(50));
        System.out.println("1. Menu Pasien");
        System.out.println("2. Menu Dokter");
        System.out.println("3. Lihat Daftar Dokter & Pasien");
        System.out.println("4. Keluar");
        System.out.println("=".repeat(50));
    }
    
    private static void viewAllDoctorsAndPatients() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("DAFTAR DOKTER & PASIEN");
            System.out.println("=".repeat(50));
            System.out.println("1. Lihat Semua Dokter");
            System.out.println("2. Lihat Semua Pasien");
            System.out.println("3. Kembali ke Menu Utama");
            System.out.println("=".repeat(50));
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    viewAllDoctors();
                    break;
                case 2:
                    viewAllPatients();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    private static void viewAllDoctors() {
        System.out.println("\n>>> DAFTAR SEMUA DOKTER <<<");
        System.out.println("=".repeat(80));
        
        Collection<Doctor> doctors = db.getAllDoctors();
        
        if (doctors.isEmpty()) {
            System.out.println("Belum ada dokter terdaftar.");
        } else {
            int no = 1;
            for (Doctor doctor : doctors) {
                System.out.println(no + ". ID: " + doctor.getId());
                System.out.println("   Nama: " + doctor.getName());
                System.out.println("   Spesialisasi: " + doctor.getSpecialization());
                System.out.println("   Jumlah Jadwal: " + doctor.getSchedules().size());
                
                // Hitung jadwal tersedia
                long availableSchedules = doctor.getSchedules().stream()
                    .filter(Schedule::isAvailable)
                    .count();
                System.out.println("   Jadwal Tersedia: " + availableSchedules);
                
                System.out.println("   " + "-".repeat(76));
                no++;
            }
            System.out.println("\nTotal Dokter: " + doctors.size());
        }
        System.out.println("=".repeat(80));
    }
    
    private static void viewAllPatients() {
        System.out.println("\n>>> DAFTAR SEMUA PASIEN <<<");
        System.out.println("=".repeat(80));
        
        Collection<Patient> patients = db.getAllPatients();
        
        if (patients.isEmpty()) {
            System.out.println("Belum ada pasien terdaftar.");
        } else {
            int no = 1;
            for (Patient patient : patients) {
                System.out.println(no + ". ID: " + patient.getId());
                System.out.println("   Nama: " + patient.getName());
                System.out.println("   Email: " + patient.getEmail());
                System.out.println("   Telepon: " + patient.getPhone());
                System.out.println("   Alamat: " + patient.getAddress());
                
                // Hitung jumlah appointment
                List<Appointment> appointments = db.getPatientAppointments(patient.getId());
                System.out.println("   Jumlah Appointment: " + appointments.size());
                
                // Hitung jumlah riwayat konsultasi
                List<ConsultationHistory> histories = db.getPatientConsultationHistory(patient.getId());
                System.out.println("   Riwayat Konsultasi: " + histories.size());
                
                System.out.println("   " + "-".repeat(76));
                no++;
            }
            System.out.println("\nTotal Pasien: " + patients.size());
        }
        System.out.println("=".repeat(80));
    }
    
    private static void patientMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("MENU PASIEN");
            System.out.println("=".repeat(50));
            System.out.println("1. Registrasi Akun Baru");
            System.out.println("2. Login Pasien");
            System.out.println("3. Kembali ke Menu Utama");
            System.out.println("=".repeat(50));
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    loginPatient();
                    if (currentPatient != null) {
                        patientLoggedInMenu();
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    private static void registerPatient() {
        System.out.println("\n>>> REGISTRASI PASIEN BARU (FACTORY METHOD) <<<");
        System.out.print("Nama: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telepon: ");
        String phone = scanner.nextLine();
        System.out.print("Alamat: ");
        String address = scanner.nextLine();
        
        // Using Factory Method Pattern
        Patient patient = userFactory.createPatient(name, email, phone, address);
        
        System.out.println("✓ Registrasi berhasil!");
        System.out.println("ID Pasien Anda: " + patient.getId());
        System.out.println("Silakan login menggunakan ID ini.");
    }
    
    private static void loginPatient() {
        System.out.println("\n>>> LOGIN PASIEN <<<");
        System.out.print("Masukkan ID Pasien: ");
        String id = scanner.nextLine();
        
        Patient patient = db.getPatient(id);
        if (patient != null) {
            currentPatient = patient;
            System.out.println("✓ Login berhasil! Selamat datang, " + patient.getName());
            
            // Strategy Pattern - Pilih metode notifikasi
            chooseNotificationStrategy(patient);
        } else {
            System.out.println("✗ ID Pasien tidak ditemukan!");
        }
    }
    
    private static void chooseNotificationStrategy(Patient patient) {
        System.out.println("\n>>> PILIH METODE NOTIFIKASI (STRATEGY PATTERN) <<<");
        System.out.println("1. Email");
        System.out.println("2. SMS");
        System.out.println("3. WhatsApp");
        int choice = getIntInput("Pilih metode notifikasi: ");
        
        switch (choice) {
            case 1:
                patient.setNotificationStrategy(new EmailNotification());
                break;
            case 2:
                patient.setNotificationStrategy(new SMSNotification());
                break;
            case 3:
                patient.setNotificationStrategy(new WhatsAppNotification());
                break;
            default:
                patient.setNotificationStrategy(new EmailNotification());
        }
        System.out.println("✓ Metode notifikasi berhasil diatur!");
    }
    
    private static void patientLoggedInMenu() {
        while (currentPatient != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("MENU PASIEN - " + currentPatient.getName());
            System.out.println("=".repeat(50));
            System.out.println("1. Lihat Daftar Dokter");
            System.out.println("2. Booking Jadwal Konsultasi");
            System.out.println("3. Lihat Riwayat Konsultasi");
            System.out.println("4. Ubah Metode Notifikasi");
            System.out.println("5. Logout");
            System.out.println("=".repeat(50));
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    viewDoctorList();
                    break;
                case 2:
                    bookAppointment();
                    break;
                case 3:
                    viewConsultationHistory();
                    break;
                case 4:
                    chooseNotificationStrategy(currentPatient);
                    break;
                case 5:
                    currentPatient = null;
                    System.out.println("✓ Logout berhasil!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    private static void viewDoctorList() {
        System.out.println("\n>>> DAFTAR DOKTER & JADWAL <<<");
        System.out.println("=".repeat(80));
        
        for (Doctor doctor : db.getAllDoctors()) {
            System.out.println("ID: " + doctor.getId());
            System.out.println("Nama: " + doctor.getName());
            System.out.println("Spesialisasi: " + doctor.getSpecialization());
            System.out.println("\nJadwal Tersedia:");
            
            boolean hasSchedule = false;
            for (Schedule schedule : doctor.getSchedules()) {
                if (schedule.isAvailable()) {
                    System.out.printf("  [%s] %s | %s - %s\n", 
                        schedule.getId(),
                        schedule.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    hasSchedule = true;
                }
            }
            
            if (!hasSchedule) {
                System.out.println("  Tidak ada jadwal tersedia");
            }
            
            System.out.println("=".repeat(80));
        }
    }
    
    private static void bookAppointment() {
        viewDoctorList();
        
        System.out.println("\n>>> BOOKING JADWAL KONSULTASI (OBSERVER PATTERN) <<<");
        System.out.print("Masukkan ID Jadwal yang ingin dipesan: ");
        String scheduleId = scanner.nextLine();
        
        Schedule schedule = db.getSchedule(scheduleId);
        
        if (schedule == null) {
            System.out.println("✗ Jadwal tidak ditemukan!");
            return;
        }
        
        if (!schedule.isAvailable()) {
            System.out.println("✗ Jadwal sudah dipesan oleh pasien lain!");
            return;
        }
        
        String appointmentId = db.generateAppointmentId();
        Appointment appointment = new Appointment(appointmentId, currentPatient.getId(), scheduleId);
        
        // Observer Pattern - Attach observers
        appointment.attach(currentPatient);
        Doctor doctor = db.getDoctor(schedule.getDoctorId());
        if (doctor != null) {
            appointment.attach(doctor);
        }
        
        db.addAppointment(appointment);
        schedule.setAvailable(false);
        db.updateSchedule(schedule);
        
        // Notify observers
        appointment.notifyObservers("Booking berhasil! Appointment ID: " + appointmentId + 
                                   " dengan " + (doctor != null ? doctor.getName() : "Dokter") + 
                                   " pada " + schedule.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        
        System.out.println("\n✓ Booking berhasil!");
        System.out.println("ID Appointment: " + appointmentId);
        System.out.println("Dokter: " + (doctor != null ? doctor.getName() : "Dokter"));
        System.out.println("Tanggal: " + schedule.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        System.out.println("Waktu: " + schedule.getStartTime() + " - " + schedule.getEndTime());
    }
    
    private static void viewConsultationHistory() {
        System.out.println("\n>>> RIWAYAT KONSULTASI <<<");
        System.out.println("=".repeat(80));
        
        List<ConsultationHistory> histories = db.getPatientConsultationHistory(currentPatient.getId());
        
        if (histories.isEmpty()) {
            System.out.println("Belum ada riwayat konsultasi.");
        } else {
            for (ConsultationHistory history : histories) {
                Appointment app = db.getAppointment(history.getAppointmentId());
                if (app != null) {
                    Schedule schedule = db.getSchedule(app.getScheduleId());
                    Doctor doctor = schedule != null ? db.getDoctor(schedule.getDoctorId()) : null;
                    
                    System.out.println("ID: " + history.getId());
                    System.out.println("Tanggal Konsultasi: " + history.getConsultationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    System.out.println("Dokter: " + (doctor != null ? doctor.getName() : "N/A"));
                    System.out.println("Diagnosis: " + history.getDiagnosis());
                    System.out.println("Catatan: " + history.getNotes());
                    System.out.println("=".repeat(80));
                }
            }
        }
    }
    
    private static void doctorMenu() {
        System.out.println("\n>>> LOGIN DOKTER <<<");
        System.out.print("Masukkan ID Dokter: ");
        String id = scanner.nextLine();
        
        Doctor doctor = db.getDoctor(id);
        if (doctor != null) {
            currentDoctor = doctor;
            System.out.println("✓ Login berhasil! Selamat datang, " + doctor.getName());
            
            // Strategy Pattern - Pilih metode notifikasi
            chooseNotificationStrategyDoctor(doctor);
            
            doctorLoggedInMenu();
        } else {
            System.out.println("✗ ID Dokter tidak ditemukan!");
        }
    }
    
    private static void chooseNotificationStrategyDoctor(Doctor doctor) {
        System.out.println("\n>>> PILIH METODE NOTIFIKASI (STRATEGY PATTERN) <<<");
        System.out.println("1. SMS");
        System.out.println("2. Email");
        System.out.println("3. WhatsApp");
        int choice = getIntInput("Pilih metode notifikasi: ");
        
        switch (choice) {
            case 1:
                doctor.setNotificationStrategy(new SMSNotification());
                break;
            case 2:
                doctor.setNotificationStrategy(new EmailNotification());
                break;
            case 3:
                doctor.setNotificationStrategy(new WhatsAppNotification());
                break;
            default:
                doctor.setNotificationStrategy(new SMSNotification());
        }
        System.out.println("✓ Metode notifikasi berhasil diatur!");
    }
    
    private static void doctorLoggedInMenu() {
        while (currentDoctor != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("MENU DOKTER - " + currentDoctor.getName());
            System.out.println("=".repeat(50));
            System.out.println("1. Lihat Jadwal Saya");
            System.out.println("2. Tambah Jadwal Baru");
            System.out.println("3. Hapus Jadwal");
            System.out.println("4. Lihat Daftar Appointment");
            System.out.println("5. Selesaikan Konsultasi");
            System.out.println("6. Ubah Metode Notifikasi");
            System.out.println("7. Logout");
            System.out.println("=".repeat(50));
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    viewDoctorSchedule();
                    break;
                case 2:
                    addDoctorSchedule();
                    break;
                case 3:
                    removeDoctorSchedule();
                    break;
                case 4:
                    viewDoctorAppointments();
                    break;
                case 5:
                    completeConsultation();
                    break;
                case 6:
                    chooseNotificationStrategyDoctor(currentDoctor);
                    break;
                case 7:
                    currentDoctor = null;
                    System.out.println("✓ Logout berhasil!");
                    return;
                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    private static void viewDoctorSchedule() {
        System.out.println("\n>>> JADWAL PRAKTEK SAYA <<<");
        System.out.println("=".repeat(80));
        
        for (Schedule schedule : currentDoctor.getSchedules()) {
            System.out.printf("[%s] %s | %s - %s | Status: %s\n",
                schedule.getId(),
                schedule.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                schedule.isAvailable() ? "Tersedia" : "Terpesan");
        }
        System.out.println("=".repeat(80));
    }
    
    private static void addDoctorSchedule() {
        System.out.println("\n>>> TAMBAH JADWAL BARU <<<");
        
        try {
            System.out.print("Tanggal (dd-MM-yyyy): ");
            String dateStr = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            
            System.out.print("Jam Mulai (HH:mm): ");
            String startStr = scanner.nextLine();
            LocalTime startTime = LocalTime.parse(startStr, DateTimeFormatter.ofPattern("HH:mm"));
            
            System.out.print("Jam Selesai (HH:mm): ");
            String endStr = scanner.nextLine();
            LocalTime endTime = LocalTime.parse(endStr, DateTimeFormatter.ofPattern("HH:mm"));
            
            String scheduleId = db.generateScheduleId();
            Schedule schedule = new Schedule(scheduleId, currentDoctor.getId(), date, startTime, endTime);
            db.addSchedule(schedule);
            
            System.out.println("✓ Jadwal berhasil ditambahkan!");
            System.out.println("ID Jadwal: " + scheduleId);
            System.out.println("Data tersimpan di file schedules.txt");
        } catch (DateTimeParseException e) {
            System.out.println("✗ Format tanggal/waktu tidak valid!");
        }
    }
    
    private static void removeDoctorSchedule() {
        viewDoctorSchedule();
        System.out.print("\nMasukkan ID Jadwal yang ingin dihapus: ");
        String scheduleId = scanner.nextLine();
        
        Schedule schedule = db.getSchedule(scheduleId);
        if (schedule != null && schedule.getDoctorId().equals(currentDoctor.getId())) {
            if (!schedule.isAvailable()) {
                System.out.println("✗ Jadwal tidak dapat dihapus karena sudah ada booking!");
            } else {
                db.removeSchedule(scheduleId);
                System.out.println("✓ Jadwal berhasil dihapus!");
                System.out.println("Data diperbarui di file schedules.txt");
            }
        } else {
            System.out.println("✗ Jadwal tidak ditemukan!");
        }
    }
    
    private static void viewDoctorAppointments() {
        System.out.println("\n>>> DAFTAR APPOINTMENT <<<");
        System.out.println("=".repeat(80));
        
        boolean hasAppointment = false;
        for (Appointment app : db.getAllAppointments()) {
            Schedule schedule = db.getSchedule(app.getScheduleId());
            if (schedule != null && schedule.getDoctorId().equals(currentDoctor.getId())) {
                Patient patient = db.getPatient(app.getPatientId());
                
                System.out.println("ID Appointment: " + app.getId());
                System.out.println("Pasien: " + (patient != null ? patient.getName() : "N/A"));
                System.out.println("Tanggal: " + schedule.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                System.out.println("Waktu: " + schedule.getStartTime() + " - " + schedule.getEndTime());
                System.out.println("Status: " + app.getStatus());
                System.out.println("=".repeat(80));
                hasAppointment = true;
            }
        }
        
        if (!hasAppointment) {
            System.out.println("Belum ada appointment.");
        }
    }
    
    private static void completeConsultation() {
        System.out.println("\n>>> SELESAIKAN KONSULTASI (OBSERVER PATTERN) <<<");
        viewDoctorAppointments();
        
        System.out.print("\nMasukkan ID Appointment yang akan diselesaikan: ");
        String appointmentId = scanner.nextLine();
        
        Appointment appointment = db.getAppointment(appointmentId);
        if (appointment == null) {
            System.out.println("✗ Appointment tidak ditemukan!");
            return;
        }
        
        Schedule schedule = db.getSchedule(appointment.getScheduleId());
        if (schedule == null || !schedule.getDoctorId().equals(currentDoctor.getId())) {
            System.out.println("✗ Appointment ini bukan milik Anda!");
            return;
        }
        
        System.out.print("Diagnosis: ");
        String diagnosis = scanner.nextLine();
        System.out.print("Catatan: ");
        String notes = scanner.nextLine();
        
        String historyId = db.generateHistoryId();
        ConsultationHistory history = new ConsultationHistory(historyId, appointmentId, diagnosis, notes);
        db.addConsultationHistory(history);
        
        // Observer Pattern - Update status dan notifikasi
        Patient patient = db.getPatient(appointment.getPatientId());
        if (patient != null) {
            appointment.attach(patient);
        }
        appointment.attach(currentDoctor);
        
        appointment.setStatus("Selesai");
        
        System.out.println("\n✓ Konsultasi berhasil diselesaikan!");
        System.out.println("ID Riwayat: " + historyId);
        System.out.println("Data tersimpan di file histories.txt");
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka!");
            }
        }
    }
}