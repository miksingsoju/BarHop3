package app.db.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** A bookable table in a bar. Named BarTable because TABLE is a reserved SQL word. */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "bar_id", "label" }))
public class BarTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private Bar bar;

	@NotBlank
	@Size(max = 50)
	@Column(nullable = false, length = 50)
	private String label;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TableType tableType;

	@NotNull
	@Min(1)
	@Max(30)
	@Column(nullable = false)
	private Integer capacity;

	/** Minimum spend or table fee in PHP. */
	@NotNull
	@DecimalMin("0.00")
	@Digits(integer = 7, fraction = 2)
	@Column(nullable = false, precision = 9, scale = 2)
	private BigDecimal rate;

	@Column(nullable = false)
	private Boolean active = Boolean.TRUE;

	// Booking history goes with the table when a bar is deleted.
	@OneToMany(mappedBy = "table", cascade = CascadeType.REMOVE)
	private List<Reservation> reservations = new ArrayList<>();

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Bar getBar() { return bar; }
	public void setBar(Bar bar) { this.bar = bar; }
	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }
	public TableType getTableType() { return tableType; }
	public void setTableType(TableType tableType) { this.tableType = tableType; }
	public Integer getCapacity() { return capacity; }
	public void setCapacity(Integer capacity) { this.capacity = capacity; }
	public BigDecimal getRate() { return rate; }
	public void setRate(BigDecimal rate) { this.rate = rate; }
	public Boolean getActive() { return active; }
	public void setActive(Boolean active) { this.active = active; }
	public List<Reservation> getReservations() { return reservations; }
}
