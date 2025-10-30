package com.smartwings.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Flight Entity
 * Represents a flight in the airline system
 */
@Entity
@Table(name = "flights")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Flight number is required")
    @Size(max = 10, message = "Flight number must be at most 10 characters")
    @Column(name = "flight_number", unique = true, nullable = false)
    private String flightNumber;
    
    @NotBlank(message = "Airline is required")
    @Size(max = 50, message = "Airline name must be at most 50 characters")
    @Column(name = "airline", nullable = false)
    private String airline;
    
    @NotBlank(message = "Origin airport is required")
    @Size(max = 10, message = "Origin airport code must be at most 10 characters")
    @Column(name = "origin_airport", nullable = false)
    private String originAirport;
    
    @NotBlank(message = "Origin city is required")
    @Size(max = 100, message = "Origin city must be at most 100 characters")
    @Column(name = "origin_city", nullable = false)
    private String originCity;
    
    @NotBlank(message = "Destination airport is required")
    @Size(max = 10, message = "Destination airport code must be at most 10 characters")
    @Column(name = "destination_airport", nullable = false)
    private String destinationAirport;
    
    @NotBlank(message = "Destination city is required")
    @Size(max = 100, message = "Destination city must be at most 100 characters")
    @Column(name = "destination_city", nullable = false)
    private String destinationCity;
    
    @NotNull(message = "Departure time is required")
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @NotBlank(message = "Aircraft type is required")
    @Size(max = 50, message = "Aircraft type must be at most 50 characters")
    @Column(name = "aircraft_type", nullable = false)
    private String aircraftType;
    
    @NotNull(message = "Economy price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Economy price must be greater than 0")
    @Column(name = "economy_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal economyPrice;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Business price must be greater than 0")
    @Column(name = "business_price", precision = 10, scale = 2)
    private BigDecimal businessPrice;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "First class price must be greater than 0")
    @Column(name = "first_class_price", precision = 10, scale = 2)
    private BigDecimal firstClassPrice;
    
    @Min(value = 1, message = "Economy seats must be at least 1")
    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;
    
    @Min(value = 0, message = "Business seats cannot be negative")
    @Column(name = "business_seats")
    private Integer businessSeats = 0;
    
    @Min(value = 0, message = "First class seats cannot be negative")
    @Column(name = "first_class_seats")
    private Integer firstClassSeats = 0;
    
    @Column(name = "economy_available")
    private Integer economyAvailable;
    
    @Column(name = "business_available")
    private Integer businessAvailable;
    
    @Column(name = "first_class_available")
    private Integer firstClassAvailable;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightStatus status = FlightStatus.SCHEDULED;
    
    @Size(max = 10, message = "Gate must be at most 10 characters")
    @Column(name = "gate")
    private String gate;
    
    @Size(max = 10, message = "Terminal must be at most 10 characters")
    @Column(name = "terminal")
    private String terminal;
    
    @Size(max = 500, message = "Notes must be at most 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Set available seats equal to total seats initially
        economyAvailable = economySeats;
        businessAvailable = businessSeats;
        firstClassAvailable = firstClassSeats;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Flight() {}
    
    public Flight(String flightNumber, String airline, String originAirport, String originCity,
                  String destinationAirport, String destinationCity, LocalDateTime departureTime,
                  LocalDateTime arrivalTime, String aircraftType, BigDecimal economyPrice) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.originAirport = originAirport;
        this.originCity = originCity;
        this.destinationAirport = destinationAirport;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftType = aircraftType;
        this.economyPrice = economyPrice;
    }
    
    // Business methods
    public boolean hasSeatsAvailable(String travelClass, int requestedSeats) {
        switch (travelClass.toLowerCase()) {
            case "economy":
                return economyAvailable >= requestedSeats;
            case "business":
                return businessAvailable >= requestedSeats;
            case "first":
                return firstClassAvailable >= requestedSeats;
            default:
                return false;
        }
    }
    
    public BigDecimal getPriceForClass(String travelClass) {
        switch (travelClass.toLowerCase()) {
            case "economy":
                return economyPrice;
            case "business":
                return businessPrice;
            case "first":
                return firstClassPrice;
            default:
                return economyPrice;
        }
    }
    
    public void reserveSeats(String travelClass, int seats) {
        switch (travelClass.toLowerCase()) {
            case "economy":
                economyAvailable = Math.max(0, economyAvailable - seats);
                break;
            case "business":
                businessAvailable = Math.max(0, businessAvailable - seats);
                break;
            case "first":
                firstClassAvailable = Math.max(0, firstClassAvailable - seats);
                break;
        }
    }
    
    public void releaseSeats(String travelClass, int seats) {
        switch (travelClass.toLowerCase()) {
            case "economy":
                economyAvailable = Math.min(economySeats, economyAvailable + seats);
                break;
            case "business":
                businessAvailable = Math.min(businessSeats, businessAvailable + seats);
                break;
            case "first":
                firstClassAvailable = Math.min(firstClassSeats, firstClassAvailable + seats);
                break;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
    
    public String getOriginAirport() { return originAirport; }
    public void setOriginAirport(String originAirport) { this.originAirport = originAirport; }
    
    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    
    public String getDestinationAirport() { return destinationAirport; }
    public void setDestinationAirport(String destinationAirport) { this.destinationAirport = destinationAirport; }
    
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public String getAircraftType() { return aircraftType; }
    public void setAircraftType(String aircraftType) { this.aircraftType = aircraftType; }
    
    public BigDecimal getEconomyPrice() { return economyPrice; }
    public void setEconomyPrice(BigDecimal economyPrice) { this.economyPrice = economyPrice; }
    
    public BigDecimal getBusinessPrice() { return businessPrice; }
    public void setBusinessPrice(BigDecimal businessPrice) { this.businessPrice = businessPrice; }
    
    public BigDecimal getFirstClassPrice() { return firstClassPrice; }
    public void setFirstClassPrice(BigDecimal firstClassPrice) { this.firstClassPrice = firstClassPrice; }
    
    public Integer getEconomySeats() { return economySeats; }
    public void setEconomySeats(Integer economySeats) { this.economySeats = economySeats; }
    
    public Integer getBusinessSeats() { return businessSeats; }
    public void setBusinessSeats(Integer businessSeats) { this.businessSeats = businessSeats; }
    
    public Integer getFirstClassSeats() { return firstClassSeats; }
    public void setFirstClassSeats(Integer firstClassSeats) { this.firstClassSeats = firstClassSeats; }
    
    public Integer getEconomyAvailable() { return economyAvailable; }
    public void setEconomyAvailable(Integer economyAvailable) { this.economyAvailable = economyAvailable; }
    
    public Integer getBusinessAvailable() { return businessAvailable; }
    public void setBusinessAvailable(Integer businessAvailable) { this.businessAvailable = businessAvailable; }
    
    public Integer getFirstClassAvailable() { return firstClassAvailable; }
    public void setFirstClassAvailable(Integer firstClassAvailable) { this.firstClassAvailable = firstClassAvailable; }
    
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus status) { this.status = status; }
    
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    
    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightNumber, flight.flightNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(flightNumber);
    }
    
    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", flightNumber='" + flightNumber + '\'' +
                ", originCity='" + originCity + '\'' +
                ", destinationCity='" + destinationCity + '\'' +
                ", departureTime=" + departureTime +
                ", status=" + status +
                '}';
    }
}

/**
 * Flight Status Enumeration
 */
enum FlightStatus {
    SCHEDULED,
    BOARDING,
    DEPARTED,
    IN_FLIGHT,
    ARRIVED,
    DELAYED,
    CANCELLED
}